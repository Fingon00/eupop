package ootie.discord;

import jakarta.annotation.Nullable;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import ootie.OotieBot;
import ootie.discord.commands.SlashCommandManager;
import ootie.discord.listeners.ListenerManager;
import ootie.executors.ExecutorServiceManager;
import ootie.executors.ExecutorUtility;
import ootie.executors.ShutdownResult;
import ootie.helpers.Constants;
import ootie.helpers.Storage;
import ootie.image.MapRenderPipeline;
import ootie.image.Mapper;
import ootie.logging.BotLogger;
import ootie.logging.LogBufferManager;
import ootie.settings.GlobalSettings;
import ootie.spring.context.SpringContext;
import ootie.spring.service.deploy.ActiveLeaseService;
import org.apache.commons.lang3.function.Consumers;

@UtilityClass
public class JdaService {

    private static final String JDA_EVENT_POOL_NAME = "JDA Event Pool";
    private static final int EVENT_POOL_SHUTDOWN_TIMEOUT_SECONDS = 5;
    private static final int JDA_SHUTDOWN_TIMEOUT_SECONDS = 20;
    private static final Set<CacheFlag> DISABLED_JDA_CACHE_FLAGS = EnumSet.of(
            // User is playing a game, listening to Spotify, etc.
            CacheFlag.ACTIVITY,
            // User on Desktop, Mobile, or Web? Could be useful for stats
            CacheFlag.CLIENT_STATUS,
            // User is online, idle, etc
            CacheFlag.ONLINE_STATUS,
            // Needed for Role.getTags()
            CacheFlag.ROLE_TAGS,
            CacheFlag.SCHEDULED_EVENTS,
            CacheFlag.SOUNDBOARD_SOUNDS,
            CacheFlag.STICKER,
            CacheFlag.VOICE_STATE);

    // TODO:
    // we may not want to trust any old "Admin" role on a server
    // should actually have admin rights
    public static final Set<Role> adminRoles = new HashSet<>();
    public static final Set<Role> developerRoles = new HashSet<>();
    public static final Set<Role> bothelperRoles = new HashSet<>();

    public static JDA jda;
    public static String guildPrimaryID;
    public static boolean testingMode;
    public static Guild guildPrimary;
    public static Guild guildFogOfWar;
    private static Guild guildFogOfWarSecondary;
    public static Guild guildCommunityPlays;
    private static Guild guildMegagame;
    private static Guild guildTourney;
    public static final Set<Guild> guilds = new HashSet<>();
    public static final Set<Guild> serversToCreateNewGamesOn = new HashSet<>();
    public static final Set<Guild> fowServers = new HashSet<>();

    private static final ExecutorService EVENT_EXECUTOR = Executors.newFixedThreadPool(
            Runtime.getRuntime().availableProcessors(),
            Thread.ofPlatform().name("ootie-jda-event-", 0).factory());

    public static void startJdaAndRegisterListeners(String[] args) {
        BotLogger.info("STARTING JDA");
        jda = JDABuilder.createDefault(args[0])
                .setEventPool(EVENT_EXECUTOR)
                .enableIntents(
                        // Needed to listen for joins/leaves
                        // Needed to cache all members of a guild (including chunking) - remove?
                        GatewayIntent.GUILD_MEMBERS,
                        // Needed to parse raw user messages
                        GatewayIntent.MESSAGE_CONTENT,
                        // Needed for emoji searches and validation
                        GatewayIntent.GUILD_EXPRESSIONS)
                // It *appears* we need to pull all members or else the bot has trouble pinging
                // players
                // but that may be a misunderstanding, in case we want to try to use an LRU
                // cache in the future
                // and avoid loading every user at startup
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .setChunkingFilter(ChunkingFilter.ALL)
                .disableCache(DISABLED_JDA_CACHE_FLAGS)
                // This allows us to use our own ShutdownHook, created below
                .setEnableShutdownHook(false)
                .build();

        BotLogger.info("INITIALIZING LISTENERS");
        ListenerManager.registerListeners(jda);
    }

    public static boolean waitForJdaReadyAndInitializeGuilds(String[] args) {
        BotLogger.info("AWAITING JDA READY");
        try {
            jda.awaitReady();
        } catch (Throwable t) {
            BotLogger.critical("Error waiting for bot to get ready", t);
            return false;
        }

        jda.getPresence()
                .setPresence(OnlineStatus.DO_NOT_DISTURB, Activity.customStatus("STARTING UP: Connecting to Servers"));

        BotLogger.info("INITIALIZING SERVERS");

        // Primary HUB Server
        guildPrimaryID = args[2];
        tryToInitGuild(args[2], false);

        if (guildPrimary == null) {
            BotLogger.critical("Failed to start the bot on the primary guild. Aborting.");
            return false;
        } else {
            // if (args.length >= 4) {
            //     guildCommunityPlays = tryToInitGuild(args[3], false);
            // }
        }

        BotLogger.info("FINISHED INITIALIZING SERVERS\n> "
                + guilds.size() + " total servers connected\n> "
                + "\n> Guilds: " + jda.getGuilds().stream().map(Guild::getName).collect(Collectors.toSet()));

        // Attempt to start a "Search Only" version of the bot on eligible servers
        for (Guild searchGuild : jda.getGuilds()) {
            // if (guilds.stream().anyMatch(g -> g.getId().equals(searchGuild.getId()))) continue;
            if ("567337533462151178".equalsIgnoreCase(searchGuild.getId())) {
                startBotSearchOnly(searchGuild);
            }
        }

        // Check for and report a missing bot-log webhook
        if (!GlobalSettings.settingExists(GlobalSettings.ImplementedSettings.BOT_LOG_WEBHOOK_URL)) {
            BotLogger.warning(
                    "BOT-LOG WEBHOOK NOT FOUND for Primary GuildID:" + guildPrimaryID
                            + "\nPlease set a valid bot-log Webhook URL using `/developer setting setting_name:bot_log_webhook_url setting_type:string setting_value:<url>`");
        }
        return true;
    }

    public static void loadStaticDataAndResources() {
        BotLogger.info("LOADING DATA");
        jda.getPresence().setActivity(Activity.customStatus("STARTING UP: Loading Data"));

        // load all /resources/data/ .json and .properties files, except
        // logging.properties, each into 1 HashMap or
        // Properties
        Mapper.init();
        // create directories for games files
        Storage.init();
    }

    public static void registerAndStartCronJobs() {
        // AutoPingCron.register();
        // PersistToSqlCron.register();
    }

    public static void markProcessReady() {
        ActiveLeaseService.setCurrentProcessReady(true);
        BotLogger.info("BOT IS READY TO RECEIVE COMMANDS");
    }

    private static Guild tryToInitGuild(String guildID, boolean addToNewGameServerList) {
        try {
            return initGuild(guildID, addToNewGameServerList);
        } catch (Throwable t) {
            BotLogger.critical("Failed to initialize guild " + guildID + ". Skipping.", t);
            return null;
        }
    }

    private static Guild initGuild(String guildID, boolean addToNewGameServerList) {
        if (!guildID.matches("\\b[0-9]+\\b")) {
            BotLogger.error(
                    "Invalid Guild ID provided: `" + guildID
                            + "` - If this is running in Production, please correct the ID [here](https://github.com/Asyncootie/ootie_map_generator_bot/settings/variables/actions/GUILDID_LIST)");
            return null;
        }
        Guild guild = jda.getGuildById(guildID);
        if (guild == null) {
            BotLogger.error("JDA FAILED TO FIND GUILD with ID: `" + guildID
                    + "` - please ensure Asyncootie is added to that server and has Admin permissions.");
            return null;
        }
        if (!startBot(guild)) {
            BotLogger.error("Failed to start bot for guild: " + guild.getName());
            return null;
        }
        if (addToNewGameServerList) {
            serversToCreateNewGamesOn.add(guild);
        }
        return guild;
    }

    private static boolean startBot(Guild guild) {
        if (guild == null) {
            return false;
        }
        if (guildPrimaryID.equals(guild.getId())) {
            guildPrimary = guild;
            BotLogger.init(); // requires guildPrimary bot-log channel existing
        }
        try {
            CommandListUpdateAction commands = guild.updateCommands();
            SlashCommandManager.getCommands().forEach(command -> command.register(commands));
            // ContextCommandManager.getCommands().forEach(cmd -> cmd.register(commands));
            commands.queue(Consumers.nop(), BotLogger::catchRestError);
            BotLogger.info("BOT STARTED UP: " + guild.getName());
            guilds.add(guild);
        } catch (Exception e) {
            BotLogger.error("\n# FAILED TO START BOT ", e);
            return false;
        }
        return true;
    }

    private static boolean startBotSearchOnly(Guild guild) {
        // Do not set up search commands for test bots, and definitely never for the hub
        // server, which several test bots
        // are still in
        if (guild == null) return false;
        // if (System.getenv("TESTING") != null) return false;
        if (Constants.ASYNCOOTIE_HUB_SERVER_ID.equals(guild.getId())) return false;

        // Disable this for now

        try {
            CommandListUpdateAction commands = guild.updateCommands();
            SlashCommandManager.getCommands().forEach(command -> command.registerSearchCommands(commands));
            commands.queue(Consumers.nop(), BotLogger::catchRestError);
            BotLogger.info("SEARCH-ONLY BOT STARTED UP: " + guild.getName());
            guilds.add(guild);
        } catch (Exception e) {
            BotLogger.error("\n# SEARCH-ONLY BOT FAILED TO START: " + guild.getName(), e);
        }
        return true;
    }

    public static void updatePresence() {
        jda.getPresence().setPresence(OnlineStatus.ONLINE, Activity.playing("Beep Boop"));
    }

    /**
     * Initializes the whitelisted roles for the bot, including admin, developer,
     * and bothelper roles.
     * <ul>
     * <li>Admins may execute /admin, /developer, and /bothelper commands</li>
     * <li>Developers may execute /developer commands</li>
     * <li>Bothelpers may execute /bothelper commands</li>
     * </ul>
     *
     * Add your test server's role ID to enable access to these commands on your
     * server
     */
    private static void initializeWhitelistedRoles() {
        // ADMIN ROLES
        adminRoles.add(jda.getRoleById("943596173896323072")); // Async Primary (Hub)

        adminRoles.removeIf(Objects::isNull);

        // DEVELOPER ROLES

        developerRoles.addAll(adminRoles); // admins may also execute developer commands
        developerRoles.add(jda.getRoleById("947648366056185897")); // Async Primary (Hub)

        developerRoles.removeIf(Objects::isNull);

        // BOTHELPER ROLES

        bothelperRoles.addAll(developerRoles); // developers may also execute bothelper commands
        bothelperRoles.addAll(adminRoles); // admins can also execute bothelper commands
        bothelperRoles.add(jda.getRoleById("1166011604488425482")); // Async Primary (Hub)

        bothelperRoles.removeIf(Objects::isNull);
    }

    public static String getBotId() {
        return jda.getSelfUser().getId();
    }

    public static boolean isReadyToReceiveCommands() {
        return ActiveLeaseService.isCurrentProcessReady();
    }

    public static List<Category> getAvailablePBDCategories() {
        return guilds.stream()
                .flatMap(guild -> guild.getCategories().stream())
                .filter(category -> category.getName().toUpperCase().startsWith("PBD #"))
                .toList();
    }

    public static boolean isValidGuild(String guildId) {
        return guilds.stream().anyMatch(g -> g.getId().equals(guildId));
    }

    @Nullable
    public static String getUsername(String userId) {
        Member member = guildPrimary.getMemberById(userId);
        if (member != null) return member.getEffectiveName();
        User user = jda.getUserById(userId);
        if (user != null) return user.getEffectiveName();
        return null;
    }

    private static void leaveNonWhitelistedGuilds() {
        jda.getGuilds().forEach(JdaService::leaveGuildIfNotWhitelisted);
    }

    public static void leaveGuildIfNotWhitelisted(Guild guild) {
        if (!isProduction() || isWhitelistedGuild(guild)) return;
        BotLogger.warning(
                "Leaving guild '" + guild.getName() + "' (" + guild.getId() + ") because it isn't whitelisted!");
        guild.leave().queue(Consumers.nop(), BotLogger::catchRestError);
    }

    public static boolean isProduction() {
        return guildPrimaryID.equals(Constants.ASYNCOOTIE_HUB_SERVER_ID);
    }

    private static boolean isWhitelistedGuild(Guild guild) {
        return guilds.stream().anyMatch(whitelistGuild -> whitelistGuild.getId().equals(guild.getId()))
                || Constants.EMOJI_FARM_SERVERS.containsKey(guild.getId());
    }

    public static void shutdown() {
        try {
            OotieBot.markShuttingDown();

            jda.getPresence().setPresence(OnlineStatus.DO_NOT_DISTURB, Activity.customStatus("BOT IS SHUTTING DOWN"));
            BotLogger.info("SHUTDOWN PROCESS STARTED");

            ActiveLeaseService.setCurrentProcessReady(false);
            BotLogger.info("NO LONGER ACCEPTING COMMANDS");

            logShutdownResult(JDA_EVENT_POOL_NAME, shutdownEventExecutor());
            logShutdownResult(ExecutorServiceManager.class.getSimpleName(), ExecutorServiceManager.shutdown());
            // logShutdownResult(CronManager.class.getSimpleName(), CronManager.shutdown());
            logShutdownResult(MapRenderPipeline.class.getSimpleName(), MapRenderPipeline.shutdown());

            SpringContext.getBean(ActiveLeaseService.class).releaseLease();
            BotLogger.info("RELEASED ACTIVE LEASE");

            BotLogger.info("SHUTTING DOWN JDA.");

            LogBufferManager.sendBufferedLogsToDiscord();

            shutdownJda();
        } catch (Exception e) {
            BotLogger.error("Error encountered within shutdown process:\n> ", e);
        }
    }

    private static ShutdownResult shutdownEventExecutor() {
        return ExecutorUtility.shutdownAndAwaitTermination(
                EVENT_EXECUTOR, EVENT_POOL_SHUTDOWN_TIMEOUT_SECONDS, TimeUnit.SECONDS);
    }

    private static void logShutdownResult(String executorName, ShutdownResult result) {
        switch (result) {
            case GRACEFUL_TERMINATION -> BotLogger.info(executorName + " terminated gracefully.");
            case FORCED_TERMINATION -> BotLogger.info(executorName + " terminated after interrupt request.");
            case TIMED_OUT -> BotLogger.info(executorName + " did not terminate before the shutdown timeout.");
            case INTERRUPTED -> BotLogger.info(executorName + " shutdown was interrupted.");
        }
    }

    private static void shutdownJda() {
        jda.shutdown();
        try {
            if (!jda.awaitShutdown(JDA_SHUTDOWN_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
                jda.shutdownNow();
                jda.awaitShutdown(JDA_SHUTDOWN_TIMEOUT_SECONDS, TimeUnit.SECONDS);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            jda.shutdownNow();
        }
    }
}
