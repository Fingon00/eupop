package ootie.discord.commands.search;

import java.util.List;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import ootie.helpers.Constants;
import ootie.image.Mapper;
import ootie.model.ActionCardModel;

class SearchActionCards extends SearchModelSubcommand<ActionCardModel> {

    SearchActionCards() {
        super(
                Constants.SEARCH_ACTION_CARDS,
                "Search for an action card",
                () -> List.copyOf(Mapper.getActionCards().values()));
    }

    @Override
    protected void sendResult(ActionCardModel model, SlashCommandInteractionEvent event) {
        SearchImageHelper.sendImage(event, "images/Action Cards/", model.getId(), model.getName());
    }
}
