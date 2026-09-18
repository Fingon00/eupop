package ootie.discord.commands.search;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import ootie.helpers.Constants;

class SearchRules extends SearchComponentModelSubcommand {

    SearchRules() {
        super(Constants.SEARCH_RULES, "List various rules sections from the LRR or other homebrew sources.");
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        String searchString = event.getOption(Constants.SEARCH, null, OptionMapping::getAsString);
    }
}
