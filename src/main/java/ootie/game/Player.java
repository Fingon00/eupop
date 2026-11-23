package ootie.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nullable;
import lombok.Getter;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.requests.restaction.ThreadChannelAction;
import net.dv8tion.jda.api.utils.FileUpload;
import ootie.discord.commands.search.SearchImageHelper;
import ootie.discord.utility.DiscordChannelUtility;
import ootie.discord.utility.DiscordErrorUtility;
import ootie.discord.utility.DiscordThreadUtility;
import ootie.helpers.Constants;
import ootie.image.Mapper;
import ootie.logging.BotLogger;
import ootie.logging.LogOrigin;
import ootie.message.MessageHelper;
import ootie.model.AreaModel;
import ootie.model.ProvinceModel;
import ootie.service.bot.BotConvertActionService;
import ootie.service.image.FileUploadService;
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

    public List<String> getAllProvinces() {
        List<String> allProvinces = new ArrayList<>();
        allProvinces.addAll(getSmallProvinces());
        allProvinces.addAll(getLargeProvinces());
        return allProvinces;
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

    public void spendBotPower(int power) {
        setBotPower(getBotPower() - power);
        setSpentBotPower(getSpentBotPower() + power);
        MessageHelper.sendMessageToChannel(
                getCardsInfoThreadParentChannel(), getUserName() + " spent " + power + " bot power.");
    }

    public String drawBotCard() {
        String botCard = getBotDeck().getFirst();
        FileUpload fileUpload =
                FileUploadService.createFileUpload(SearchImageHelper.getImage("images/Bot Cards/", botCard), "botcard");
        MessageHelper.sendMessageWithFile(
                getCardsInfoThreadParentChannel(), fileUpload, getUserName() + " drew this bot card:", false);
        getBotDeckDiscard().add(botCard);
        return botCard;
    }

    public void scorePrestige(int prestige, String reason) {
        setPrestige(getPrestige() + prestige);
        String announcement =
                getUserName() + " scored " + prestige + " prestige (now at " + getPrestige() + " total prestige)";
        if (reason != null) {
            announcement += " due to " + reason;
        } else {
            announcement += ".";
        }
        MessageHelper.sendMessageToChannel(getCardsInfoThreadParentChannel(), announcement);
    }

    public void gainDucats(int ducats, String reason) {
        setDucats(getDucats() + ducats);
        String announcement = getUserName() + " gained " + ducats + " ducats (now at " + getDucats() + " total ducats)";
        if (reason != null) {
            announcement += " due to " + reason;
        } else {
            announcement += ".";
        }
        MessageHelper.sendMessageToChannel(getCardsInfoThreadParentChannel(), announcement);
    }

    public List<String> getCoreableAreas() {
        List<String> areas = new ArrayList<>();
        for (String province : getAllProvinces()) {
            AreaModel area = BotConvertActionService.getAreaByProvince(province);
            if (area.getProvinces().size() == 1) {
                continue;
            }
            if (getCoredAreas().contains(area.getAlias())) {
                continue;
            }
            boolean allOwned = true;
            boolean allCore = true;
            for (String prov : area.getProvinces()) {
                if (!getAllProvinces().contains(prov)) {
                    allOwned = false;
                }
                ProvinceModel pro = Mapper.getProvinces().get(prov);
                if (!pro.getRealm1444().equalsIgnoreCase(getRealm())) {
                    allCore = false;
                }
            }
            if (allOwned && !allCore) {
                areas.add(area.getId());
            }
        }

        return areas;
    }

    public int getTaxValueOfArea(String area) {
        AreaModel aM = Mapper.getAreas().get(area);
        int taxValue = 0;
        for (String prov : aM.getProvinces()) {
            if (getLargeProvinces().contains(prov)) {
                taxValue += 2;
            } else {
                taxValue += 1;
            }
        }

        return taxValue;
    }

    public void gainBotPower(int botPower, String reason) {
        setBotPower(getBotPower() + botPower);
        String announcement =
                getUserName() + " gained " + botPower + " bot power (now at " + getBotPower() + " unused bot power)";
        if (reason != null) {
            announcement += " due to " + reason;
        } else {
            announcement += ".";
        }
        MessageHelper.sendMessageToChannel(getCardsInfoThreadParentChannel(), announcement);
    }

    public void completeMilestone(String milestone) {

        MessageHelper.sendMessageToChannel(
                getCardsInfoThreadParentChannel(), getUserName() + " completed the " + milestone + " milestone.");
        int prestigeToScore = 0;
        int numOfPrevPlayers =
                getGame().getPlayersWhoCompletedMilestone(milestone).size();
        if (numOfPrevPlayers == 0) {
            prestigeToScore = 5;
        }
        if (numOfPrevPlayers == 1) {
            prestigeToScore = 3;
        }
        if (numOfPrevPlayers == 1) {
            prestigeToScore = 1;
        }
        if (prestigeToScore > 0) {
            scorePrestige(prestigeToScore, "completing the " + milestone + " milestone.");
        }
        getCompletedMilestones().add(milestone);
    }

    public void researchIdea(String idea) {
        MessageHelper.sendMessageToChannel(
                getCardsInfoThreadParentChannel(), getUserName() + " researched the " + idea + " idea.");
        List<Player> players = getGame().getPlayersWhoResearchedIdea(idea);
        if (players.isEmpty()) {
            scorePrestige(2, "researching the " + idea + " idea.");
        } else {
            if (players.size() < 3) {
                for (Player p2 : players) {
                    p2.scorePrestige(1, "someone else researching the " + idea + " idea");
                }
            }
        }

        getIdeas().add(idea);
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
    public TextChannel getCardsInfoThreadParentChannel() {
        TextChannel parentChannel = game.getMainGameChannel();
        if (parentChannel == null && !game.isHasEnded()) {
            BotLogger.warning(
                    new LogOrigin(this),
                    "`Player.getCardsInfoThreadParentChannel`: parent channel is null for game: " + game.getName());
        }
        return parentChannel;
    }

    @Nullable
    public TextChannel getActionsChannel() {
        return getCardsInfoThreadParentChannel();
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
