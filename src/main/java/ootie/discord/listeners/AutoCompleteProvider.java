package ootie.discord.listeners;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import ootie.game.Game;
import ootie.game.persistence.GameManager;
import ootie.helpers.Constants;
import ootie.image.Mapper;
import ootie.logging.BotLogger;
import ootie.logging.LogOrigin;
import ootie.model.EmbeddableModel;
import ootie.model.ModelInterface;
import ootie.model.Source.ComponentSource;
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

        if (subCommandName != null) {
            switch (commandName) {
                case Constants.DEVELOPER -> resolveDeveloperCommandAutoComplete(event, subCommandName, optionName);
                case Constants.SEARCH -> resolveSearchCommandAutoComplete(event, subCommandName, optionName);
            }
            if (event.isAcknowledged()) return;
        }

        // String gameName = GameNameService.getGameNameFromChannel(event);
        // if (GameManager.isValid(gameName) && subCommandName != null) {
        //     if (event.isAcknowledged()) return;
        // }

        // GENERIC HANDLING OF OPTIONS
        handleOptions(event, optionName, subCommandName, "none");
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
            case Constants.SOURCE -> {
                String enteredValue = event.getFocusedOption().getValue();
                List<Command.Choice> options = Stream.of(ComponentSource.values())
                        .filter(token -> token.toString().contains(enteredValue))
                        .limit(25)
                        .map(token -> new Command.Choice(token.toString(), token.toString()))
                        .collect(Collectors.toList());
                event.replyChoices(options).queue(Consumers.nop(), BotLogger::catchRestError);
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
        ComponentSource source =
                ComponentSource.fromString(event.getOption(Constants.SOURCE, null, OptionMapping::getAsString));
        List<Command.Choice> options = null;
        switch (subCommandName) {
            case Constants.SEARCH_EVENTS ->
                options = searchModels(event, Mapper.getEvents().values(), source, true);
        }
        event.replyChoices(Objects.requireNonNullElse(options, Collections.emptyList()))
                .queue(Consumers.nop(), BotLogger::catchRestError);
    }

    private static <T extends ModelInterface & EmbeddableModel> List<Command.Choice> searchModels(
            CommandAutoCompleteInteractionEvent event,
            Collection<T> models,
            ComponentSource source,
            boolean limithomebrew) {
        String enteredValue = event.getFocusedOption().getValue().toLowerCase();
        return models.stream()
                .filter(model -> model.getSource() != null)
                .filter(model -> model.search(enteredValue, source))
                .limit(25)
                .map(model -> new Command.Choice(model.getAutoCompleteName(), model.getAlias()))
                .toList();
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
