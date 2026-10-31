package ootie.discord.commands.search;

import java.util.List;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import ootie.helpers.Constants;
import ootie.image.Mapper;
import ootie.model.IdeaModel;

class SearchIdeas extends SearchModelSubcommand<IdeaModel> {

    SearchIdeas() {
        super(
                Constants.SEARCH_IDEAS,
                "Search for an idea",
                () -> List.copyOf(Mapper.getIdeas().values()));
    }

    @Override
    protected void sendResult(IdeaModel model, SlashCommandInteractionEvent event) {
        SearchImageHelper.sendImage(event, "images/Ideas/", model.getId(), model.getName());
    }
}
