package ootie.discord.commands.search;

import java.util.List;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import ootie.helpers.Constants;
import ootie.image.Mapper;
import ootie.logging.BotLogger;
import ootie.model.Source.ComponentSource;
import org.apache.commons.lang3.function.Consumers;

class SearchEventsSubcommand extends SearchComponentModelSubcommand {

    public SearchEventsSubcommand() {
        super(Constants.SEARCH_EVENTS, "List all events");
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        String searchString = event.getOption(Constants.SEARCH, null, OptionMapping::getAsString);
        ComponentSource source =
                ComponentSource.fromString(event.getOption(Constants.SOURCE, null, OptionMapping::getAsString));

        if (Mapper.isValidEvent(searchString)) {
            event.getChannel()
                    .sendMessageEmbeds(Mapper.getEvent(searchString).getRepresentationEmbed())
                    .queue(Consumers.nop(), BotLogger::catchRestError);
            return;
        }

        List<MessageEmbed> messageEmbeds = Mapper.getEvents().values().stream()
                .filter(model -> model.search(searchString, source))
                .map(model -> model.getRepresentationEmbed(true))
                .toList();
        SearchHelper.sendSearchEmbedsToEventChannel(event, messageEmbeds);
    }
}
