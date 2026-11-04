package ootie.service.player;

import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import ootie.game.Game;
import ootie.game.Player;
import ootie.image.Mapper;
import ootie.model.AreaModel;
import ootie.model.SetupCardModel;
import org.apache.commons.lang3.StringUtils;

@UtilityClass
public class PlayerSetupService {

    public static void resetPlayer(Player player, Game game) {
        String playerID = player.getUserID();
        player = new Player(game);
        player.setUserID(playerID);
    }

    public static void setupPlayer(
            String color,
            String realm,
            Player player,
            Game game,
            GenericInteractionCreateEvent event,
            String setupCardID,
            boolean isBot) {
        resetPlayer(player, game);
        player.setRealm(realm);
        player.setColor(color);
        player.setBot(isBot);
        if (setupCardID != null) {
            setupPlayer(player, game, setupCardID);
        }
    }

    public static void setupPlayer(Player player, Game game, String setupCardID) {
        SetupCardModel setupCardModel = Mapper.getSetupCards().get(setupCardID);
        if (setupCardModel.getProvinces() != null) {
            for (String province : setupCardModel.getProvinces()) {
                if (province.contains("All in")) {
                    province = province.replace("All in ", "").trim();
                    for (String area : province.split(",")) {
                        String areaName = area.trim().toLowerCase().replaceAll(" ", "_");
                        AreaModel areaM = Mapper.getAreas().get(areaName);
                        if (area != null) {
                            for (String prov : areaM.getProvinces()) {
                                player.getSmallProvinces().add(prov);
                            }
                        } else {
                            System.out.println("Didnt find an area for " + areaName);
                        }
                    }
                } else {
                    if (province.contains("(L)")) {
                        player.getLargeProvinces()
                                .add(province.replace("(L)", "").trim().toLowerCase());
                    } else {
                        player.getSmallProvinces().add(province.trim().toLowerCase());
                    }
                }
            }
        }
        for (String merchant : setupCardModel.getMerchants()) {
            player.getMerchants().put(merchant, false);
        }
        player.setStateReligion(setupCardModel.getState_religion());
        player.setRuler(setupCardID);
        for (String influence : setupCardModel.getInfluence()) {
            if (influence.contains("(")) {
                String numberString = StringUtils.substringBefore(")", StringUtils.substringAfter("(", influence));
                String areaName = StringUtils.substringBefore("(", influence)
                        .trim()
                        .toLowerCase()
                        .replaceAll(" ", "_");
                int num = Integer.parseInt(numberString);
                for (int x = 0; x < num; x++) {
                    player.getInfluence().add(areaName);
                }
            }
        }
        player.setColonists(setupCardModel.getColonists());

        for (String unit : setupCardModel.getMilitary()) {
            if (unit.contains("(L. Ship")) {
                String loc = StringUtils.substringBefore(unit, "(L. Ship")
                        .trim()
                        .toLowerCase()
                        .replaceAll(" ", "_");
                int[] ships = {1, 1};
                if (player.getLightShips().containsKey(loc)) {
                    ships[0] = ships[0] + player.getLightShips().get(loc)[0];
                    ships[1] = ships[1] + player.getLightShips().get(loc)[1];
                }
                player.getLightShips().put(loc, ships);
            }
            if (unit.contains("(Army 1:")) {
                String loc = StringUtils.substringBefore(unit, "(")
                        .trim()
                        .toLowerCase()
                        .replaceAll(" ", "_");
                player.getArmy1().setLocation(loc);
                String units = StringUtils.substringAfter(unit, ":");
                for (String un : units.split(",")) {
                    un = un.replace(")", "").toLowerCase();
                    int amount = Integer.parseInt(StringUtils.substringBefore(un, " "));
                    if (un.contains("infantry")) {
                        player.getArmy1().setInfantry(amount);
                    }
                    if (un.contains("cav")) {
                        player.getArmy1().setCavalry(amount);
                    }
                    if (un.contains("artillery")) {
                        player.getArmy1().setArtillery(amount);
                    }
                }
            }
            if (unit.contains("Fleet")) {
                String loc = StringUtils.substringBefore(unit, "(")
                        .trim()
                        .toLowerCase()
                        .replaceAll(" ", "_");
                player.getFleet().setLocation(loc);
                String units = StringUtils.substringAfter(unit, ":");
                for (String un : units.split(",")) {
                    un = un.replace(")", "").toLowerCase();
                    int amount = Integer.parseInt(StringUtils.substringBefore(un, " "));
                    if (un.contains("galley")) {
                        player.getFleet().setGalleys(amount);
                    }
                    if (un.contains("heavy") || un.contains("h.")) {
                        player.getFleet().setHeavyShips(amount);
                        ;
                    }
                    if (un.contains("light")) {
                        player.getFleet().setLightShips(amount);
                    }
                }
            }
            if (unit.contains("(Army 2:")) {
                String loc = StringUtils.substringBefore(unit, "(")
                        .trim()
                        .toLowerCase()
                        .replaceAll(" ", "_");
                player.getArmy2().setLocation(loc);
                String units = StringUtils.substringAfter(unit, ":");
                for (String un : units.split(",")) {
                    un = un.replace(")", "").toLowerCase();
                    int amount = Integer.parseInt(StringUtils.substringBefore(un, " "));
                    if (un.contains("infantry")) {
                        player.getArmy2().setInfantry(amount);
                    }
                    if (un.contains("cav")) {
                        player.getArmy2().setCavalry(amount);
                    }
                    if (un.contains("artillery")) {
                        player.getArmy2().setArtillery(amount);
                    }
                }
            }
        }
    }
}
