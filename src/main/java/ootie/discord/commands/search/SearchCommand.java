package ootie.discord.commands.search;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import ootie.discord.commands.ParentCommand;
import ootie.discord.commands.Subcommand;
import ootie.helpers.Constants;

public class SearchCommand implements ParentCommand {

    private final List<Subcommand> searchSubcommandList = List.of(
            new SearchEventsSubcommand(),
            new SearchCharacters(),
            new SearchLocations(),
            new SearchMilestones(),
            new SearchActionCards(),
            new SearchIdeas(),
            new SearchSetupCards(),
            new SearchTradeCards(),
            new SearchPowerStruggles());

    private final Map<String, Subcommand> subcommands =
            searchSubcommandList.stream().collect(Collectors.toMap(Subcommand::getName, subcommand -> subcommand));

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
            searchSubcommandList.stream().collect(Collectors.toMap(Subcommand::getName, subcommand -> subcommand));

    @Override
    public Map<String, Subcommand> getSearchSubcommands() {
        return searchSubcommands;
    }
}
