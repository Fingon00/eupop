package ootie.discord.listeners;

import java.util.List;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.events.channel.ChannelCreateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import ootie.discord.JdaService;
import ootie.executors.ExecutorServiceManager;
import ootie.service.game.CreateGameLaunchPostService;
import ootie.spring.service.deploy.ActiveLeaseService;
import org.jetbrains.annotations.NotNull;

class ChannelCreationListener extends ListenerAdapter {

    @Override
    public void onChannelCreate(@NotNull ChannelCreateEvent event) {
        if (!ActiveLeaseService.shouldHandleCurrentProcessInteraction()) {
            return;
        }
        if (!JdaService.isReadyToReceiveCommands()) {
            return;
        }
        ExecutorServiceManager.runAsync(
                "ChannelCreationListener task", () -> handleMakingNewGamesThreadCreation(event));
    }

    private void handleMakingNewGamesThreadCreation(ChannelCreateEvent event) {
        if (!JdaService.isValidGuild(event.getGuild().getId())
                || !(event.getChannel() instanceof ThreadChannel channel)) {
            return;
        }

        String parentName = channel.getParentChannel().getName();
        if (CreateGameLaunchPostService.isCreateGameLaunchParentName(parentName)) {
            Member owner = channel.getOwner();
            if (owner == null || owner.getUser().isBot()) return;

            CreateGameLaunchPostService.postLaunchButtons(channel, List.of(owner), "");
        }
    }
}
