package ootie.discord.commands.player;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import ootie.discord.commands.ParentCommand;
import ootie.discord.commands.Subcommand;
import ootie.helpers.Constants;

public class PlayerCommand implements ParentCommand {

    private final Map<String, Subcommand> subcommands = Stream.of(new Stats(), new Setup())
            .collect(Collectors.toMap(Subcommand::getName, subcommand -> subcommand));

    @Override
    public String getName() {
        return Constants.PLAYER;
    }

    @Override
    public String getDescription() {
        return "Player";
    }

    @Override
    public Map<String, Subcommand> getSubcommands() {
        return subcommands;
    }
}
