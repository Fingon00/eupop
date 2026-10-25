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

class ChangeColor extends GameStateSubcommand {

    ChangeColor() {
        super(Constants.CHANGE_COLOR, "Change Color", true, true);
        addOptions(new OptionData(OptionType.STRING, Constants.COLOR, "Color of units").setAutoComplete(true));
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        Game game = getGame();
        Player player = getPlayer();
        String color = event.getOption(Constants.COLOR, OptionMapping::getAsString);
        if (game.getPlayerByRealmOrColor(color) != null) {
            MessageHelper.sendMessageToChannel(event.getMessageChannel(), "Someone already is set up to use " + color);
            return;
        }
        player.setColor(color);
    }
}
