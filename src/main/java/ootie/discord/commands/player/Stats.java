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
import ootie.message.MessageHelper;
import ootie.service.player.PlayerStatsService;

class Stats extends GameStateSubcommand {

    Stats() {
        super(Constants.STATS, "Player Stats: Command tokens, trade goods, commodities", true, true);
        addOptions(new OptionData(
                        OptionType.STRING, Constants.DUCATS, "Ducat count - can use +1/-1 etc. to add/subtract"))
                .addOptions(new OptionData(OptionType.STRING, Constants.PASSED, "Set whether player has passed y/n"))
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

        if (optionMappings.isEmpty()) return;

        MessageHelper.sendMessageToEventChannel(event, player.getUserName() + " player stats changed:");

        OptionMapping ducats = event.getOption(Constants.DUCATS);
        if (ducats != null) {
            PlayerStatsService.setValue(event, game, player, ducats, player::setDucats, player::getDucats);
        }

        OptionMapping optionPassed = event.getOption(Constants.PASSED);
        if (optionPassed != null) {
            StringBuilder message = new StringBuilder(getGeneralMessage(optionPassed));
            String value = optionPassed.getAsString().toLowerCase();
            if ("y".equals(value) || "yes".equals(value)) {
                player.setPassed(true);
            } else if ("n".equals(value) || "no".equals(value)) {
                player.setPassed(false);
            } else {
                message.append(", which is not a valid input. Please use one of `y` `yes` `n` or `no`.");
            }
            MessageHelper.sendMessageToEventChannel(event, message.toString());
        }
    }

    private static String getGeneralMessage(OptionMapping option) {
        return ">  set **" + option.getName() + "** to " + option.getAsString();
    }
}
