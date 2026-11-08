package ootie.service.scenario;

import java.util.ArrayList;
import java.util.List;
import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.components.tree.MessageComponentTree;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import ootie.discord.buttons.Buttons;
import ootie.discord.routing.ButtonHandler;
import ootie.game.Game;
import ootie.game.Player;
import ootie.helpers.ScenarioHelper;
import ootie.image.Mapper;
import ootie.message.MessageHelper;
import ootie.model.ScenarioModel;
import ootie.model.SetupCardModel;
import ootie.service.player.PlayerSetupService;

@UtilityClass
public class StartScenarioService {

    @ButtonHandler("startScenario")
    public static void startScenario(Game game, String buttonID) {
        String id = buttonID.split("_")[1];
        ScenarioModel mod = Mapper.getScenarios().get(id);
        game.setScenario(id);
        for (String realm : mod.getRealms()) {
            game.setStoredValue(realm + "State", "bot");
            MessageHelper.sendMessageToChannel(
                    game.getMainGameChannel(), getStateMessage(game, realm), getStateButtons(game, realm));
        }

        List<Button> buttons = new ArrayList<>();
        buttons.add(Buttons.gray("launchScenario", "Finish Setup & Start Game"));
        MessageHelper.sendMessageToChannel(
                game.getMainGameChannel(),
                "Once you have decided which realms will be played by bots or players, use this button to start the game.",
                buttons);
    }

    @ButtonHandler("launchScenario")
    public static void launchScenario(Game game, String buttonID, ButtonInteractionEvent event) {
        event.getMessage().delete().queue();
        String id = game.getScenario();
        ScenarioModel mod = Mapper.getScenarios().get(id);
        for (String realm : mod.getRealms()) {
            String color = getPrefferedColor(realm);
            if (game.getPlayerByRealmOrColor(color) != null) {
                List<String> colors = List.of("Purple", "Yellow", "White", "Teal", "Blue", "Red");
                for (String col : colors) {
                    if (game.getPlayerByRealmOrColor(col) == null) {
                        color = col;
                        break;
                    }
                }
            }
            Player player = null;
            if (game.getStoredValue(realm + "State").isEmpty()
                    || "bot".equalsIgnoreCase(game.getStoredValue(realm + "State"))) {
                String botUserID = "botUser";
                for (int x = 1; x < 100; x++) {
                    if (game.getPlayer(botUserID + x) == null) {
                        game.addPlayer(botUserID + x, "Bot User " + x);
                        player = game.getPlayer(botUserID + x);
                        player.setBot(true);
                        break;
                    }
                }
            } else {
                player = game.getPlayer(game.getStoredValue(realm + "State"));
            }
            String setupCardID = "";
            for (SetupCardModel setup : Mapper.getSetupCards().values()) {
                if (setup.getRealm().equalsIgnoreCase(realm)) {
                    if ((mod.getStartingAge() == 1 || mod.getStartingAge() == 2)
                            && setup.getYear().equalsIgnoreCase("1444")) {
                        setupCardID = setup.getId();
                        break;
                    }
                    if ((mod.getStartingAge() == 3 || mod.getStartingAge() == 4)
                            && setup.getYear().equalsIgnoreCase("1618")) {
                        setupCardID = setup.getId();
                        break;
                    }
                }
            }
            PlayerSetupService.setupPlayer(color, player, game, event, setupCardID, player.isBot());
        }
        ScenarioHelper.setupScenarioDecks(game, id);
    }

    public static String getPrefferedColor(String realm) {
        switch (realm) {
            case "Autria" -> {
                return "White";
            }
            case "Castile", "Spain" -> {
                return "Yellow";
            }
            case "England" -> {
                return "Red";
            }
            case "France" -> {
                return "Blue";
            }
            case "Denmark" -> {
                return "Purple";
            }
            default -> {
                return "Purple";
            }
        }
    }

    public String getStateMessage(Game game, String realm) {
        String message = realm + " will currently be played by ";
        if (game.getStoredValue(realm + "State").isEmpty()
                || "bot".equalsIgnoreCase(game.getStoredValue(realm + "State"))) {
            message += "a bot";
        } else {
            Player player = game.getPlayer(game.getStoredValue(realm + "State"));
            if (player != null) {
                message += player.getUserName();
            }
        }
        return message;
    }

    public List<Button> getStateButtons(Game game, String realm) {
        List<Button> buttons = new ArrayList<>();
        if (game.getStoredValue(realm + "State").isEmpty()
                || "bot".equalsIgnoreCase(game.getStoredValue(realm + "State"))) {
            buttons.add(Buttons.green("putPlayerInSeat_" + realm, "Have a player control " + realm));
        } else {
            buttons.add(Buttons.blue("putBotInSeat_" + realm, "Have a bot control " + realm));
        }

        return buttons;
    }

    public MessageComponentTree getMessageComponentTree(List<Button> buttons) {
        return MessageComponentTree.of(ActionRow.partitionOf(buttons));
    }

    @ButtonHandler("putBotInSeat")
    public static void putBotInSeat(Game game, String buttonID, ButtonInteractionEvent event) {
        String realm = buttonID.split("_")[1];
        game.setStoredValue(realm + "State", "bot");
        event.getMessage()
                .editMessage(getStateMessage(game, realm))
                .setComponents(getMessageComponentTree(getStateButtons(game, realm)))
                .queue();
    }

    @ButtonHandler("putPlayerInSeat")
    public static void putPlayerInSeat(Game game, String buttonID, ButtonInteractionEvent event) {
        String realm = buttonID.split("_")[1];
        List<Button> buttons = new ArrayList<>();
        for (Player player : game.getGamePlayers()) {
            buttons.add(
                    Buttons.green("putSpecificPlayerInSeat_" + realm + "_" + player.getUserID(), player.getUserName()));
        }
        event.getMessage()
                .editMessage("Please choose the player who should take the seat of " + realm)
                .setComponents(getMessageComponentTree(buttons))
                .queue();
    }

    @ButtonHandler("putSpecificPlayerInSeat")
    public static void putSpecificPlayerInSeat(Game game, String buttonID, ButtonInteractionEvent event) {
        String realm = buttonID.split("_")[1];
        String userID = buttonID.split("_")[2];
        game.setStoredValue(realm + "State", userID);
        event.getMessage()
                .editMessage(getStateMessage(game, realm))
                .setComponents(getMessageComponentTree(getStateButtons(game, realm)))
                .queue();
    }
}
