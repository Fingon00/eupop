package ootie.service.bot;

import java.util.Random;
import lombok.experimental.UtilityClass;
import ootie.game.Game;
import ootie.game.Player;

@UtilityClass
public class BotEventActionService {
    public static boolean eventAction(Player bot, Game game) {
        boolean resolved = false;
        if (bot.isUsedEvent()) {
            return false;
        } else {
            int result = new Random().nextInt(6) + 1;
            if (result == 1) {
                // pick leftmost revealed event, gain 1 bot power if ducats on event
                return resolveEvent(bot, game, null);
            }
            if (result == 2) {
                // pick second from left revealed event, gain 1 bot power if ducats on event
                return resolveEvent(bot, game, null);
            }
            if (result == 3) {
                // pick rightmost revealed event, gain 1 bot power if ducats on event
                return resolveEvent(bot, game, null);
            }
            if (result == 4) {
                boolean opponentEventRevealed = false;
                if (opponentEventRevealed) {
                    // pick opponent revealed event, gain 1 bot power if ducats on event
                    resolveEvent(bot, game, null);
                } else {
                    return ownEventNode(bot, game);
                }
            }
            if (result > 4) {
                return ownEventNode(bot, game);
            }
        }

        return resolved;
    }

    public static boolean ownEventNode(Player bot, Game game) {
        String event = "";
        boolean ownEventRevealed = false;
        if (ownEventRevealed) {
            // pick own event
        } else {
            boolean atWarWithOppWithEventRevealed = false;
            if (atWarWithOppWithEventRevealed) {
                // pick opponent event
            } else {
                // pick leftmost event
            }
        }

        return resolveEvent(bot, game, event);
    }

    public static boolean resolveEvent(Player bot, Game game, String event) {
        boolean isOpponentEvent = false;
        if (isOpponentEvent) {
            // resolve option B
        } else {
            // resolve option A
        }
        // Resolve secondary effects
        if (bot.getBotPower() > 10 && bot.getLargeProvinces().size() < 8) {
            bot.spendBotPower(2);
            // Turn small into large
        }
        return true;
    }
}
