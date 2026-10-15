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
import ootie.image.Mapper;
import ootie.message.MessageHelper;

class AddProvince extends GameStateSubcommand {

    AddProvince() {
        super(Constants.ADD_PROVINCE, "Add province", true, true);
        addOptions(new OptionData(OptionType.STRING, Constants.PROVINCE, "province name")
                .setAutoComplete(true)
                .setRequired(true));
        addOptions(new OptionData(OptionType.STRING, Constants.SIZE, "Size of town, default small")
                .setAutoComplete(true)
                .setRequired(false));
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        Game game = getGame();
        Player player = getPlayer();
        String province = event.getOption(Constants.PROVINCE).getAsString();
        String size = "small";
        if (event.getOption(Constants.SIZE) != null) {
            size = event.getOption(Constants.SIZE).getAsString();
        }

        if (Mapper.getProvinces().containsKey(province)) {
            if ("small".equalsIgnoreCase(size)) {
                player.getSmallProvinces().add(province);
            } else {
                player.getLargeProvinces().add(province);
            }
        }

        PlayerHelper.drawPlayerArea(player, event);
        MessageHelper.sendFileUploadToChannel(event.getChannel(), GameImageHelper.drawMap(game));
    }
}
