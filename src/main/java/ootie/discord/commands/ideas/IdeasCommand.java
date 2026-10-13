package ootie.discord.commands.ideas;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import ootie.discord.commands.ParentCommand;
import ootie.discord.commands.Subcommand;
import ootie.helpers.Constants;

public class IdeasCommand implements ParentCommand {

    private final Map<String, Subcommand> subcommands =
            Stream.of(new IdeasShow()).collect(Collectors.toMap(Subcommand::getName, subcommand -> subcommand));

    @Override
    public String getName() {
        return Constants.IDEAS;
    }

    @Override
    public String getDescription() {
        return "Ideas";
    }

    @Override
    public Map<String, Subcommand> getSubcommands() {
        return subcommands;
    }
}
