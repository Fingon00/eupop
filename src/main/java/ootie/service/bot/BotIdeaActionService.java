package ootie.service.bot;

import java.util.Random;
import lombok.experimental.UtilityClass;
import ootie.game.Game;
import ootie.game.Player;

@UtilityClass
public class BotIdeaActionService {
    public static boolean ideaAction(Player bot, Game game) {
        boolean resolved = false;

        if (!bot.getBotFocusCubes().isEmpty()
                && !bot.getBotFocusCubes().getFirst().equalsIgnoreCase("politics")) {
            boolean anyMatchingIdeas = false; // TODO Check this
            if (anyMatchingIdeas) {
                // Tag idea and score P as normal
                resolved = true;
            } else {
                int x = 0;
                while (x < 100 && !resolved) {
                    String ideaType = getIdeaType();
                    /// Do matchingIdeas check
                }
            }
        } else {
            int x = 0;
            while (x < 100 && !resolved) {
                String ideaType = getIdeaType();
                /// Do matchingIdeas check
            }
        }

        return resolved;
    }

    public static String getIdeaType() {
        Random random = new Random();
        int result = random.nextInt(6) + 1;
        if (result > 4) {
            return "Military";
        }
        if (result > 2) {
            return "Diplomatic";
        }

        return "Administrative";
    }
}
