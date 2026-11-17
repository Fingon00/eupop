package ootie.service.bot;

import java.util.HashMap;
import java.util.Map;
import lombok.experimental.UtilityClass;
import ootie.game.Game;
import ootie.game.Player;
import ootie.image.Mapper;
import ootie.model.AreaModel;

@UtilityClass
public class BotConvertActionService {
    public static boolean convertAction(Player bot, Game game) {
        boolean resolved = false;
        Map<String, String> areasWithRD = getAreasWithRD(bot, game);
        boolean religiousDissent = areasWithRD.isEmpty();
        if (religiousDissent) {
            boolean isAllCatholicOrProtestant = true;
            String lastReligion = "";
            for (String religion : areasWithRD.values()) {
                if (religion.contains("Counter")) {
                    religion = "Catholic";
                }
                if (!"Catholic".equalsIgnoreCase(religion) && !"Protestant".equalsIgnoreCase(religion)) {
                    isAllCatholicOrProtestant = false;
                    break;
                }
                if (lastReligion.isEmpty()) {
                    lastReligion = religion;
                } else {
                    if (!lastReligion.equalsIgnoreCase(religion)) {
                        isAllCatholicOrProtestant = false;
                        break;
                    }
                }
            }

            if (isAllCatholicOrProtestant) {
                bot.setStateReligion(lastReligion);
                bot.scorePrestige(-3, "changing state religion.");
                // Lose 1 royal marriage
                // Move 5 cubes
                // Add Unrest
                // Roll rebel dice
                resolved = true;
            } else {
                String areaToConvert = getAreaToConvert(bot, game);
                if (areaToConvert != null) {
                    game.convertArea(areaToConvert, bot.getStateReligion());
                    bot.scorePrestige(
                            2, "converting an area (" + areaToConvert + ") to " + bot.getStateReligion() + ".");
                    resolved = true;
                }
            }
        }

        return resolved;
    }

    public static String getAreaToConvert(Player player, Game game) {
        Map<String, String> areasWithRD = getAreasWithRD(player, game);
        String preferredArea = null;
        String stateReligion = player.getStateReligion();
        int highestTaxValue = 0;
        for (String area : areasWithRD.keySet()) {
            boolean canConvert = true;
            int taxValue = 0;
            AreaModel aM = Mapper.getAreas().get(area);
            for (String province : aM.getProvinces()) {
                Player owner = game.getProvinceOwner(province);
                if (owner != null) {
                    if (owner == player) {
                        taxValue += game.getProvinceTaxValue(province);
                    }
                    if (!owner.getStateReligion().equalsIgnoreCase(stateReligion)) {
                        canConvert = false;
                    }
                } else {
                    canConvert = false;
                }
            }
            if (canConvert) {
                if (taxValue > highestTaxValue) {
                    preferredArea = area;
                    highestTaxValue = taxValue;
                }
            }
        }
        return preferredArea;
    }

    public static AreaModel getAreaByProvince(String province) {
        for (AreaModel a : Mapper.getAreas().values()) {
            if (a.getProvinces().contains(province)) {
                return a;
            }
        }
        return null;
    }

    public static Map<String, String> getAreasWithRD(Player player, Game game) {
        Map<String, String> areasWithRD = new HashMap<>();
        String stateReligion = player.getStateReligion();
        for (String province : player.getAllProvinces()) {
            AreaModel area = getAreaByProvince(province);
            String areaReligion = "Catholic"; // TODO: Change this to actually look up the right religion
            if (game.getConvertedAreas().containsKey(area.getAlias())) {
                areaReligion = game.getConvertedAreas().get(area.getAlias());
            }
            if (!areaReligion.equalsIgnoreCase(stateReligion)) {
                areasWithRD.put(area.getAlias(), areaReligion);
            }
        }

        return areasWithRD;
    }
}
