package ootie.service.bot;

import lombok.experimental.UtilityClass;
import ootie.game.Game;
import ootie.game.Player;
import ootie.message.MessageHelper;

@UtilityClass
public class BotUnrestActionService {
    public static boolean unrestAction(Player bot, Game game) {
        boolean resolved = false;
        if (bot.getUnrestProvinces().isEmpty()) {
            if (bot.getCoreableAreas().isEmpty()) {
                return false;
            }
            if (bot.getCoredAreas().size() < 2) {
                String bestArea = null;
                int bestTaxValue = 0;
                for (String area : bot.getCoreableAreas()) {
                    if (bot.getTaxValueOfArea(area) > bestTaxValue) {
                        bestArea = area;
                        bestTaxValue = bot.getTaxValueOfArea(area);
                    }
                }
                bot.getCoredAreas().add(bestArea);
                MessageHelper.sendMessageToChannel(
                        game.getMainGameChannel(),
                        bot.getRepresentation() + " cored the area of " + bestArea.toUpperCase() + ".");
                resolved = true;
            } else {
                String bestArea = null;
                String worstCore = null;
                int bestTaxValue = 100;
                for (String area : bot.getCoredAreas()) {
                    if (bot.getTaxValueOfArea(area) < bestTaxValue) {
                        worstCore = area;
                        bestArea = area;
                        bestTaxValue = bot.getTaxValueOfArea(area);
                    }
                }
                for (String area : bot.getCoreableAreas()) {
                    if (bot.getTaxValueOfArea(area) > bestTaxValue) {
                        bestArea = area;
                        bestTaxValue = bot.getTaxValueOfArea(area);
                    }
                }

                boolean canCoreHigherArea = bot.getCoredAreas().contains(bestArea);
                if (canCoreHigherArea) {
                    bot.getCoredAreas().remove(worstCore);
                    MessageHelper.sendMessageToChannel(
                            game.getMainGameChannel(),
                            bot.getRepresentation() + " removed a core token from " + worstCore.toUpperCase() + ".");
                    bot.getCoredAreas().add(bestArea);
                    MessageHelper.sendMessageToChannel(
                            game.getMainGameChannel(),
                            bot.getRepresentation() + " cored the area of " + bestArea.toUpperCase() + ".");
                    resolved = true;
                } else {
                    return false;
                }
            }
        } else {
            String capital = bot.getCapital();
            if (capital == null) {
                capital = "Paris";
            }
            int unrestRemoved = 0;
            if (bot.getUnrestProvinces().remove(capital)) {
                unrestRemoved++;
                MessageHelper.sendMessageToChannel(
                        game.getMainGameChannel(), bot.getRepresentation() + "removed unrest from " + capital + ".");
            }
            for (String large : bot.getLargeProvinces()) {
                if (unrestRemoved < 2) {
                    if (bot.getUnrestProvinces().remove(large)) {
                        unrestRemoved++;
                        MessageHelper.sendMessageToChannel(
                                game.getMainGameChannel(),
                                bot.getRepresentation() + "removed unrest from " + large + ".");
                    }
                }
            }
            for (String small : bot.getSmallProvinces()) {
                if (unrestRemoved < 2) {
                    if (bot.getUnrestProvinces().remove(small)) {
                        unrestRemoved++;
                        MessageHelper.sendMessageToChannel(
                                game.getMainGameChannel(),
                                bot.getRepresentation() + "removed unrest from " + small + ".");
                    }
                }
            }
            resolved = true;
        }

        return resolved;
    }
}
