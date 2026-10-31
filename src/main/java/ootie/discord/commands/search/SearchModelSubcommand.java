package ootie.discord.commands.search;

import java.util.List;
import java.util.function.Supplier;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import ootie.helpers.Constants;
import ootie.message.MessageHelper;
import ootie.model.EmbeddableModel;
import ootie.model.Source.ComponentSource;

abstract class SearchModelSubcommand<T extends EmbeddableModel> extends SearchComponentModelSubcommand {

    private final Supplier<List<T>> models;

    SearchModelSubcommand(String name, String description, Supplier<List<T>> models) {
        super(name, description);
        this.models = models;
    }

    protected abstract void sendResult(T model, SlashCommandInteractionEvent event);

    @Override
    public final void execute(SlashCommandInteractionEvent event) {
        String searchString = event.getOption(Constants.SEARCH, null, OptionMapping::getAsString);
        ComponentSource source =
                ComponentSource.fromString(event.getOption(Constants.SOURCE, null, OptionMapping::getAsString));

        if (searchString == null || searchString.isBlank()) {
            MessageHelper.sendMessageToChannel(event.getChannel(), "Enter a search term.");
            return;
        }

        List<T> components = models.get();
        T exactMatch = components.stream()
                .filter(model -> searchString.equalsIgnoreCase(model.getAlias()))
                .filter(model -> model.searchSource(source))
                .findFirst()
                .orElse(null);
        if (exactMatch != null) {
            sendResult(exactMatch, event);
            return;
        }

        List<T> matches = components.stream()
                .filter(model -> model.searchSource(source))
                .filter(model -> model.search(searchString)
                        || (model.getAlias() != null
                                && model.getAlias().toLowerCase().contains(searchString.toLowerCase())))
                .limit(25)
                .toList();
        if (matches.isEmpty()) {
            MessageHelper.sendMessageToChannel(event.getChannel(), "No results found for " + searchString);
        } else if (matches.size() == 1) {
            sendResult(matches.getFirst(), event);
        } else {
            String results = matches.stream()
                    .limit(15)
                    .map(EmbeddableModel::getAutoCompleteName)
                    .collect(java.util.stream.Collectors.joining("\n"));
            MessageHelper.sendMessageToChannel(
                    event.getChannel(),
                    "Multiple results found. Select a specific result from autocomplete:\n" + results);
        }
    }
}
