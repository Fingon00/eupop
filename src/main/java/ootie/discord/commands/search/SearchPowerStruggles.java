package ootie.discord.commands.search;

import java.util.List;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import ootie.helpers.Constants;
import ootie.image.Mapper;
import ootie.model.PowerStruggleModel;

class SearchPowerStruggles extends SearchModelSubcommand<PowerStruggleModel> {

    SearchPowerStruggles() {
        super(
                Constants.SEARCH_POWER_STRUGGLES,
                "Search for a power struggle",
                () -> List.copyOf(Mapper.getPowerStruggles().values()));
    }

    @Override
    protected void sendResult(PowerStruggleModel model, SlashCommandInteractionEvent event) {
        SearchImageHelper.sendImage(event, "images/Power Struggles/", model.getId(), model.getName());
    }
}
