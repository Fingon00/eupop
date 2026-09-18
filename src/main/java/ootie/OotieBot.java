package ootie;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;
import java.util.regex.Pattern;
import ootie.discord.JdaService;
import ootie.discord.buttons.ButtonProcessor;
import ootie.game.persistence.GameManager;
import ootie.game.persistence.migration.DataMigrationManager;
import ootie.logging.BotLogger;
import ootie.logging.RollbarManager;
import ootie.settings.GlobalSettings;
import ootie.spring.service.deploy.ActiveLeaseService;
import ootie.spring.service.jda.JdaLifecycleService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class OotieBot {

    public static final long START_TIME_MILLISECONDS = System.currentTimeMillis();
    public static final String INSTANCE_ID = UUID.randomUUID().toString();
    public static final String SHORT_INSTANCE_ID = INSTANCE_ID.substring(0, 8);
    private static final Pattern WHITESPACE_PATTERN = Pattern.compile("\\s+");
    private static final Duration WARMUP_DURATION = Duration.ofMinutes(2);

    private static volatile boolean shuttingDown;

    static void main(String[] args) {
        GlobalSettings.loadSettings();
        RollbarManager.init();
        BotLogger.info("\n# __BOT IS STARTING UP__");

        ConfigurableApplicationContext applicationContext = SpringApplication.run(OotieBot.class, args);
        applicationContext.getBean(JdaLifecycleService.class);
        ActiveLeaseService activeLeaseService = applicationContext.getBean(ActiveLeaseService.class);

        String[] resolvedArgs = resolveSourceArgs(args);
        JdaService.startJdaAndRegisterListeners(resolvedArgs);
        if (!JdaService.waitForJdaReadyAndInitializeGuilds(resolvedArgs)) {
            applicationContext.close();
            throw new IllegalStateException("Failed to initialize JDA and guilds");
        }
        JdaService.loadStaticDataAndResources();
        BotLogger.info("WARMING INTERACTION HANDLERS");
        ButtonProcessor.checkButtonHandlersSetup();
        BotLogger.info("FINISHED WARMING INTERACTION HANDLERS");
        activeLeaseService.beginLeaseParticipation(OotieBot::runLeaseOwnedStartupWork);
        JdaService.registerAndStartCronJobs();
        JdaService.markProcessReady();
    }

    private static void runLeaseOwnedStartupWork() {
        BotLogger.info("STARTED BACKGROUND MANAGED GAME WARMUP");
        GameManager.warmup();
        DataMigrationManager.runMigrations();
    }

    private static String[] resolveSourceArgs(String[] sourceArgs) {
        if (sourceArgs.length >= 3) {
            return sourceArgs;
        }

        // Compatibility bridge: the legacy startup path passes Discord config as
        // positional args,
        // while the Compose/docker-rollout deployment path provides the same values via
        // env vars.
        String botToken = System.getenv("DISCORD_BOT_TOKEN");
        String botUserId = System.getenv("DISCORD_BOT_USERID");
        String guildIdList = System.getenv("GUILDID_LIST");
        if (isBlank(botToken) || isBlank(botUserId) || isBlank(guildIdList)) {
            return sourceArgs;
        }

        var resolvedArgs = new ArrayList<String>();
        resolvedArgs.add(botToken);
        resolvedArgs.add(botUserId);
        resolvedArgs.addAll(Arrays.asList(WHITESPACE_PATTERN.split(guildIdList.trim())));
        return resolvedArgs.toArray(String[]::new);
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    public static void markShuttingDown() {
        shuttingDown = true;
    }

    public static boolean isUnstable() {
        return shuttingDown || isWithinStartupDuration();
    }

    private static boolean isWithinStartupDuration() {
        return System.currentTimeMillis() - START_TIME_MILLISECONDS <= WARMUP_DURATION.toMillis();
    }
}
