package ootie.discord.commands.search;

import java.util.List;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import ootie.helpers.Constants;
import ootie.image.Mapper;
import ootie.model.MilestoneModel;
import ootie.model.Source.ComponentSource;

class SearchMilestones extends SearchModelSubcommand<MilestoneModel> {

    SearchMilestones() {
        super(
                Constants.SEARCH_MILESTONES,
                "Search for a milestone",
                () -> List.copyOf(Mapper.getMilestones().values()));
    }

    @Override
    protected void sendResult(MilestoneModel model, SlashCommandInteractionEvent event) {
        String age =
                switch (model.getAge()) {
                    case 1 -> "I";
                    case 2 -> "II";
                    case 3 -> "III";
                    case 4 -> "IV";
                    default -> throw new IllegalArgumentException("Invalid milestone age: " + model.getAge());
                };
        String folder = model.getSource() == ComponentSource.defying_destiny
                ? "images/Milestones/DD Milestones Age " + age
                : "images/Milestones/Milestones_Age_" + age;
        SearchImageHelper.sendImage(event, folder, model.getId(), model.getId());
    }
}
