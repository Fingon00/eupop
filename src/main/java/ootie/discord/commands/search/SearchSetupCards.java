package ootie.discord.commands.search;

import java.util.List;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import ootie.helpers.Constants;
import ootie.image.Mapper;
import ootie.model.SetupCardModel;

class SearchSetupCards extends SearchModelSubcommand<SetupCardModel> {

    SearchSetupCards() {
        super(
                Constants.SEARCH_SETUP_CARDS,
                "Search for a setup card",
                () -> List.copyOf(Mapper.getSetupCards().values()));
    }

    @Override
    protected void sendResult(SetupCardModel model, SlashCommandInteractionEvent event) {
        SearchImageHelper.sendImage(event, "images/Setup Cards/", model.getId(), model.getRealm());
    }
}
