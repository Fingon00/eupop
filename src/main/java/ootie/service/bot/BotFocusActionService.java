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
                String availableMilestone = getAvailableMilestone(bot, game, type);
                if (availableMilestone.isEmpty()) {
                    bot.setBotPower(bot.getBotPower() + 2);
                    bot.completeMilestone(availableMilestone);
                } else {
                    bot.scorePrestige(3, "no available milestone to score of type: " + type);
                }
            }
            resolved = true;
        }

        return resolved;
    }

    public static String getAvailableMilestone(Player bot, Game game, String type) {
        String milestone = "";
        switch (type.toLowerCase()) {
            case "political" -> {
                for (String m : game.getPoliticalMilestones()) {
                    if (!bot.getCompletedMilestones().contains(m)) {
                        return m;
                    }
                }
            }
            case "expansion" -> {
                for (String m : game.getExpansionMilestones()) {
                    if (!bot.getCompletedMilestones().contains(m)) {
                        return m;
                    }
                }
            }
            case "economic" -> {
                for (String m : game.getEconomicMilestones()) {
                    if (!bot.getCompletedMilestones().contains(m)) {
                        return m;
                    }
                }
            }
            case "warfare" -> {
                for (String m : game.getWarfareMilestones()) {
                    if (!bot.getCompletedMilestones().contains(m)) {
                        return m;
                    }
                }
            }
        }
        return milestone;
    }
}
