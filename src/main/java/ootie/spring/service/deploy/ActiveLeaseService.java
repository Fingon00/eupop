package ootie.spring.service.deploy;

import java.time.Instant;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.RequiredArgsConstructor;
import ootie.OotieBot;
import ootie.logging.BotLogger;
import ootie.spring.context.SpringContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * Owns the shared active lease and the local process state that decides whether this instance may serve traffic,
 * handle Discord interactions, mutate game state, or run scheduled work.
 */
@Service
@RequiredArgsConstructor
public class ActiveLeaseService {

    private final ActiveLeaseRepository activeLeaseRepository;
    private final LeaseProperties leaseProperties;
    private final ActiveLeaseTransactionService activeLeaseTransactionService;

    private final AtomicBoolean active = new AtomicBoolean(false);
    private final AtomicBoolean draining = new AtomicBoolean(false);
    private final AtomicBoolean ready = new AtomicBoolean(false);
    private final AtomicBoolean leaseParticipationEnabled = new AtomicBoolean(false);
    private volatile Runnable onLeaseAcquired = () -> {};

    public void beginLeaseParticipation(Runnable onLeaseAcquired) {
        this.onLeaseAcquired = Objects.requireNonNull(onLeaseAcquired);
        leaseParticipationEnabled.set(true);

        boolean acquired = tryAcquireLease(onLeaseAcquired);
        if (acquired) {
            BotLogger.info("Acquired active lease for instance " + OotieBot.INSTANCE_ID);
        } else {
            BotLogger.warning("Did not acquire active lease on startup for instance " + OotieBot.INSTANCE_ID);
        }
    }

    private boolean tryAcquireLease() {
        if (!activeLeaseTransactionService.tryAcquireLease(OotieBot.INSTANCE_ID)) {
            return false;
        }

        setActive(true);
        setDraining(false);
        onLeaseAcquired.run();
        return true;
    }

    private boolean tryAcquireLease(Runnable onLeaseAcquired) {
        if (!activeLeaseTransactionService.tryAcquireLease(OotieBot.INSTANCE_ID)) {
            return false;
        }

        setActive(true);
        setDraining(false);
        onLeaseAcquired.run();
        return true;
    }

    private void renewLease() {
        if (!isActive()) {
            return;
        }

        if (!activeLeaseTransactionService.renewLease(OotieBot.INSTANCE_ID)) {
            BotLogger.warning("Active instance lost lease ownership: " + OotieBot.INSTANCE_ID);
            setReady(false);
            setActive(false);
        }
    }

    public void releaseLease() {
        activeLeaseTransactionService.releaseLease(OotieBot.INSTANCE_ID);
        setActive(false);
    }

    public boolean stillOwnsLease() {
        Instant now = Instant.now();
        return activeLeaseRepository
                .findById(leaseProperties.getLeaseKey())
                .map(lease -> OotieBot.INSTANCE_ID.equals(lease.getInstanceId())
                        && lease.getLeaseExpiresAt() != null
                        && lease.getLeaseExpiresAt().isAfter(now))
                .orElse(false);
    }

    public boolean mayMutate() {
        return isActive() && stillOwnsLease();
    }

    public boolean isActive() {
        return active.get();
    }

    public void setActive(boolean active) {
        this.active.set(active);
        if (!active) {
            draining.set(false);
        }
    }

    public boolean isDraining() {
        return draining.get();
    }

    private void setDraining(boolean draining) {
        this.draining.set(draining);
    }

    private boolean shouldHandleDiscordInteraction() {
        return isActive() && !isDraining();
    }

    public boolean isReady() {
        return ready.get();
    }

    public void setReady(boolean ready) {
        this.ready.set(ready);
    }

    public boolean isLeaseParticipationEnabled() {
        return leaseParticipationEnabled.get();
    }

    private boolean shouldServeTraffic() {
        return isReady() && mayMutate() && !isDraining();
    }

    public boolean requestDrain() {
        if (!isActive() || isDraining()) {
            return false;
        }

        leaseParticipationEnabled.set(false);
        setDraining(true);
        setReady(false);
        BotLogger.info("Drain requested for active instance " + OotieBot.INSTANCE_ID);
        Thread.ofPlatform().name("bot-drain-shutdown").start(SpringContext::closeApplicationContext);
        return true;
    }

    private String currentProcessLogPrefix() {
        if (shouldServeTraffic()) {
            return "";
        }
        if (isDraining()) {
            return "[DRAINING " + OotieBot.SHORT_INSTANCE_ID + "] ";
        }
        if (!isActive()) {
            return "[STANDBY " + OotieBot.SHORT_INSTANCE_ID + "] ";
        }
        return "[WARMING " + OotieBot.SHORT_INSTANCE_ID + "] ";
    }

    @Scheduled(fixedDelayString = "#{@leaseProperties.heartbeatIntervalMillis}")
    void maintainLease() {
        if (isDraining()) {
            return;
        }

        if (isActive()) {
            renewLease();
            return;
        }

        if (!leaseParticipationEnabled.get()) {
            return;
        }

        if (tryAcquireLease()) {
            setReady(true);
            BotLogger.info("Inactive instance acquired active lease: " + OotieBot.INSTANCE_ID);
        }
    }

    public static boolean isCurrentProcessReady() {
        try {
            return SpringContext.getBean(ActiveLeaseService.class).isReady();
        } catch (IllegalStateException e) {
            return false;
        }
    }

    public static void setCurrentProcessReady(boolean ready) {
        try {
            SpringContext.getBean(ActiveLeaseService.class).setReady(ready);
        } catch (IllegalStateException e) {
            // Spring not initialized yet; ignore.
        }
    }

    public static boolean shouldHandleCurrentProcessInteraction() {
        try {
            return SpringContext.getBean(ActiveLeaseService.class).shouldHandleDiscordInteraction();
        } catch (IllegalStateException e) {
            return false;
        }
    }

    public static boolean shouldCurrentProcessServeTraffic() {
        try {
            return SpringContext.getBean(ActiveLeaseService.class).shouldServeTraffic();
        } catch (IllegalStateException e) {
            return false;
        }
    }

    // TODO: Can we avoid littering these throughout the code?
    public static boolean shouldCurrentProcessRunScheduledWork() {
        try {
            ActiveLeaseService activeLeaseService = SpringContext.getBean(ActiveLeaseService.class);
            return activeLeaseService.mayMutate() && !activeLeaseService.isDraining();
        } catch (IllegalStateException e) {
            return false;
        }
    }

    public static String getCurrentProcessLogPrefix() {
        try {
            return SpringContext.getBean(ActiveLeaseService.class).currentProcessLogPrefix();
        } catch (IllegalStateException e) {
            return "[STARTUP] ";
        }
    }
}
