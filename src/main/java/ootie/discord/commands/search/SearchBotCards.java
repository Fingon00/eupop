package ootie.discord.commands.search;

import java.util.List;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import ootie.helpers.Constants;
import ootie.image.Mapper;
import ootie.model.BotCardModel;

class SearchBotCards extends SearchModelSubcommand<BotCardModel> {

    SearchBotCards() {
        super(
                Constants.SEARCH_BOT_CARDS,
                "Search for a bot card",
                () -> List.copyOf(Mapper.getBotCards().values()));
    }

    @Override
    protected void sendResult(BotCardModel model, SlashCommandInteractionEvent event) {
        SearchImageHelper.sendImage(event, "images/Bot Cards/", model.getId(), model.getId());
    }
}
