package ootie.discord.commands;

import java.util.List;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import ootie.game.Game;
import ootie.helpers.Constants;
import ootie.helpers.GameImageHelper;
import ootie.message.MessageHelper;

public class ShowGameCommand extends GameStateCommand {

    public ShowGameCommand() {
        super(false, true);
    }

    @Override
    public String getName() {
        return Constants.SHOW_GAME;
    }

    @Override
    public String getDescription() {
        return "Show selected map";
    }

    @Override
    public List<OptionData> getOptions() {
        return List.of(
                new OptionData(OptionType.STRING, Constants.GAME_NAME, "Map name to be shown").setAutoComplete(true));
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        Game game = getGame();
        MessageHelper.sendFileUploadToChannel(event.getChannel(), GameImageHelper.drawMap(game));
    }
}
