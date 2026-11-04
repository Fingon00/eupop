package ootie.discord.commands.player;

import java.util.List;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import ootie.discord.commands.GameStateSubcommand;
import ootie.game.Game;
import ootie.game.Player;
import ootie.helpers.Constants;
import ootie.image.Mapper;
import ootie.message.MessageHelper;
import ootie.model.SetupCardModel;
import ootie.service.player.PlayerSetupService;

class Setup extends GameStateSubcommand {

    Setup() {
        super(Constants.SETUP, "Player initialisation", true, true);
        addOptions(new OptionData(OptionType.STRING, Constants.SETUP_CARDS, "Setup card")
                .setAutoComplete(true)
                .setRequired(true));
        addOptions(new OptionData(OptionType.STRING, Constants.COLOR, "Color of units").setAutoComplete(true));
        addOptions(new OptionData(OptionType.USER, Constants.PLAYER, "Player for which you set up realm"));
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        Game game = getGame();
        String setupCardID = event.getOption(Constants.SETUP_CARDS, null, OptionMapping::getAsString);
        Player player = getPlayer();
        SetupCardModel setup = Mapper.getSetupCards().get(setupCardID);
        if (setup == null) {
            MessageHelper.sendMessageToChannel(
                    event.getMessageChannel(), "No setup card with this id was found: " + setupCardID);
            return;
        }
        if (game.getPlayerByRealmOrColor(setup.getRealm()) != null
                && game.getPlayerByRealmOrColor(setup.getRealm()) != player) {
            MessageHelper.sendMessageToChannel(
                    event.getMessageChannel(), "Someone already is set up to use " + setup.getRealm());
            return;
        }

        if (event.getOption(Constants.PLAYER) == null) {
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
            if (player == null) {
                User user = event.getOption(Constants.PLAYER).getAsUser();
                game.addPlayer(user.getId(), user.getEffectiveName());
                player = game.getPlayer(user.getId());
            }
        }
        String color = event.getOption(Constants.COLOR, OptionMapping::getAsString);
        if (color == null
                || (game.getPlayerByRealmOrColor(color) != null && game.getPlayerByRealmOrColor(color) != player)) {
            List<String> colors = List.of("Purple", "Yellow", "White", "Teal", "Blue", "Red");
            for (String col : colors) {
                if (game.getPlayerByRealmOrColor(col) == null) {
                    color = col;
                    break;
                }
            }
        }
        PlayerSetupService.setupPlayer(color, player, game, event, setupCardID, player.isBot());
    }
}
