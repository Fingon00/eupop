package ootie.discord.commands.search;

import java.util.List;
import java.util.concurrent.TimeUnit;
import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel.AutoArchiveDuration;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import ootie.image.Mapper;
import ootie.logging.BotLogger;
import ootie.message.MessageHelper;
import org.apache.commons.lang3.function.Consumers;

@UtilityClass
class SearchHelper {

    public static void sendSearchMessageToEventChannel(SlashCommandInteractionEvent event, String message) {
        event.getChannel().sendMessage(message).queue(Consumers.nop(), BotLogger::catchRestError);
    }

    public static void sendSearchEmbedsToEventChannel(
            SlashCommandInteractionEvent event, List<MessageEmbed> messageEmbeds) {
        if (messageEmbeds.size() > 3) {
            String threadName = event.getCommandString();
            MessageHelper.sendMessageEmbedsToThread(event.getChannel(), threadName, messageEmbeds);
        } else if (!messageEmbeds.isEmpty()) {
            event.getChannel().sendMessageEmbeds(messageEmbeds).queue(Consumers.nop(), BotLogger::catchRestError);
        } else {
            event.getChannel().sendMessage("> No results found").queue(Consumers.nop(), BotLogger::catchRestError);
        }
    }

    public static void sendSearchEventsToEventChannel(SlashCommandInteractionEvent event, List<String> eventIDs) {
        if (eventIDs.size() > 5) {
            MessageChannelUnion channel = event.getChannel();
            String threadName = event.getCommandString();
            if (channel == null || threadName == null) {
                return;
            }
            if (channel instanceof TextChannel) {
                channel.asTextChannel()
                        .createThreadChannel(threadName)
                        .setAutoArchiveDuration(AutoArchiveDuration.TIME_1_HOUR)
                        .queueAfter(
                                500,
                                TimeUnit.MILLISECONDS,
                                t -> {
                                    for (String id : eventIDs) {
                                        Mapper.getEvent(id).drawEventImage(t);
                                    }
                                },
                                error -> BotLogger.error(
                                        "Error creating thread channel: " + threadName + " in channel: "
                                                + channel.getAsMention(),
                                        error));
            } else if (channel instanceof ThreadChannel thread) {
                if (thread.getParentChannel() instanceof TextChannel chan) {
                    chan.createThreadChannel(threadName)
                            .setAutoArchiveDuration(AutoArchiveDuration.TIME_1_HOUR)
                            .queueAfter(
                                    500,
                                    TimeUnit.MILLISECONDS,
                                    t -> {
                                        for (String id : eventIDs) {
                                            Mapper.getEvent(id).drawEventImage(t);
                                        }
                                        MessageHelper.sendMessageToChannel(
                                                channel,
                                                "Redirected your results to the following thread: " + t.getJumpUrl());
                                    },
                                    error -> BotLogger.error(
                                            "Error creating thread channel: " + threadName + " in channel: "
                                                    + chan.getAsMention(),
                                            error));

                } else {
                    for (String id : eventIDs) {
                        Mapper.getEvent(id).drawEventImage(event.getMessageChannel());
                    }
                }
            }
        } else if (!eventIDs.isEmpty()) {
            for (String id : eventIDs) {
                Mapper.getEvent(id).drawEventImage(event.getMessageChannel());
            }
        } else {
            event.getChannel().sendMessage("> No results found").queue(Consumers.nop(), BotLogger::catchRestError);
        }
    }
}
