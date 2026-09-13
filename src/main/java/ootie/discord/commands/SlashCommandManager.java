package ootie.discord.commands;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.experimental.UtilityClass;
import ootie.discord.commands.search.SearchCommand;

@UtilityClass
public class SlashCommandManager {

    private static final Map<String, ParentCommand> commands =
            Stream.of(new SearchCommand()).collect(Collectors.toMap(ParentCommand::getName, command -> command));

    public static ParentCommand getCommand(String name) {
        return commands.get(name);
    }

    public static Collection<ParentCommand> getCommands() {
        return List.copyOf(commands.values());
    }
}
