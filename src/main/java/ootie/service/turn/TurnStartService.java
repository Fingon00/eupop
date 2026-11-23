package ootie.service.turn;

import ootie.game.Game;
import ootie.game.Player;
import ootie.message.MessageHelper;
import ootie.service.bot.StartBotTurnService;

public class TurnStartService {

    public static void startTurn(Player player, Game game) {
        MessageHelper.sendMessageToChannel(game.getMainGameChannel(), player.getRepresentation() + " started turn.");
        if (player.isBot()) {
            StartBotTurnService.startBotTurn(player, game);
        } else {
            MessageHelper.sendMessageToChannel(game.getMainGameChannel(), "Idk how to do this turn yet.");
        }
    }
}
