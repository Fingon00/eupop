package ootie.discord.commands.events;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.utils.FileUpload;
import ootie.discord.commands.GameStateSubcommand;
import ootie.game.Game;
import ootie.helpers.Constants;
import ootie.helpers.GameImageHelper;
import ootie.message.MessageHelper;

class EventsShow extends GameStateSubcommand {

    EventsShow() {
        super(Constants.SHOW, "Show the current events", false, false);
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        Game game = getGame();
        FileUpload fileUpload = GameImageHelper.drawEvents(game);
        MessageHelper.sendFileUploadToChannel(event.getChannel(), fileUpload);
    }
}
