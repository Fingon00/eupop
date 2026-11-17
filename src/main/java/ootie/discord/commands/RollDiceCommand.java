package ootie.discord.commands;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import ootie.helpers.Constants;
import ootie.message.MessageHelper;
import ootie.service.emoji.DiceEmojis;

public class RollDiceCommand implements ParentCommand {

    @Override
    public String getName() {
        return Constants.ROLL_DICE;
    }

    @Override
    public String getDescription() {
        return "Roll dice and display the results";
    }

    @Override
    public List<OptionData> getOptions() {
        return List.of(
                new OptionData(OptionType.STRING, Constants.DICE_TYPE, "Type of die to roll")
                        .setAutoComplete(true)
                        .setRequired(true),
                new OptionData(OptionType.INTEGER, Constants.DICE_COUNT, "Number of dice to roll")
                        .setMinValue(1)
                        .setMaxValue(Constants.MAX_DICE_PER_ROLL)
                        .setRequired(true));
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        String diceType = event.getOption(Constants.DICE_TYPE).getAsString().toLowerCase(Locale.ROOT);
        int diceCount = event.getOption(Constants.DICE_COUNT).getAsInt();

        if (!Constants.DICE_TYPES.contains(diceType)) {
            MessageHelper.sendMessageToChannel(
                    event.getChannel(), "Choose a die type from the autocomplete suggestions.");
            return;
        }
        if (diceCount < 1 || diceCount > Constants.MAX_DICE_PER_ROLL) {
            MessageHelper.sendMessageToChannel(
                    event.getChannel(),
                    "The number of dice must be between 1 and " + Constants.MAX_DICE_PER_ROLL + ".");
            return;
        }

        List<String> results = IntStream.range(0, diceCount)
                .mapToObj(roll -> DiceEmojis.getDieEmoji(
                        diceType, ThreadLocalRandom.current().nextInt(1, 7)))
                .toList();
        String diceNoun = diceCount == 1 ? "die" : "dice";
        MessageHelper.sendMessageToChannel(
                event.getChannel(),
                "Rolled " + diceCount + " " + diceType + " " + diceNoun + ":\n" + String.join(" ", results));
    }
}
