package ootie.discord.commands.search;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import ootie.helpers.Constants;
import ootie.helpers.GameImageHelper;
import ootie.message.MessageHelper;

class SearchLocations extends SearchComponentModelSubcommand {

    public SearchLocations() {
        super(Constants.SEARCH_LOCATIONS, "Search for a particular province, area, or sea-zone");
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        String searchString = event.getOption(Constants.SEARCH, null, OptionMapping::getAsString);

        MessageHelper.sendFileUploadToChannel(event.getChannel(), GameImageHelper.drawSpecificLocation(searchString));
    }
}
