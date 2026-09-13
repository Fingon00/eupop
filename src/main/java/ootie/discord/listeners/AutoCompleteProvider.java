package ootie.discord.listeners;

import java.util.Collections;
import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import ootie.game.Game;
import ootie.game.persistence.GameManager;
import ootie.helpers.Constants;
import ootie.logging.BotLogger;
import ootie.logging.LogOrigin;
import ootie.service.game.GameNameService;
import org.apache.commons.lang3.function.Consumers;
import org.jetbrains.annotations.NotNull;

@UtilityClass
class AutoCompleteProvider {

    static void handleAutoCompleteEvent(CommandAutoCompleteInteractionEvent event) {
        try {
            resolveAutoCompleteEvent(event);
        } catch (IllegalArgumentException ignored) {
            // We don't care about these.
        } catch (Exception e) {
            BotLogger.error(new LogOrigin(event), "Error in handleAutoCompleteEvent", e);
        }
    }

    private static void resolveAutoCompleteEvent(CommandAutoCompleteInteractionEvent event) {
        String commandName = event.getName();
        String subCommandName = event.getSubcommandName();
        String optionName = event.getFocusedOption().getName();

        if (Constants.FIND.equals(commandName)) {
            resolveFindAutoComplete(event, optionName);
            if (event.isAcknowledged()) return;
        }

        if (subCommandName != null) {
            switch (commandName) {
                case Constants.DEVELOPER -> resolveDeveloperCommandAutoComplete(event, subCommandName, optionName);
                case Constants.SEARCH, "search2" -> resolveSearchCommandAutoComplete(event, subCommandName, optionName);
            }
            if (event.isAcknowledged()) return;
        }

        String gameName = GameNameService.getGameNameFromChannel(event);
        if (GameManager.isValid(gameName) && subCommandName != null) {
            if (event.isAcknowledged()) return;
        }

        // GENERIC HANDLING OF OPTIONS
        handleOptions(event, optionName, subCommandName, gameName);
        if (!event.isAcknowledged()) {
            event.replyChoices(Collections.emptyList()).queue(Consumers.nop(), BotLogger::catchRestError);
        }
    }

    private static void handleOptions(
            @NotNull CommandAutoCompleteInteractionEvent event,
            @NotNull String optionName,
            String subcommandName,
            String gameName) {
        switch (optionName) {
            case Constants.UNDO_TO_COMMAND -> {
                if (!GameManager.isValid(gameName)) return;
                Game game = GameManager.getManagedGame(gameName).getGame();

                // List<Command.Choice> options =
                //         GameUndoNameService.getUndoNamesToCommandText(game, 25).entrySet().stream()
                //                 .sorted(Map.Entry.comparingByKey(
                //                         Comparator.comparing(GameUndoNameService::getUndoNumberFromFileName)
                //                                 .reversed()))
                //                 .limit(25)
                //                 .map(entry ->
                //                         new Command.Choice(StringUtils.left(entry.getValue(), 100), entry.getKey()))
                //                 .toList();
                // event.replyChoices(options).queue(Consumers.nop(), BotLogger::catchRestError);
            }
        }
    }

    private static void resolveDeveloperCommandAutoComplete(
            @NotNull CommandAutoCompleteInteractionEvent event,
            @NotNull String subCommandName,
            @NotNull String optionName) {
        switch (subCommandName) {

            // case Constants.RUN_CRON -> {
            //     if (!Constants.CRON_NAME.equals(optionName)) return;
            //     replyWith25ChoicesThatContainValue(event, CronManager.getCronNames());
            // }
            default -> {}
        }
    }

    private static void resolveSearchCommandAutoComplete(
            @NotNull CommandAutoCompleteInteractionEvent event,
            @NotNull String subCommandName,
            @NotNull String optionName) {
        if (!Constants.SEARCH.equals(optionName)) return;
        switch (subCommandName) {
        }
    }

    private static void resolveFindAutoComplete(
            @NotNull CommandAutoCompleteInteractionEvent event, @NotNull String optionName) {}

    // private static void resolveEventAutoComplete(
    //         CommandAutoCompleteInteractionEvent event, String subCommandName, String optionName, String gameName) {
    //     if (!Constants.EVENT_PLAY.equals(subCommandName)) return;
    //     if (!Constants.EVENT_ID.equals(optionName)) return;
    //     Game game = GameManager.getManagedGame(gameName).getGame();
    //     Player player = CommandHelper.getPlayerFromGame(
    //             game, event.getMember(), event.getUser().getId());
    //     String enteredValue = event.getFocusedOption().getValue().toLowerCase();
    //     Map<String, Integer> events = new HashMap<>(player.getEvents());
    //     List<Command.Choice> options = events.entrySet().stream()
    //             .filter(entry -> entry.getKey().contains(enteredValue))
    //             .limit(25)
    //             .map(entry -> new Command.Choice(entry.getValue() + " " + entry.getKey(), entry.getValue()))
    //             .collect(Collectors.toList());
    //     event.replyChoices(options).queue(Consumers.nop(), BotLogger::catchRestError);
    // }

    // private static void replyWith25ChoicesThatContainValue(
    //         CommandAutoCompleteInteractionEvent event, Collection<String> toFilter) {
    //     String enteredValue = event.getFocusedOption().getValue();
    //     var options = mapTo25ChoicesThatContain(toFilter, enteredValue);
    //     event.replyChoices(options).queue(Consumers.nop(), BotLogger::catchRestError);
    // }

    // private static List<Command.Choice> mapTo25ChoicesThatContain(Collection<String> toFilter, String toContain) {
    //     return toFilter.stream()
    //             .filter(string -> string.toLowerCase().contains(toContain.toLowerCase()))
    //             .sorted()
    //             .limit(25)
    //             .map(string -> new Command.Choice(string, string))
    //             .toList();
    // }
}
