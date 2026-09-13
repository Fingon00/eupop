package ootie.discord.listeners.context;

import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.Interaction;
import net.dv8tion.jda.api.callbacks.IReplyCallback;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import ootie.discord.JdaService;
import ootie.discord.commands.CommandHelper;
import ootie.game.Game;
import ootie.game.Player;
import ootie.game.persistence.GameManager;
import ootie.helpers.Constants;
import ootie.logging.BotLogger;
import ootie.message.MessageHelper;
import ootie.service.GameNameService;
import ootie.service.event.EventAuditService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.function.Consumers;

@Getter
public abstract class ListenerContext {

    private final long creationStartTime;
    private final long creationEndTime;
    protected boolean contextIsValid = true;
    protected final String origComponentID;
    protected String componentID;
    protected boolean factionChecked;
    protected final Game game;
    protected Player player;
    protected MessageChannel privateChannel, mainGameChannel, actionsChannel;
    final GenericInteractionCreateEvent event;

    @Setter
    protected boolean shouldSave = true;

    public abstract GenericInteractionCreateEvent getEvent();

    protected abstract String getContextType();

    public boolean isValid() {
        return contextIsValid;
    }

    private boolean allowsNonPlayerInteraction() {
        return "showGameAgain".equalsIgnoreCase(componentID)
                || componentID.startsWith(CombatSideBetButtonIds.PREFIX)
                || componentID.startsWith(CombatDoubleOrBustButtonIds.PREFIX);
    }

    ListenerContext(GenericInteractionCreateEvent event, String compID) {
        creationStartTime = System.currentTimeMillis();

        this.event = event;
        componentID = origComponentID = compID;

        String gameName = GameNameService.getGameNameFromChannel(event);
        game = GameManager.isValid(gameName)
                ? GameManager.getManagedGame(gameName).getGame()
                : null;
        player = null;
        privateChannel = event.getMessageChannel();
        mainGameChannel = event.getMessageChannel();

        if (game != null) {
            String userID = event.getUser().getId();
            player = CommandHelper.getPlayerFromGame(game, event.getMember(), userID);

            if (player == null && !allowsNonPlayerInteraction()) {
                String message = event.getUser().getAsMention() + " is not a player of the game";
                if (event instanceof IReplyCallback replyCallback) {
                    if (replyCallback.isAcknowledged()) {
                        replyCallback
                                .getHook()
                                .sendMessage(message)
                                .setEphemeral(true)
                                .queue(Consumers.nop(), BotLogger::catchRestError);
                    } else {
                        replyCallback
                                .reply(message)
                                .setEphemeral(true)
                                .queue(Consumers.nop(), BotLogger::catchRestError);
                    }
                } else {
                    event.getMessageChannel().sendMessage(message).queue(Consumers.nop(), BotLogger::catchRestError);
                }
                contextIsValid = false;
                creationEndTime = System.currentTimeMillis();
                return;
            }

            if ("button".equals(getContextType())) {
                game.increaseButtonPressCount();
            }

            if (game.isFowMode()) {
                if (player != null && player.isRealPlayer() && player.getPrivateChannel() == null) {
                    MessageHelper.sendMessageToChannel(
                            event.getMessageChannel(),
                            "Private channels are not set up for this game. Messages will be suppressed.");
                    privateChannel = null;
                } else if (player != null) {
                    privateChannel = player.getPrivateChannel();
                }
            }

            if (game.getMainGameChannel() != null) {
                mainGameChannel = game.getMainGameChannel();
            }

            if (componentID.contains("dummyPlayerSpoof")) {
                String identity = StringUtils.substringBefore(componentID, "_").replace("dummyPlayerSpoof", "");
                player = game.getPlayerFromColorOrFaction(identity);
                componentID = componentID.replace("dummyPlayerSpoof" + identity + "_", "");
            }

            if (player != null
                    && game.getActivePlayerID() != null
                    && player.getUserID().equalsIgnoreCase(game.getActivePlayerID())) {
                AutoPingMetadataManager.delayPing(gameName);
            }
        }

        if (!checkFinsFactionChecker()) {
            contextIsValid = false;
            creationEndTime = System.currentTimeMillis();
            return;
        }

        actionsChannel = null;
        for (TextChannel textChannel_ : JdaService.jda.getTextChannels()) {
            if (textChannel_.getName().equals(gameName + Constants.ACTIONS_CHANNEL_SUFFIX)) {
                actionsChannel = textChannel_;
                break;
            }
        }
        creationEndTime = System.currentTimeMillis();
    }

    private boolean checkFinsFactionChecker() {
        GenericInteractionCreateEvent event = getEvent();
        if (factionChecked || componentID == null || !componentID.startsWith("FFCC_")) {
            return true;
        }
        componentID = componentID.replace("FFCC_", "");
        String factionWhoPressedButton = player == null ? "nullPlayer" : player.getFaction();

        if (player != null
                && !componentID.startsWith(factionWhoPressedButton + "_")
                && (!componentID.contains("firmament_") || !factionWhoPressedButton.contains("obsidian"))) {
            handlePlayerHittingButtonTheyDoNotOwn(event);
            return false;
        }
        if (componentID.contains("firmament_") && factionWhoPressedButton.contains("obsidian")) {
            factionWhoPressedButton = "firmament";
        }
        componentID = componentID.replaceFirst(factionWhoPressedButton + "_", "");
        factionChecked = true;
        return true;
    }

    private void handlePlayerHittingButtonTheyDoNotOwn(Interaction event) {
        String message = "To " + player.fogSafeEmoji() + ": these buttons are for someone else";
        MessageHelper.sendMessageToChannel(event.getMessageChannel(), message);
    }

    public void save() {
        if (game != null) {
            GameManager.save(game, EventAuditService.getReason(getEvent()));
        }
    }
}
