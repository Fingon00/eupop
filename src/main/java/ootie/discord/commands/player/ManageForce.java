package ootie.discord.commands.player;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import ootie.discord.commands.GameStateSubcommand;
import ootie.game.Army;
import ootie.game.Fleet;
import ootie.game.Game;
import ootie.game.Player;
import ootie.helpers.Constants;
import ootie.helpers.PlayerHelper;
import ootie.image.Mapper;
import ootie.service.player.PlayerStatsService;

class ManageForce extends GameStateSubcommand {

    ManageForce() {
        super(Constants.MANAGE_FORCE, "Manage an army or fleet", true, true);
        addOptions(new OptionData(OptionType.STRING, Constants.FORCE, "Force name")
                .setAutoComplete(true)
                .setRequired(true));
        addOptions(new OptionData(OptionType.STRING, Constants.CHARACTER, "Leader -- enter gibberish to remove")
                .setAutoComplete(true)
                .setRequired(false));
        addOptions(new OptionData(
                        OptionType.STRING, Constants.INFANTRY, "Infantry count -- can use +1/-1 etc. to add/subtract")
                .setRequired(false));
        addOptions(new OptionData(
                        OptionType.STRING, Constants.CAVALRY, "Infantry count -- can use +1/-1 etc. to add/subtract")
                .setRequired(false));
        addOptions(new OptionData(
                        OptionType.STRING, Constants.ARTILLERY, "Artillery count -- can use +1/-1 etc. to add/subtract")
                .setRequired(false));
        addOptions(new OptionData(
                        OptionType.STRING,
                        Constants.LIGHT_SHIPS,
                        "Light Ship count -- can use +1/-1 etc. to add/subtract")
                .setRequired(false));
        addOptions(new OptionData(
                        OptionType.STRING,
                        Constants.HEAVY_SHIPS,
                        "Heavy Ship count -- can use +1/-1 etc. to add/subtract")
                .setRequired(false));
        addOptions(new OptionData(
                        OptionType.STRING,
                        Constants.DAMAGED_HEAVYS,
                        "Damaged Heavy Ship count -- can use +1/-1 etc. to add/subtract")
                .setRequired(false));
        addOptions(new OptionData(
                        OptionType.STRING, Constants.GALLEYS, "Galley count -- can use +1/-1 etc. to add/subtract")
                .setRequired(false));
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        Game game = getGame();
        Player player = getPlayer();
        String force = event.getOption(Constants.FORCE).getAsString();

        switch (force) {
            case "army1" -> {
                manageArmy(game, player, player.getArmy1(), event);
            }
            case "army2" -> {
                manageArmy(game, player, player.getArmy2(), event);
            }
            case "army3" -> {
                manageArmy(game, player, player.getArmy3(), event);
            }
            case "fleet" -> {
                manageFleet(game, player, player.getFleet(), event);
            }
        }

        PlayerHelper.drawArmysNFleets(player, event);
    }

    public static void manageArmy(Game game, Player player, Army army, SlashCommandInteractionEvent event) {
        OptionMapping leader = event.getOption(Constants.CHARACTER);
        if (leader != null) {
            if (Mapper.getCharacters().keySet().contains(leader.getAsString())) {
                army.setLeader(leader.getAsString());
            } else {
                army.setLeader(null);
            }
        }
        OptionMapping infantry = event.getOption(Constants.INFANTRY);
        if (infantry != null) {
            PlayerStatsService.setValue(event, game, player, infantry, army::setInfantry, army::getInfantry);
        }
        OptionMapping cavalry = event.getOption(Constants.CAVALRY);
        if (cavalry != null) {
            PlayerStatsService.setValue(event, game, player, cavalry, army::setCavalry, army::getCavalry);
        }
        OptionMapping artillery = event.getOption(Constants.ARTILLERY);
        if (artillery != null) {
            PlayerStatsService.setValue(event, game, player, artillery, army::setArtillery, army::getArtillery);
        }
    }

    public static void manageFleet(Game game, Player player, Fleet fleet, SlashCommandInteractionEvent event) {
        OptionMapping leader = event.getOption(Constants.CHARACTER);
        if (leader != null) {
            if (Mapper.getCharacters().keySet().contains(leader.getAsString())) {
                fleet.setLeader(leader.getAsString());
            } else {
                fleet.setLeader(null);
            }
        }
        OptionMapping lightShips = event.getOption(Constants.LIGHT_SHIPS);
        if (lightShips != null) {
            PlayerStatsService.setValue(event, game, player, lightShips, fleet::setLightShips, fleet::getLightShips);
        }
        OptionMapping heavyShips = event.getOption(Constants.HEAVY_SHIPS);
        if (heavyShips != null) {
            PlayerStatsService.setValue(event, game, player, heavyShips, fleet::setHeavyShips, fleet::getHeavyShips);
        }
        OptionMapping galleys = event.getOption(Constants.GALLEYS);
        if (galleys != null) {
            PlayerStatsService.setValue(event, game, player, galleys, fleet::setGalleys, fleet::getGalleys);
        }
        OptionMapping damagedHeavys = event.getOption(Constants.DAMAGED_HEAVYS);
        if (damagedHeavys != null) {
            PlayerStatsService.setValue(
                    event, game, player, damagedHeavys, fleet::setDamagedHeavyShips, fleet::getDamagedHeavyShips);
        }
    }
}
