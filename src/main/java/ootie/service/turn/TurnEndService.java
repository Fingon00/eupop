package ootie.service.turn;

import ootie.game.Game;
import ootie.game.Player;
import ootie.message.MessageHelper;

public class TurnEndService {

    public static void endTurn(Player player, Game game) {
        MessageHelper.sendMessageToChannel(game.getMainGameChannel(), player.getRepresentation() + " ended turn.");
        Player nextPlayer = game.getNextPlayer(player);
        TurnStartService.startTurn(nextPlayer, game);
    }
}
