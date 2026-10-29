package ootie.discord.commands.player;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import ootie.discord.commands.GameStateSubcommand;
import ootie.game.Game;
import ootie.game.Player;
import ootie.helpers.Constants;
import ootie.helpers.GameImageHelper;
import ootie.helpers.PlayerHelper;
import ootie.message.MessageHelper;

class ManageProvince extends GameStateSubcommand {

    ManageProvince() {
        super(Constants.MANAGE_PROVINCE, "Manage province", true, true);
        addOptions(new OptionData(OptionType.STRING, Constants.PROVINCE, "Province name")
                .setAutoComplete(true)
                .setRequired(true));
        addOptions(new OptionData(OptionType.STRING, Constants.PROVINCE_ACTION, "Action to do")
                .setAutoComplete(true)
                .setRequired(true));
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        Game game = getGame();
        Player player = getPlayer();
        String province = event.getOption(Constants.PROVINCE).getAsString();
        String action = event.getOption(Constants.PROVINCE_ACTION).getAsString();

        switch (action) {
            case "make_small" -> {
                player.getSmallProvinces().add(province);
                player.getLargeProvinces().remove(province);
            }
            case "make_large" -> {
                player.getSmallProvinces().remove(province);
                player.getLargeProvinces().add(province);
            }
            case "add_unrest" -> {
                player.getUnrestProvinces().add(province);
            }
            case "remove_unrest" -> {
                player.getUnrestProvinces().remove(province);
            }
            case "add" -> {
                player.getSmallProvinces().add(province);
            }
            case "remove" -> {
                player.getLargeProvinces().remove(province);
                player.getSmallProvinces().remove(province);
                player.getUnrestProvinces().remove(province);
            }
        }

        PlayerHelper.drawPlayerArea(player, event);
        MessageHelper.sendFileUploadToChannel(event.getChannel(), GameImageHelper.drawMap(game));
    }
}
