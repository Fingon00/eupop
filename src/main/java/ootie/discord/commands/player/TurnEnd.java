package ootie.discord.commands.player;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import ootie.discord.commands.GameStateSubcommand;
import ootie.game.Game;
import ootie.game.Player;
import ootie.helpers.Constants;
import ootie.service.turn.TurnEndService;

class TurnEnd extends GameStateSubcommand {

    TurnEnd() {
        super(Constants.TURN_END, "End turn", true, true);
        addOptions(new OptionData(OptionType.STRING, Constants.REALM_COLOR, "End someone else's turn")
                .setAutoComplete(true)
                .setRequired(false));
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        Game game = getGame();
        Player player = getPlayer();
        TurnEndService.endTurn(player, game);
    }
}
