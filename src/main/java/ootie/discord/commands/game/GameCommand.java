package ootie.discord.commands.game;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import ootie.discord.commands.ParentCommand;
import ootie.discord.commands.Subcommand;
import ootie.helpers.Constants;

public class GameCommand implements ParentCommand {

    private final Map<String, Subcommand> subcommands =
            Stream.of(new CreateGameButton()).collect(Collectors.toMap(Subcommand::getName, subcommand -> subcommand));

    @Override
    public String getName() {
        return Constants.GAME;
    }

    @Override
    public String getDescription() {
        return "Game";
    }

    @Override
    public Map<String, Subcommand> getSubcommands() {
        return subcommands;
    }
}
