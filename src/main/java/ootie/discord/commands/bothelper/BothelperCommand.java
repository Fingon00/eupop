package ootie.discord.commands.bothelper;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import ootie.discord.JdaService;
import ootie.discord.commands.CommandHelper;
import ootie.discord.commands.ParentCommand;
import ootie.discord.commands.Subcommand;
import ootie.helpers.Constants;

public class BothelperCommand implements ParentCommand {

    private final Map<String, Subcommand> subcommands = Stream.of(new CreateGameChannels())
            .collect(Collectors.toMap(Subcommand::getName, subcommand -> subcommand));

    @Override
    public boolean accept(SlashCommandInteractionEvent event) {
        return ParentCommand.super.accept(event) && CommandHelper.acceptIfHasRoles(event, JdaService.bothelperRoles);
    }

    @Override
    public String getName() {
        return Constants.BOTHELPER;
    }

    @Override
    public String getDescription() {
        return "BotHelper";
    }

    @Override
    public Map<String, Subcommand> getSubcommands() {
        return subcommands;
    }
}
