package ootie.discord.commands.search;

import java.util.List;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import ootie.helpers.Constants;
import ootie.image.Mapper;
import ootie.message.MessageHelper;
import ootie.model.Source.ComponentSource;

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
            Mapper.getEvent(searchString).drawEventImage(event.getChannel());
            // event.getChannel()
            //         .sendMessageEmbeds(Mapper.getEvent(searchString).getRepresentationEmbed())
            //         .queue(Consumers.nop(), BotLogger::catchRestError);
            return;
        }

        List<String> messageEmbeds = Mapper.getEvents().values().stream()
                .filter(model -> model.search(searchString, source))
                .map(model -> model.getID())
                .toList();
        if (messageEmbeds.size() > 15) {
            if (event.getMessageChannel() instanceof ThreadChannel thread) {
                if (thread.getParentChannel() instanceof TextChannel) {
                } else {
                    MessageHelper.sendMessageToChannel(
                            event.getMessageChannel(),
                            "Sorry at this time, more than 15 events in a non-text channel is not allowed");
                    return;
                }
            }
        }
        if (messageEmbeds.size() > 50) {
            MessageHelper.sendMessageToChannel(
                    event.getMessageChannel(), "Sorry at this time, more than 50 events in one search is not allowed");
            return;
        }
        SearchHelper.sendSearchEventsToEventChannel(event, messageEmbeds);
    }
}
