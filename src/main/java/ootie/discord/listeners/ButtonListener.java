package ootie.discord.listeners;

import java.time.Duration;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicLong;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import ootie.OotieBot;
import ootie.discord.JdaService;
import ootie.discord.buttons.ButtonProcessor;
import ootie.logging.BotLogger;
import ootie.spring.service.deploy.ActiveLeaseService;
import org.apache.commons.lang3.function.Consumers;

class ButtonListener extends ListenerAdapter {

    private static final Set<String> BUTTONS_TO_THINK_ABOUT = Set.of("showGameAgain", "bothelperDashboard_manageRoles");

    private static ButtonListener instance;

    public static ButtonListener getInstance() {
        if (instance == null) instance = new ButtonListener();
        return instance;
    }

    @Override
    public void onButtonInteraction(@Nonnull ButtonInteractionEvent event) {
        EventLatencyChecker.check(event);
        if (!ActiveLeaseService.shouldHandleCurrentProcessInteraction()) {
            return;
        }
        if (!JdaService.isReadyToReceiveCommands()) {
            event.reply("You pressed something. Please try again in a few minutes. The bot is rebooting.")
                    .setEphemeral(true)
                    .queue(Consumers.nop(), BotLogger::catchRestError);
            return;
        }

        // Only defer if button does not spawn a Modal
        if (!isModalSpawner(event)) {
            event.deferReply(true).queue(Consumers.nop(), BotLogger::catchRestError);
        }

        ButtonProcessor.queue(event);
    }

    /**
     * @return whether a button should show the bot is thinking - need to add the
     *         following at end of execution:
     *         ` if (event instanceof ButtonInteractionEvent buttonEvent) {
     *         buttonEvent.getHook().deleteOriginal().queue(Consumers.nop(),
     *         BotLogger::catchRestError);
     *         }`
     */

    /**
     * @return whether the button spawns a Modal - modals must be a raw undeferred
     *         reply
     */
    private static boolean isModalSpawner(ButtonInteractionEvent event) {
        return event.getButton().getCustomId().contains("~MDL");
    }

    private static final class EventLatencyChecker {

        private static final long THRESHOLD_MS = 2000;
        private static final Duration WARNING_COOLDOWN_WINDOW = Duration.ofMinutes(5);
        private static final Duration WARNING_TRIM_WINDOW = Duration.ofMinutes(1);
        private static final int EVENT_COUNT_THRESHOLD = 15;

        private static final ConcurrentLinkedDeque<Long> slowEvents = new ConcurrentLinkedDeque<>();
        private static final AtomicLong lastWarningTimeMs = new AtomicLong(0);

        static void check(GenericInteractionCreateEvent event) {
            if (OotieBot.isUnstable()) return;

            long now = System.currentTimeMillis();
            long lastWarning = lastWarningTimeMs.get();

            if (now - lastWarning < WARNING_COOLDOWN_WINDOW.toMillis()) {
                return;
            }

            long eventTimeMs = event.getTimeCreated().toInstant().toEpochMilli();
            long latencyMs = now - eventTimeMs;

            if (latencyMs <= THRESHOLD_MS) {
                return;
            }

            slowEvents.addLast(now);

            long windowMs = WARNING_TRIM_WINDOW.toMillis();
            while (!slowEvents.isEmpty() && now - slowEvents.getFirst() > windowMs) {
                slowEvents.removeFirst();
            }

            if (slowEvents.size() <= EVENT_COUNT_THRESHOLD) {
                return;
            }

            if (!lastWarningTimeMs.compareAndSet(lastWarning, now)) {
                return;
            }

            slowEvents.clear();

            long gatewayPing = event.getJDA().getGatewayPing();
            long warningWindowMinutes = WARNING_TRIM_WINDOW.toMinutes();
            String minuteLabel = warningWindowMinutes == 1 ? "minute" : "minutes";
            BotLogger.error("⚠ **High Discord/JDA latency detected: "
                    + EVENT_COUNT_THRESHOLD
                    + "+ slow events in the last "
                    + warningWindowMinutes + " " + minuteLabel + ".**"
                    + "\n**Gateway ping:** " + gatewayPing);
        }
    }
}
