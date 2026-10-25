package ootie.discord.commands.player;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import ootie.discord.commands.GameStateSubcommand;
import ootie.game.Game;
import ootie.game.Player;
import ootie.helpers.Constants;
import ootie.message.MessageHelper;
import ootie.service.player.PlayerSetupService;

class Setup extends GameStateSubcommand {

    Setup() {
        super(Constants.SETUP, "Player initialisation: Realm and Color", true, true);
        addOptions(new OptionData(OptionType.STRING, Constants.REALM, "Faction Name").setAutoComplete(true));
        addOptions(new OptionData(OptionType.STRING, Constants.COLOR, "Color of units").setAutoComplete(true));
        addOptions(new OptionData(OptionType.USER, Constants.PLAYER, "Player for which you set up realm"));
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        Game game = getGame();
        String realm = event.getOption(Constants.REALM, null, OptionMapping::getAsString);
        if (game.getPlayerByRealmOrColor(realm) != null) {
            MessageHelper.sendMessageToChannel(event.getMessageChannel(), "Someone already is set up to use " + realm);
            return;
        }
        Player player = getPlayer();
        String color = event.getOption(Constants.COLOR, OptionMapping::getAsString);
        if (game.getPlayerByRealmOrColor(color) != null) {
            MessageHelper.sendMessageToChannel(event.getMessageChannel(), "Someone already is set up to use " + color);
            return;
        }
        PlayerSetupService.setupPlayer(color, realm, player, game, event);
    }
}
