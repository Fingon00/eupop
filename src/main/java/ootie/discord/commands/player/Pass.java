package ootie.discord.commands.player;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import ootie.discord.commands.GameStateSubcommand;
import ootie.game.Game;
import ootie.game.Player;
import ootie.helpers.Constants;
import ootie.service.turn.PassService;

class Pass extends GameStateSubcommand {

    Pass() {
        super(Constants.PASS, "pass", true, true);
        addOptions(new OptionData(OptionType.STRING, Constants.REALM_COLOR, "Pass for someone else")
                .setAutoComplete(true)
                .setRequired(false));
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        Game game = getGame();
        Player player = getPlayer();
        PassService.pass(player, game);
    }
}
