package ootie.discord.listeners;

import java.util.Arrays;
import java.util.List;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import ootie.discord.commands.Command;
import ootie.discord.commands.ParentCommand;
import ootie.discord.commands.SlashCommandManager;
import ootie.executors.ExecutionLockType;
import ootie.executors.ExecutorServiceManager;
import ootie.helpers.Constants;
import ootie.logging.BotLogger;
import ootie.logging.RollbarManager;
import ootie.service.game.GameNameService;

import org.apache.commons.lang3.function.Consumers;

class SlashCommandListener extends ListenerAdapter implements CommandListener {

    private static final List<String> SLASHCOMMANDS_WITH_MODALS = Arrays.asList(
            Constants.ADD_TILE_LIST,
            Constants.ADD_TILE_LIST_RANDOM,
            Constants.EDIT_TRACK_RECORD,
            Constants.IMPORT_MAP_JSON);

    @Override
    public void onSlashCommandInteraction(@Nonnull SlashCommandInteractionEvent event) {
        if (!canReceiveCommands(event)) return;

        if (!isModalCommand(event)) {
            Command<SlashCommandInteractionEvent> command = getCommand(event);
            event.getInteraction()
                    .deferReply(command.isEphemeral(event))
                    .queue(Consumers.nop(), BotLogger::catchRestError);
        }

        queue(event);
    }

    private void queue(SlashCommandInteractionEvent event) {
        Command<SlashCommandInteractionEvent> command = getCommand(event);
        ExecutionLockType lockType = getLockType(command);
        String eventString = eventToString(event);
        if (lockType == null) {
            ExecutorServiceManager.runAsync(eventString, () -> process(event));
            return;
        }
        String gameName = GameNameService.getGameName(event);
        ExecutorServiceManager.runAsyncWithLock(
                eventString, gameName, event.getMessageChannel(), () -> process(event), lockType);
    }

    public String eventToString(GenericCommandInteractionEvent event) {
        String gameName = GameNameService.getGameName(event);
        return "SlashCommandListener task for `" + event.getUser().getEffectiveName() + "`"
                + (gameName == null ? "" : " in `" + gameName + "`")
                + ": `"
                + event.getCommandString() + "`";
    }

    private void process(SlashCommandInteractionEvent event) {
        long processStartTime = System.currentTimeMillis();
        RollbarManager.putInteractionMetadata("slash_command", event);
        RollbarManager.put("command_name", event.getCommandString());
        RollbarManager.put("game_name", GameNameService.getGameName(event));

        ParentCommand command = SlashCommandManager.getCommand(event.getName());
        Command<SlashCommandInteractionEvent> resolvedCommand = getCommand(event);
        try {
            if (command.accept(event)) {
                command.preExecute(event);
                logSlashCommand(event);
                command.execute(event);
                command.postExecute(event);
                if (!isModalCommand(event) && !resolvedCommand.isEphemeral(event)) {
                    event.getHook().deleteOriginal().queue(Consumers.nop(), BotLogger::catchRestError);
                }
            }
        } catch (Exception e) {
            command.onException(event, e);
        } finally {
            RollbarManager.clear();
        }

        warnForLongRunningCommands(event, processStartTime);
    }

    private static boolean isModalCommand(SlashCommandInteractionEvent event) {
        return SLASHCOMMANDS_WITH_MODALS.contains(event.getInteraction().getSubcommandName());
    }

    private static void logSlashCommand(SlashCommandInteractionEvent event) {
        Member member = event.getMember();
        if (member == null) return;

        var command = SlashCommandManager.getCommand(event.getInteraction().getName());
        String susPrefix = command.isSuspicious(event) ? "sus" : "notSus";
        String commandText =
                "```" + susPrefix + "\n" + member.getEffectiveName() + " used " + event.getCommandString() + "\n```";
        if (!event.getCommandString().contains("/rules ask")
                && !event.getCommandString().contains("/fow whisper")
                && !event.getCommandString().contains("/bothelper impersonate")) {
            event.getChannel()
                    .sendMessage(commandText)
                    .queue(
                            m -> {
                                BotLogger.logSlashCommand(event, m);
                            },
                            BotLogger::catchRestError);
        }
    }

    private static ExecutionLockType getLockType(Command<SlashCommandInteractionEvent> command) {
        if (command instanceof GameStateContainer gameStateContainer) {
            return gameStateContainer.isSaveGame() ? ExecutionLockType.WRITE : ExecutionLockType.READ;
        }
        return null;
    }

    private static Command<SlashCommandInteractionEvent> getCommand(SlashCommandInteractionEvent event) {
        ParentCommand command = SlashCommandManager.getCommand(event.getName());
        Command<SlashCommandInteractionEvent> subcommand = command.getSubcommand(event);
        return subcommand == null ? command : subcommand;
    }
}
