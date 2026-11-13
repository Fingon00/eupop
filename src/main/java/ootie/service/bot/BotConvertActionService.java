package ootie.service.bot;

import lombok.experimental.UtilityClass;
import ootie.game.Game;
import ootie.game.Player;

@UtilityClass
public class BotConvertActionService {
    public static boolean convertAction(Player bot, Game game) {
        boolean resolved = false;

        boolean religiousDissent = false; // TODO Write a method to check this
        if (religiousDissent) {
            boolean isAllCatholicOrProtestant = false;
            if (isAllCatholicOrProtestant) {
                // Change state religion to match
                bot.scorePrestige(-3, "changing state religion.");
                // Lose 1 royal marriage
                // Move 5 cubes
                // Add Unrest
                // Roll rebel dice
                resolved = true;
            } else {
                boolean canConvert = false; // TODO: Check how to do this
                if (canConvert) {
                    // TODO: Convert
                    bot.scorePrestige(2, "converting an area(Change to actual area).");
                    resolved = true;
                }
            }
        }

        return resolved;
    }
}
