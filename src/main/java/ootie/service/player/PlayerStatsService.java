package ootie.service.player;

import java.util.function.Consumer;
import java.util.function.Supplier;
import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import ootie.game.Game;
import ootie.game.Player;
import ootie.message.MessageHelper;

@UtilityClass
public class PlayerStatsService {

    public void setValue(
            SlashCommandInteractionEvent event,
            Game game,
            Player player,
            OptionMapping option,
            Consumer<Integer> consumer,
            Supplier<Integer> supplier) {
        setValue(event, game, player, option.getName(), consumer, supplier, option.getAsString(), false);
    }

    public void setValue(
            SlashCommandInteractionEvent event,
            Game game,
            Player player,
            OptionMapping option,
            Consumer<Integer> consumer,
            Supplier<Integer> supplier,
            boolean suppressMessage) {
        setValue(event, game, player, option.getName(), consumer, supplier, option.getAsString(), suppressMessage);
    }

    public void setValue(
            SlashCommandInteractionEvent event,
            Game game,
            Player player,
            String optionName,
            Consumer<Integer> consumer,
            Supplier<Integer> supplier,
            String value,
            boolean suppressMessage) {
        try {
            boolean setValue = !value.startsWith("+") && !value.startsWith("-");
            String explanation = "";
            if (value.contains("?")) {
                explanation = value.substring(value.indexOf('?') + 1);
                value = value.substring(0, value.indexOf('?')).replace(" ", "");
            }

            int number = Integer.parseInt(value);
            int existingNumber = supplier.get();
            if (setValue) {
                consumer.accept(number);
                String messageToSend = getSetValueMessage(optionName, number, existingNumber, explanation);
                if (!suppressMessage) MessageHelper.sendMessageToEventChannel(event, messageToSend);
            } else {
                int newNumber = existingNumber + number;
                consumer.accept(newNumber);
                String messageToSend =
                        getChangeValueMessage(optionName, number, existingNumber, newNumber, explanation);
                if (!suppressMessage) MessageHelper.sendMessageToEventChannel(event, messageToSend);
            }
        } catch (Exception e) {
            MessageHelper.sendMessageToEventChannel(event, "Could not parse number for: " + optionName);
        }
    }

    public static String getSetValueMessage(
            String optionName, Integer setToNumber, Integer existingNumber, String explanation) {
        if (explanation == null || "".equalsIgnoreCase(explanation)) {
            return "Set __" + optionName + "__ to " + setToNumber + " (was "
                    + existingNumber + ", a change of " + (setToNumber - existingNumber)
                    + ").";
        } else {
            return "Set __" + optionName + "__ to " + setToNumber + " (was "
                    + existingNumber + ", a change of " + (setToNumber - existingNumber)
                    + ") for the reason of: " + explanation + ".";
        }
    }

    public static String getChangeValueMessage(
            String optionName, Integer changeNumber, Integer existingNumber, Integer newNumber, String explanation) {
        String changeDescription = "Changed";
        if (changeNumber > 0) {
            changeDescription = "Increased";
        } else if (changeNumber < 0) {
            changeDescription = "Decreased";
            changeNumber *= -1;
        }
        if (explanation == null || "".equalsIgnoreCase(explanation)) {
            return changeDescription + " __" + optionName + "__ by " + changeNumber + " (was " + existingNumber
                    + ", now " + newNumber + ").";
        } else {
            return changeDescription + " __" + optionName + "__ by " + changeNumber + " (was " + existingNumber
                    + ", now " + newNumber + ") for the reason of: " + explanation + ".";
        }
    }
}
