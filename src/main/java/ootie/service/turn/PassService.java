package ootie.service.turn;

import ootie.game.Game;
import ootie.game.Player;
import ootie.message.MessageHelper;

public class PassService {

    public static void pass(Player player, Game game) {
        MessageHelper.sendMessageToChannel(game.getMainGameChannel(), player.getRepresentation() + " passed.");
        player.setPassed(true);
        giveOutPassingRewards(player, game);
        Player nextPlayer = game.getNextPlayer(player);
        if (nextPlayer != null) {
            TurnStartService.startTurn(nextPlayer, game);
        } else {
            // TODO: Start next phase
        }
    }

    public static void giveOutPassingRewards(Player player, Game game) {
        int passedPlayerSize = game.getPassedPlayers().size();
        int playerSize = game.getGamePlayers().size();
        if (player.isBot()) {
            if (passedPlayerSize == 1) {
                player.gainBotPower(2, "passing first.");
            }
            if (passedPlayerSize == 2) {
                player.gainBotPower(1, "passing second.");
            }
        } else {
            if (passedPlayerSize == 1) {
                player.gainDucats(5, "passing first.");
            }
            if (passedPlayerSize == 2) {
                if (playerSize == 3) {
                    player.gainDucats(2, "passing second.");
                } else {
                    player.gainDucats(3, "passing second.");
                }
            }
            if (passedPlayerSize == 3 && playerSize > 3) {
                player.gainDucats(2, "passing third.");
            }
            if (passedPlayerSize == 4 && playerSize > 5) {
                player.gainDucats(2, "passing fourth.");
            }
        }
        if ((passedPlayerSize == 2 && playerSize == 3)
                || (passedPlayerSize == 3 && playerSize == 4)
                || (passedPlayerSize == 3 && playerSize == 5)
                || (passedPlayerSize == 4 && playerSize == 6)) {
            game.setStoredValue("passMoneyRanOut", "yes");
            for (Player p2 : game.getGamePlayers()) {
                if (!p2.isPassed()) {
                    game.setStoredValue(
                            "remainingPlayersWithTurn",
                            game.getStoredValue("remainingPlayersWithTurn") + "_" + p2.getRealm());
                }
            }
        }
    }
}
