package ootie.discord.commands.search;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import ootie.helpers.Constants;
import ootie.image.Mapper;
import ootie.logging.BotLogger;
import ootie.model.TradeCardModel;

class SearchTradeCards extends SearchModelSubcommand<TradeCardModel> {

    SearchTradeCards() {
        super(
                Constants.SEARCH_TRADE_CARDS,
                "Search for a trade card",
                () -> List.copyOf(Mapper.getTradeCards().values()));
    }

    @Override
    protected void sendResult(TradeCardModel model, SlashCommandInteractionEvent event) {
        EmbedBuilder embed = new EmbedBuilder()
                .setTitle(model.getName() + " (" + model.getId() + ")")
                .setFooter("Source: " + model.getSource())
                .addField("Trade value thresholds", formatValues(model.getTradeValueThresholds()), false)
                .addField("Regular trade income", formatValues(model.getRegularTradeIncome()), false)
                .addField("Expanded trade income", formatValues(model.getExpandedTradeIncome()), false);

        addTradeNode(embed, model.getTradeNode1());
        addTradeNode(embed, model.getTradeNode2());
        addTradeNode(embed, model.getTradeNode3());
        event.getChannel().sendMessageEmbeds(embed.build()).queue(success -> {}, BotLogger::catchRestError);
    }

    private static String formatValues(int[] values) {
        return values == null ? "Not specified" : Arrays.toString(values);
    }

    private static void addTradeNode(EmbedBuilder embed, ootie.game.TradeNode node) {
        if (node == null) return;

        StringBuilder description = new StringBuilder();
        if (node.getAdditionalRequirements() != null
                && !node.getAdditionalRequirements().isBlank()) {
            description.append(node.getAdditionalRequirements()).append('\n');
        }
        Stream.of(node.getKeyProvince1(), node.getKeyProvince2(), node.getKeyProvince3(), node.getKeyProvince4())
                .filter(province -> province != null)
                .forEach(province -> description
                        .append(province.getName())
                        .append(" (")
                        .append(province.getTradeValue())
                        .append(")\n"));

        embed.addField(
                node.getTradeNodeName() == null ? "Trade node" : node.getTradeNodeName(),
                description.isEmpty() ? "No additional details" : description.toString(),
                false);
    }
}
