package ootie.discord.commands.events;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import ootie.discord.commands.ParentCommand;
import ootie.discord.commands.Subcommand;
import ootie.helpers.Constants;

public class EventsCommand implements ParentCommand {

    private final Map<String, Subcommand> subcommands =
            Stream.of(new EventsShow()).collect(Collectors.toMap(Subcommand::getName, subcommand -> subcommand));

    @Override
    public String getName() {
        return Constants.EVENTS;
    }

    @Override
    public String getDescription() {
        return "Events";
    }

    @Override
    public Map<String, Subcommand> getSubcommands() {
        return subcommands;
    }
}
