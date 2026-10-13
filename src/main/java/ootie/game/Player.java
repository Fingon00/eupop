package ootie.game;

import java.util.Objects;
import javax.annotation.Nullable;
import lombok.Getter;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.requests.restaction.ThreadChannelAction;
import ootie.discord.utility.DiscordChannelUtility;
import ootie.discord.utility.DiscordErrorUtility;
import ootie.discord.utility.DiscordThreadUtility;
import ootie.helpers.Constants;
import ootie.logging.BotLogger;
import ootie.logging.LogOrigin;
import ootie.message.MessageHelper;
import org.apache.commons.lang3.StringUtils;

public class Player extends PlayerProperties {

    private static final int EMBED_FIELD_VALUE_LIMIT = 1024;

    @Getter
    private final Game game;

    // @Getter
    // private final Map<String, Integer> actionCards = new LinkedHashMap<>();

    // @Getter
    // private final Map<String, String> trapCardsPlanets = new LinkedHashMap<>();

    // private @Getter Map<String, Integer> currentProducedUnits = new HashMap<>();

    public Player(Game game) {
        this.game = game;
    }

    public boolean is(Player p2) {
        if (p2 == null) return false;
        return getUserID().equals(p2.getUserID());
    }

    /**
     * Searches for the Player's CardsInfo ThreadChannel, or will create a new one if an existing ThreadChannel cannot be retrieved.
     * @return ThreadChannel for the player's cards info
     */
    @Nullable
    public ThreadChannel getCardsInfoThread() {
        return getCardsInfoThread(true);
    }

    @Nullable
    private ThreadChannel getCardsInfoThread(boolean createIfMissing) {
        TextChannel parentChannel = getCardsInfoThreadParentChannel();
        if (parentChannel == null) return null;

        String threadName = getCardsInfoThreadName();
        ThreadChannel foundThread = findCardsInfoThreadByIdOrName(parentChannel, threadName);

        if (foundThread != null) {
            setCardsInfoThreadID(foundThread.getId());
            return foundThread;
        }

        return createIfMissing ? createAndGreetCardsInfoThread(parentChannel, threadName) : null;
    }

    @Nullable
    public ThreadChannel createCardsInfoThread() {

        TextChannel parentChannel = getCardsInfoThreadParentChannel();
        if (parentChannel == null) return null;

        return createCardsInfoThread(parentChannel, getCardsInfoThreadName());
    }

    public void sendCardsInfoThreadGreeting(ThreadChannel thread) {
        MessageHelper.sendMessageToChannel(thread, "Hello! " + getRepresentation() + " This is your private channel.");
    }

    @Nullable
    private TextChannel getCardsInfoThreadParentChannel() {
        TextChannel parentChannel = game.getMainGameChannel();
        if (parentChannel == null && !game.isHasEnded()) {
            BotLogger.warning(
                    new LogOrigin(this),
                    "`Player.getCardsInfoThreadParentChannel`: parent channel is null for game: " + game.getName());
        }
        return parentChannel;
    }

    private String getCardsInfoThreadName() {
        String userName = getUserName().replace("/", "");
        return DiscordThreadUtility.fitThreadName(
                String.format("%s%s-%s", Constants.CARDS_INFO_THREAD_PREFIX, game.getName(), userName));
    }

    @Nullable
    private ThreadChannel findCardsInfoThreadByIdOrName(TextChannel parentChannel, String name) {
        Long id = getCardsInfoThreadIdLong();
        if (id != null) {
            ThreadChannel thread = retrieveCardsInfoThreadById(parentChannel, id);
            if (thread != null) return thread;
        }
        return DiscordChannelUtility.retrieveFirstThreadChannelByNameIgnoringCase(parentChannel, name)
                .complete();
    }

    private Long getCardsInfoThreadIdLong() {
        String idStr = getCardsInfoThreadID();
        if (StringUtils.isBlank(idStr) || "null".equals(idStr)) return null;
        try {
            return Long.valueOf(idStr);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static ThreadChannel retrieveCardsInfoThreadById(TextChannel parentChannel, Long id) {
        try {
            return DiscordChannelUtility.retrieveThreadChannelById(parentChannel.getGuild(), id)
                    .complete();
        } catch (Exception e) {
            if (DiscordErrorUtility.isUnknownChannelError(e)) {
                return null;
            }
            BotLogger.error("**CardsInfoThreadError**", e);
            return null;
            // throw e;
        }
    }

    private ThreadChannel createAndGreetCardsInfoThread(TextChannel parentChannel, String threadName) {
        ThreadChannel thread = createCardsInfoThread(parentChannel, threadName);
        sendCardsInfoThreadGreeting(thread);
        return thread;
    }

    private ThreadChannel createCardsInfoThread(TextChannel parentChannel, String threadName) {
        ThreadChannelAction action = parentChannel
                .createThreadChannel(threadName, true)
                .setAutoArchiveDuration(ThreadChannel.AutoArchiveDuration.TIME_1_WEEK);
        ThreadChannel thread = action.complete();
        setCardsInfoThreadID(thread.getId());
        return thread;
    }

    public String getRepresentation() {
        return "<@" + getUserID() + ">";
    }

    public String getRealmCheckerPrefix() {
        return factionButtonChecker();
    }

    public String factionButtonChecker() {
        return "FFCC_" + getRealm() + "_";
    }

    public String dummyPlayerSpoof() {
        return "dummyPlayerSpoof" + getRealm() + "_";
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Player that)) return false;
        return Objects.equals(getUserID(), that.getUserID());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getUserID());
    }
}
