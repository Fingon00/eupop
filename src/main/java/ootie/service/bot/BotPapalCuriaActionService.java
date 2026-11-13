package ootie.service.bot;

import lombok.experimental.UtilityClass;
import ootie.game.Game;
import ootie.game.Player;

@UtilityClass
public class BotPapalCuriaActionService {
    public static boolean papalCuriaAction(Player bot, Game game) {
        boolean resolved = false;

        if (bot.getStateReligion().contains("Catholic")
                || bot.getStateReligion().contains("Counter")) {
            boolean isUncontestedPapalController = false; // TODO: Write a method to figure out who controls the PC
            if (isUncontestedPapalController) {
                if (game.getAge() < 3) {
                    boolean isAdjacentTo2MuslimProvWithNoExcomCrusade = false; // TODO: Figure this out
                    if (isAdjacentTo2MuslimProvWithNoExcomCrusade) {
                        // TODO: Place a crusade token on Area and a claim
                        bot.scorePrestige(1, "it declared a crusade against the Muslims.");
                        // Recruit 2 units if able
                        // Military Action
                        resolved = true;
                    } else {
                        boolean otherCatholicsNNoActiveCrusadeExcom = false; // TODO: Figure this out
                        if (otherCatholicsNNoActiveCrusadeExcom) {
                            // TODO: EXCOM opponent
                            resolved = true;
                        }
                    }
                }
            } else {
                boolean otherCatholicsNNoActiveCrusadeExcom = false;
                if (game.getAge() < 3 && otherCatholicsNNoActiveCrusadeExcom) {
                    // EXCOM opponent
                    resolved = true;
                } else {
                    game.getNonRomaCardinals().add(0, bot.getRealm());
                    // TODO: Check if papalController and remove excom on own realm if so
                    resolved = true;
                }
            }
        }

        return resolved;
    }
}
