package ootie.service.bot;

import lombok.experimental.UtilityClass;
import ootie.game.Game;
import ootie.game.Player;

@UtilityClass
public class BotUnrestActionService {
    public static boolean unrestAction(Player bot, Game game) {
        boolean resolved = false;
        if (bot.getUnrestProvinces().isEmpty()) {
            // TODO: Check if bot can place a core
            if (bot.getCoredAreas().size() < 2) {
                // TODO: Place Core
                resolved = true;
            } else {
                boolean canCoreHigherArea = false; // TODO: Check if this is possible
                if (canCoreHigherArea) {
                    // TODO: Remove Core
                    // Place Core
                    resolved = true;
                } else {
                    return false;
                }
            }
        } else {
            // TODO: Remove 2 unrest from bot provinces
            resolved = true;
        }

        return resolved;
    }
}
