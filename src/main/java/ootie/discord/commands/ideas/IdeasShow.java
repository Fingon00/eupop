package ootie.discord.commands.ideas;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.utils.FileUpload;
import ootie.discord.commands.GameStateSubcommand;
import ootie.game.Game;
import ootie.helpers.Constants;
import ootie.helpers.GameImageHelper;
import ootie.message.MessageHelper;

class IdeasShow extends GameStateSubcommand {

    IdeasShow() {
        super(Constants.SHOW, "Show the current ideas", false, false);
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        Game game = getGame();
        FileUpload fileUpload = GameImageHelper.drawIdeas(game);
        MessageHelper.sendFileUploadToChannel(event.getChannel(), fileUpload);
    }
}
