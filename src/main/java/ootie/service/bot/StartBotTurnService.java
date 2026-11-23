package ootie.service.bot;

import lombok.experimental.UtilityClass;
import ootie.game.Game;
import ootie.game.Player;
import ootie.image.Mapper;
import ootie.model.BotCardModel;
import ootie.service.turn.PassService;
import ootie.service.turn.TurnEndService;

@UtilityClass
public class StartBotTurnService {
    public static void startBotTurn(Player bot, Game game) {
        boolean canTakeMoreTurns = game.canPlayerTakeANormalTurn(bot);
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
                    BotSiegeActionService.siegeAction(bot, game);
                } else {
                    if (bot.getBotPower() > 2 && bot.getColonists() > 0) {
                        BotColonizeActionService.colonizeAction(bot, game);
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
            TurnEndService.endTurn(bot, game);
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
                    TurnEndService.endTurn(bot, game);
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
                return BotEventActionService.eventAction(bot, game);
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
                return BotExploreActionService.exploreAction(bot, game);
            }
            case "Colonize" -> {
                return BotColonizeActionService.colonizeAction(bot, game);
            }
            case "Unrest" -> {
                return BotUnrestActionService.unrestAction(bot, game);
            }
            case "Spy" -> {
                return BotSpyActionService.spyAction(bot, game);
            }
            case "Papal Curia" -> {
                return BotPapalCuriaActionService.papalCuriaAction(bot, game);
            }
            case "Convert" -> {
                return BotConvertActionService.convertAction(bot, game);
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
        if (!worked) {
            if (!botCard.getAction2().isEmpty()) {
                worked = doActionOfBotCard(bot, game, botCard.getAction2());
                if (!worked) {
                    bot.scorePrestige(
                            spentPower,
                            "being unable to resolve any of the actions on the card and spending " + spentPower
                                    + " bot power.");
                }
            } else {
                bot.scorePrestige(
                        spentPower,
                        "being unable to resolve any of the actions on the card and spending " + spentPower
                                + " bot power.");
            }
        }
        TurnEndService.endTurn(bot, game);
    }

    public static void botEventNode(Player bot, Game game) {
        if (bot.isUsedEvent()) {
            PassService.pass(bot, game);
        } else {
            BotEventActionService.eventAction(bot, game);
        }
    }
}
