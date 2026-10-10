package ootie.discord.commands.search;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import ootie.helpers.Constants;
import ootie.image.Mapper;
import ootie.message.MessageHelper;

class SearchCharacters extends SearchComponentModelSubcommand {

    public SearchCharacters() {
        super(Constants.SEARCH_CHARACTERS, "List all events");
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        String searchString = event.getOption(Constants.SEARCH, null, OptionMapping::getAsString);

        if (Mapper.getCharacters().containsKey(searchString)) {
            Mapper.getEvent(searchString).drawEventImage(event.getChannel());
            return;
        }else{
            MessageHelper.sendMessageToChannel(event.getChannel(), "Cannot find "+searchString);
        }
    }
}
