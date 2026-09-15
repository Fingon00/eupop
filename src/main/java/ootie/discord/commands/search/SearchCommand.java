package ootie.discord.commands.search;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import ootie.discord.commands.ParentCommand;
import ootie.discord.commands.Subcommand;
import ootie.helpers.Constants;

public class SearchCommand implements ParentCommand {

    private final Map<String, Subcommand> subcommands = Stream.of(new SearchEventsSubcommand())
            .collect(Collectors.toMap(Subcommand::getName, subcommand -> subcommand));

    @Override
    public String getName() {
        return Constants.SEARCH;
    }

    @Override
    public String getDescription() {
        return "Search game component descriptions";
    }

    @Override
    public Map<String, Subcommand> getSubcommands() {
        return subcommands;
    }

    private final Map<String, Subcommand> searchSubcommands =
            Stream.of(new SearchRules()).collect(Collectors.toMap(Subcommand::getName, subcommand -> subcommand));

    @Override
    public Map<String, Subcommand> getSearchSubcommands() {
        return searchSubcommands;
    }
}
