package ootie.discord.listeners.context;

import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.entities.Message.Interaction;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import ootie.discord.JdaService;
import ootie.game.Game;
import ootie.game.Player;
import ootie.game.persistence.GameManager;
import ootie.helpers.Constants;
import ootie.logging.BotLogger;
import ootie.service.game.GameNameService;
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
        return "showGameAgain".equalsIgnoreCase(componentID);
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
        String message = "To these buttons are for someone else";
    }

    public void save() {
        if (game != null) {
            GameManager.save(game, "eh");
        }
    }
}
