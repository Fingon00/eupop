package ootie.discord.commands.search;

import java.util.List;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import ootie.helpers.Constants;
import ootie.image.Mapper;
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
        SearchImageHelper.sendImage(event, "images/Trade Cards/", model.getId(), model.getName());
    }
}
