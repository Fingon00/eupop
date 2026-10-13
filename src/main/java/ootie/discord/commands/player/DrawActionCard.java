package ootie.discord.commands.player;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import ootie.discord.commands.GameStateSubcommand;
import ootie.game.Game;
import ootie.game.Player;
import ootie.helpers.Constants;
import ootie.helpers.PlayerHelper;

class DrawActionCard extends GameStateSubcommand {

    DrawActionCard() {
        super(Constants.DRAW_ACTIONCARD, "Draw Action Card", true, true);
        addOptions(new OptionData(OptionType.STRING, Constants.AC_TYPE, "Action card type")
                .setAutoComplete(true)
                .setRequired(true));
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        Game game = getGame();
        Player player = getPlayer();
        String type = event.getOption(Constants.AC_TYPE).getAsString();

        if ("administrative".equalsIgnoreCase(type)) {
            player.getActionCards().add(game.getAdministrativeActionCardDeck().removeFirst());
        }
        if ("military".equalsIgnoreCase(type)) {
            player.getActionCards().add(game.getMilitaryActionCardDeck().removeFirst());
        }
        if ("diplomatic".equalsIgnoreCase(type)) {
            player.getActionCards().add(game.getDiplomaticActionCardDeck().removeFirst());
        }
        PlayerHelper.showActionCards(player, event);
    }
}
