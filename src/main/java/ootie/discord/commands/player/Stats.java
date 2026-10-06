package ootie.discord.commands.player;

import java.util.List;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import ootie.discord.commands.GameStateSubcommand;
import ootie.game.Game;
import ootie.game.Player;
import ootie.helpers.Constants;
import ootie.helpers.PlayerHelper;
import ootie.message.MessageHelper;
import ootie.service.player.PlayerStatsService;

class Stats extends GameStateSubcommand {

    Stats() {
        super(Constants.STATS, "Player Stats: Command tokens, trade goods, commodities", true, true);
        addOptions(new OptionData(
                        OptionType.STRING, Constants.DUCATS, "Ducat count - can use +1/-1 etc. to add/subtract"))
                .addOptions(new OptionData(
                        OptionType.STRING,
                        Constants.ADMIN_POWER,
                        "Admin power count - can use +1/-1 etc. to add/subtract"))
                .addOptions(new OptionData(
                        OptionType.STRING,
                        Constants.MILITARY_POWER,
                        "Military power count - can use +1/-1 etc. to add/subtract"))
                .addOptions(new OptionData(
                        OptionType.STRING,
                        Constants.DIPLO_POWER,
                        "Diplomatic power count - can use +1/-1 etc. to add/subtract"))
                .addOptions(new OptionData(
                        OptionType.STRING, Constants.STABILITY, "Stability count - can use +1/-1 etc. to add/subtract"))
                .addOptions(new OptionData(OptionType.STRING, Constants.STATE_RELIGION, "State religion"))
                .addOptions(new OptionData(OptionType.STRING, Constants.COLOR, "Color"))
                .addOptions(new OptionData(OptionType.BOOLEAN, Constants.PASSED, "Set whether player has passed y/n"))
                .addOptions(new OptionData(OptionType.BOOLEAN, Constants.BOT, "Set whether player is a bot"))
                .addOptions(new OptionData(
                        OptionType.BOOLEAN, Constants.CHANGED_FOCUS, "Set whether player has changed focus"))
                .addOptions(new OptionData(OptionType.USER, Constants.PLAYER, "Player for which you set stats"))
                .addOptions(new OptionData(
                                OptionType.STRING, Constants.FACTION_COLOR, "Set stats for another Faction or Color")
                        .setAutoComplete(true));
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        Game game = getGame();
        Player player = getPlayer();

        List<OptionMapping> optionMappings = event.getOptions();
        optionMappings.remove(event.getOption(Constants.PLAYER));
        optionMappings.remove(event.getOption(Constants.FACTION_COLOR));
        // NO OPTIONS SELECTED, JUST DISPLAY STATS

        if (optionMappings.isEmpty()) {
            PlayerHelper.drawPlayerArea(player, event);
            return;
        }

        MessageHelper.sendMessageToEventChannel(event, player.getUserName() + " player stats changed:");

        OptionMapping ducats = event.getOption(Constants.DUCATS);
        if (ducats != null) {
            PlayerStatsService.setValue(event, game, player, ducats, player::setDucats, player::getDucats);
        }

        OptionMapping diploPower = event.getOption(Constants.DIPLO_POWER);
        if (diploPower != null) {
            PlayerStatsService.setValue(event, game, player, diploPower, player::setDiploPower, player::getDiploPower);
        }
        OptionMapping adminPower = event.getOption(Constants.ADMIN_POWER);
        if (adminPower != null) {
            PlayerStatsService.setValue(event, game, player, adminPower, player::setAdminPower, player::getAdminPower);
        }
        OptionMapping militaryPower = event.getOption(Constants.MILITARY_POWER);
        if (militaryPower != null) {
            PlayerStatsService.setValue(
                    event, game, player, militaryPower, player::setMilitaryPower, player::getMilitaryPower);
        }

        OptionMapping stability = event.getOption(Constants.STABILITY);
        if (stability != null) {
            PlayerStatsService.setValue(event, game, player, stability, player::setStability, player::getStability);
        }

        OptionMapping stateReligion = event.getOption(Constants.STATE_RELIGION);
        if (stateReligion != null) {
            String value = stateReligion.getAsString();
            player.setStateReligion(value);
            MessageHelper.sendMessageToEventChannel(event, ">  set **" + stateReligion.getName() + "** to " + value);
        }

        OptionMapping color = event.getOption(Constants.COLOR);
        if (color != null) {
            String value = color.getAsString();
            player.setColor(value);
            MessageHelper.sendMessageToEventChannel(event, ">  set **" + color.getName() + "** to " + value);
        }

        OptionMapping optionPassed = event.getOption(Constants.PASSED);
        if (optionPassed != null) {
            StringBuilder message = new StringBuilder(getGeneralMessage(optionPassed));
            boolean value = optionPassed.getAsBoolean();
            player.setPassed(value);
            MessageHelper.sendMessageToEventChannel(event, message.toString());
        }

        OptionMapping optionChangedFocus = event.getOption(Constants.CHANGED_FOCUS);
        if (optionChangedFocus != null) {
            StringBuilder message = new StringBuilder(getGeneralMessage(optionChangedFocus));
            boolean value = optionChangedFocus.getAsBoolean();
            player.setChangedNatFocus(value);
            MessageHelper.sendMessageToEventChannel(event, message.toString());
        }
    }

    private static String getGeneralMessage(OptionMapping option) {
        return ">  set **" + option.getName() + "** to " + option.getAsString();
    }
}
