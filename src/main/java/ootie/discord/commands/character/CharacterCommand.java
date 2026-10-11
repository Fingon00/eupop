package ootie.discord.commands.character;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import ootie.discord.commands.ParentCommand;
import ootie.discord.commands.Subcommand;
import ootie.helpers.Constants;

public class CharacterCommand implements ParentCommand {

    private final Map<String, Subcommand> subcommands = Stream.of(new CharacterAdd(), new CharacterRemove())
            .collect(Collectors.toMap(Subcommand::getName, subcommand -> subcommand));

    @Override
    public String getName() {
        return Constants.CHARACTER;
    }

    @Override
    public String getDescription() {
        return "Character";
    }

    @Override
    public Map<String, Subcommand> getSubcommands() {
        return subcommands;
    }
}
