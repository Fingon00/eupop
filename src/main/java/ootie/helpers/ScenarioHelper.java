package ootie.helpers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import ootie.game.Game;
import ootie.game.Player;
import ootie.image.Mapper;
import ootie.model.ScenarioModel;
import ootie.model.Source.ComponentSource;
import ootie.model.TradeCardModel;

public class ScenarioHelper {

    public static void setupScenarioDecks(Game game, String scenarioID) {
        ScenarioModel scen = Mapper.getScenarios().get(scenarioID);
        List<String> events = new ArrayList<>();
        if (scen.getStartingAge() == 1) {
            events.addAll(scen.getEventsA1H1());
        }

        Collections.shuffle(events);
        game.setEventDeck(events);
        game.setScenario(scenarioID);

        if ("S1-00".equalsIgnoreCase(scenarioID)) {
            game.setPlayerOrder(List.of("France", "Castile", "England"));
            game.setEconomicMilestones(List.of("Full Cabinet"));
            game.setPoliticalMilestones(List.of("Dynastic Ties"));
            game.setWarfareMilestones(List.of("Victorious in Battle"));
            game.setExpansionMilestones(List.of("Build a Trade Fleet"));
            game.getAdministrativeActionCardDeck()
                    .removeIf(id -> id.contains("ADM-01")
                            || id.contains("ADM-02")
                            || id.contains("ADM-07")
                            || id.contains("ADM-08"));
            game.getDiplomaticActionCardDeck()
                    .removeIf(id -> id.contains("DIP-02")
                            || id.contains("DIP-05")
                            || id.contains("DIP-07")
                            || id.contains("DIP-06"));
            game.getMilitaryActionCardDeck()
                    .removeIf(id -> id.contains("MIL-01")
                            || id.contains("MIL-03")
                            || id.contains("MIL-05")
                            || id.contains("MIL-06")
                            || id.contains("MIL-09"));
            List<String> tradeCards = new ArrayList<>();
            for (TradeCardModel tradeCard : Mapper.getTradeCards().values()) {
                if (tradeCard.getSource() != ComponentSource.base || tradeCard.isHasCross() || tradeCard.isHasStar()) {
                    continue;
                }
                tradeCards.add(tradeCard.getID());
            }
            Collections.shuffle(tradeCards);
            game.setTradeDeck(tradeCards);
            Player france = game.getPlayerByRealmOrColor("France");
            if (!france.isBot()) {
                france.getMissions().add("generic_1A");
                france.getMissions().add("generic_1F");
            }

            Player england = game.getPlayerByRealmOrColor("England");
            if (!england.isBot()) {
                england.getMissions().add("generic_1D");
                england.getMissions().add("generic_1F");
            }

            Player castile = game.getPlayerByRealmOrColor("Castile");
            if (!castile.isBot()) {
                castile.getMissions().add("generic_1A");
                castile.getMissions().add("generic_1B");
            }
        }

        for (Player player : game.getGamePlayers()) {
            if (player.isBot()) {
                continue;
            }
            player.getActionCards().add(game.getAdministrativeActionCardDeck().removeFirst());
            player.getActionCards().add(game.getMilitaryActionCardDeck().removeFirst());
            player.getActionCards().add(game.getDiplomaticActionCardDeck().removeFirst());
            PlayerHelper.showActionCards(player);
            PlayerHelper.showMissions(player);
        }
    }
}
