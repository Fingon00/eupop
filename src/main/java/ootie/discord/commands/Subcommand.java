package ootie.discord.interactions.commands;

import org.jetbrains.annotations.NotNull;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

public abstract class Subcommand extends SubcommandData implements Command<SlashCommandInteractionEvent> {

    protected Subcommand(@NotNull String name, @NotNull String description) {
        super(name, description);
    }

    public boolean accept(SlashCommandInteractionEvent event) {
        return getName().equals(event.getInteraction().getSubcommandName());
    }

    public abstract void execute(SlashCommandInteractionEvent event);
}
