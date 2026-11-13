package ootie.service.bot;

import lombok.experimental.UtilityClass;
import ootie.game.Game;
import ootie.game.Player;
import ootie.image.Mapper;
import ootie.model.BotCardModel;

@UtilityClass
public class StartBotTurnService {
    public static void startBotTurn(Player bot, Game game) {
        boolean canTakeMoreTurns =
                true; // TODO: need to run the calc on if everyone has passed, and change this to false if so
        if (!canTakeMoreTurns || bot.getBotPower() == 0) {
            botEventNode(bot, game);
        } else {
            boolean botIsUnderAttack =
                    false; // TODO: Write a method that checks every bot province area for any enemies, or if any
            // enemies occupy bot provinces
            if (botIsUnderAttack) {
                // TODO: Implement Defend Action
                bot.spendBotPower(1);
            } else {
                boolean botCanSiege = false; // TODO: Write a method that checks if the bots army can siege
                if (botCanSiege) {
                    // TODO: Implement Siege Action
                } else {
                    if (bot.getBotPower() > 2 && bot.getColonists() > 0) {
                        // TODO: Implement Colonize Action
                        bot.spendBotPower(2);
                    } else {
                        if (bot.getSpentBotPower() > bot.getBotPower()) {
                            if (!bot.getAtWarWith().isEmpty() && bot.getBotPower() > 3) {
                                botCardNode(bot, game);
                            } else {
                                botEventNode(bot, game);
                            }
                        } else {
                            botCardNode(bot, game);
                        }
                    }
                }
            }
        }
    }

    public static void botCardNode(Player bot, Game game) {
        if (bot.getBotDeck().isEmpty()) {
            bot.spendBotPower(1);
            bot.scorePrestige(1, "not having any bot cards left and spending 1 bot power.");
            // TODO: Write an end turn method
        } else {
            BotCardModel botCard = Mapper.getBotCards().get(bot.drawBotCard());
            int spentPower = Math.min(bot.getBotPower(), botCard.getBotPower());
            bot.spendBotPower(spentPower);
            if (!botCard.getActionCard().isEmpty()) {
                // TODO: Write method to discard AC and declare what it was
            }
            if (!botCard.getWarAction().isEmpty() && !bot.getAtWarWith().isEmpty()) {
                boolean worked = doActionOfBotCard(bot, game, botCard.getWarAction());
                if (worked) {
                    // end turn
                } else {
                    firstActionOfCardNode(bot, game, botCard, spentPower);
                }
            } else {
                firstActionOfCardNode(bot, game, botCard, spentPower);
            }
        }
    }

    public static boolean doActionOfBotCard(Player bot, Game game, String action) {
        switch (action) {
            case "Event" -> {
                // event
            }
            case "Military" -> {
                // TODO: This
            }
            case "Diplomacy" -> {
                // TODO: This
            }
            case "Naval" -> {
                // TODO: This
            }
            case "Explore" -> {
                // TODO: This
            }
            case "Colonize" -> {
                // TODO: This
            }
            case "Unrest" -> {
                // TODO: This
            }
            case "Spy" -> {
                // TODO: This
            }
            case "Papal Curia" -> {
                return BotPapalCuriaActionService.papalCuriaAction(bot, game);
            }
            case "Convert" -> {
                // TODO: This
            }
            case "Focus (Pol.)" -> {
                return BotFocusActionService.focusAction(bot, game, "Politics");
            }
            case "Focus (War.)" -> {
                return BotFocusActionService.focusAction(bot, game, "Warfare");
            }
            case "Focus (Eco.)" -> {
                return BotFocusActionService.focusAction(bot, game, "Economic");
            }
            case "Focus (Exp.)" -> {
                return BotFocusActionService.focusAction(bot, game, "Expansion");
            }
        }

        return false;
    }

    public static void firstActionOfCardNode(Player bot, Game game, BotCardModel botCard, int spentPower) {
        boolean worked = doActionOfBotCard(bot, game, botCard.getAction1());
        if (worked) {
            // end turn
        } else {
            if (!botCard.getAction2().isEmpty()) {
                worked = doActionOfBotCard(bot, game, botCard.getAction2());
                if (worked) {
                    // end turn
                } else {
                    bot.scorePrestige(
                            spentPower,
                            "being unable to resolve any of the actions on the card and spending " + spentPower
                                    + " bot power.");
                    // end turn
                }
            } else {
                bot.scorePrestige(
                        spentPower,
                        "being unable to resolve any of the actions on the card and spending " + spentPower
                                + " bot power.");
                // end turn
            }
        }
    }

    public static void botEventNode(Player bot, Game game) {
        if (bot.isUsedEvent()) {
            // TODO: Write a pass method
        } else {
            // TODO: Write an event method
        }
    }
}
