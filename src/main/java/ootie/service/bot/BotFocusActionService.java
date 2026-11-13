package ootie.service.bot;

import java.util.ArrayList;
import lombok.experimental.UtilityClass;
import ootie.game.Game;
import ootie.game.Player;

@UtilityClass
public class BotFocusActionService {
    public static boolean focusAction(Player bot, Game game, String type) {
        boolean resolved = false;
        if (bot.getBotFocusCubes().isEmpty()) {
            bot.getBotFocusCubes().add(type);
            resolved = true;
        } else {
            type = bot.getBotFocusCubes().getFirst();
            bot.getBotFocusCubes().add(type);
            int threshold = bot.getBotDifficulty().equalsIgnoreCase("Easy") ? 2 : 3;
            if (bot.getBotFocusCubes().size() >= threshold) {
                bot.setBotFocusCubes(new ArrayList<>());
                boolean availableMilestone = false; // Check this
                if (availableMilestone) {
                    bot.setBotPower(bot.getBotPower() + 2);
                    // Score Milestone
                } else {
                    bot.scorePrestige(3, "no available milestone to score of type: " + type);
                }
            }

            resolved = true;
        }

        return resolved;
    }
}
