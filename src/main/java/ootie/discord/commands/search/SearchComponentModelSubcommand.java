package ootie.discord.commands.search;

import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import ootie.discord.commands.Subcommand;
import ootie.helpers.Constants;

abstract class SearchComponentModelSubcommand extends Subcommand {

    SearchComponentModelSubcommand(String name, String description) {
        super(name, description);
        addOptions(new OptionData(
                        OptionType.STRING,
                        Constants.SEARCH,
                        "Searches the text and limits results to those containing this string.")
                .setAutoComplete(true));
        addOptions(new OptionData(OptionType.STRING, Constants.SOURCE, "Limit results to a specific source.")
                .setAutoComplete(true));
    }
}
