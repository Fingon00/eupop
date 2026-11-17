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
                && !bot.getBotFocusCubes().getFirst().equalsIgnoreCase("economic")) {
            String matchingIdea1 = getMatchingIdea(bot.getBotFocusCubes().getFirst(), game, bot);
            if (!matchingIdea1.isEmpty()) {
                bot.researchIdea(matchingIdea1);
                resolved = true;
            } else {
                int x = 0;
                while (x < 100 && !resolved) {
                    String ideaType = getIdeaType();
                    String matchingIdea = getMatchingIdea(ideaType, game, bot);
                    if (!matchingIdea.isEmpty()) {
                        bot.researchIdea(matchingIdea);
                        resolved = true;
                    }
                }
            }
        } else {
            int x = 0;
            while (x < 100 && !resolved) {
                String ideaType = getIdeaType();
                String matchingIdea = getMatchingIdea(ideaType, game, bot);
                if (!matchingIdea.isEmpty()) {
                    bot.researchIdea(matchingIdea);
                    resolved = true;
                }
            }
        }

        return resolved;
    }

    public static String getMatchingIdea(String type, Game game, Player bot) {
        String idea = "";
        int numPlayersWhoGotIt = 10;
        switch (type) {
            case "Military" -> {
                for (String ideaG : game.getMilitaryIdeas()) {
                    if (bot.getIdeas().contains(ideaG)) {
                        continue;
                    }
                    if (game.getPlayersWhoResearchedIdea(ideaG).size() <= numPlayersWhoGotIt) {
                        idea = ideaG;
                        numPlayersWhoGotIt =
                                game.getPlayersWhoResearchedIdea(ideaG).size();
                    }
                }
            }
            case "Administrative" -> {
                for (String ideaG : game.getAdministrativeIdeas()) {
                    if (bot.getIdeas().contains(ideaG)) {
                        continue;
                    }
                    if (game.getPlayersWhoResearchedIdea(ideaG).size() <= numPlayersWhoGotIt) {
                        idea = ideaG;
                        numPlayersWhoGotIt =
                                game.getPlayersWhoResearchedIdea(ideaG).size();
                    }
                }
            }
            case "Diplomatic" -> {
                for (String ideaG : game.getDiplomaticIdeas()) {
                    if (bot.getIdeas().contains(ideaG)) {
                        continue;
                    }
                    if (game.getPlayersWhoResearchedIdea(ideaG).size() <= numPlayersWhoGotIt) {
                        idea = ideaG;
                        numPlayersWhoGotIt =
                                game.getPlayersWhoResearchedIdea(ideaG).size();
                    }
                }
            }
        }
        return idea;
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
