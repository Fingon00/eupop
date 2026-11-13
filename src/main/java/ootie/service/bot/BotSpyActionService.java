package ootie.service.bot;

import lombok.experimental.UtilityClass;
import ootie.game.Game;
import ootie.game.Player;

@UtilityClass
public class BotSpyActionService {
    public static boolean spyAction(Player bot, Game game) {
        boolean resolved = false;

        if (bot.getAtWarWith().isEmpty()) {
            boolean atWarWithPR = false;
            if (atWarWithPR) {
                Player pr = null; // TODO: Figure out how to select an opponent
                if (pr.getUnrestProvinces().size() > 2) {
                    // TODO PR must roll rebel dice
                    resolved = true;
                } else {
                    // TODO Add 2 unrest to Opponents towns
                }
                resolved = true;
            } else {
                // Military
                resolved = true;
            }
        } else {
            // Select some opponent
            // Add 2 unrest to Opponents towns
            resolved = true;
        }

        return resolved;
    }
}
