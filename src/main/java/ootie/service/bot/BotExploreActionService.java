package ootie.service.bot;

import java.util.Random;
import lombok.experimental.UtilityClass;
import ootie.game.Game;
import ootie.game.Player;
import ootie.message.MessageHelper;

@UtilityClass
public class BotExploreActionService {
    public static boolean exploreAction(Player bot, Game game) {
        boolean resolved = false;
        int colonialClaims = 0; // TODO: Figure out how to find this
        if (colonialClaims > 2) {
            return colonialTownNode(bot, game);
        } else {
            boolean vacant1TerritorryNextToBotTown = false; // TODO: Figure this out;
            if (vacant1TerritorryNextToBotTown) {
                return rollDieNode(bot, game);
            } else {
                if (bot.getIdeas().contains("Quest for the New World")) {
                    boolean vacant1TerNextToBotShip = false;
                    if (vacant1TerNextToBotShip) {
                        return rollDieNode(bot, game);
                    } else {
                        boolean canPlaceShipInExplorableSZ = false;
                        if (canPlaceShipInExplorableSZ) {
                            // place ship
                            boolean navalBattle = false;
                            if (navalBattle) {
                                // go to naval battle
                            } else {
                                return rollDieNode(bot, game);
                            }
                        } else {
                            return placeShipNode(bot, game);
                        }
                    }
                } else {
                    // Reseatch QftNW and score normal
                }
            }
        }

        return resolved;
    }

    public static boolean rollDieNode(Player bot, Game game) {
        int result = new Random().nextInt(6) + 1;
        // TODO: Display roll result
        if (result < 5) {
            return eligibleTerNode(bot, game, result);
        } else {
            if (bot.getBotPower() > 1) {
                bot.spendBotPower(1);
                return eligibleTerNode(bot, game, result);
            } else {
                // Remove ship from distant continent
                return true;
            }
        }
    }

    public static boolean placeShipNode(Player bot, Game game) {
        boolean placeShipHelps = false;
        if (placeShipHelps) {
            // TODO: Place Ship
            boolean navalBattle = false;
            if (navalBattle) {
                // go to Naval battle
            }
            return true;
        } else {
            return colonialTownNode(bot, game);
        }
    }

    public static boolean eligibleTerNode(Player bot, Game game, int result) {
        boolean matchedEligibleTer = false;
        if (matchedEligibleTer) {
            // place claim on eligible ter
            bot.scorePrestige(2, "placed a claim on an eligible territory");
            boolean firstClaim = false;
            if (firstClaim) {
                // add trade cards matching continent
            }
            return true;
        } else {
            boolean elgProvToDisc = false;
            if (elgProvToDisc) {
                // place claim on dist prov
                boolean firstClaim = false;
                if (firstClaim) {
                    // add trade cards matching continent
                }
                return true;
            } else {
                return placeShipNode(bot, game);
            }
        }
    }

    public static boolean colonialTownNode(Player bot, Game game) {
        boolean coloConnectedViaChain = false;
        if (coloConnectedViaChain) {
            if (bot.getColonists() > 2) {
                return BotColonizeActionService.colonizeAction(bot, game);
            } else {

                bot.setColonists(bot.getColonists() + 1);
                bot.setBotPower(bot.getBotPower() + 1);
                MessageHelper.sendMessageToChannel(
                        bot.getActionsChannel(), bot.getRepresentation() + " gained 1 bot power and 1 colonist.");
                return true;
            }
        } else {
            boolean wouldPlacingShipHelp = false; // TODO: figure this out
            if (wouldPlacingShipHelp) {
                // TODO: Place Ship
                boolean navalBattle = false;
                if (navalBattle) {
                    // go to Naval battle

                }
            } else {
                bot.scorePrestige(
                        2 + bot.getColonists(),
                        "not being able to place a ship that would bring the bot closer to establishing a connection.");
                bot.setColonists(0);
                // TODO: Remove colonial claims
            }
            return true;
        }
    }
}
