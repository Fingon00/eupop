package ootie.discord.commands;

import java.lang.reflect.Member;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nullable;
import javax.management.relation.Role;
import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command.Choice;
import net.dv8tion.jda.api.interactions.commands.CommandInteractionPayload;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonInteraction;
import ootie.game.Game;
import ootie.game.Player;
import ootie.game.persistence.GameManager;
import ootie.helpers.Constants;
import ootie.logging.BotLogger;
import ootie.service.GameNameService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.function.Consumers;

@UtilityClass
public class CommandHelper {

    public static List<Choice> toChoices(String... values) {
        return toChoices(Arrays.asList(values));
    }

    public static List<Choice> toChoices(Collection<String> values) {
        return values.stream().map(v -> new Choice(v, v)).toList();
    }

    static boolean acceptIfValidGame(SlashCommandInteractionEvent event, boolean checkChannel, boolean checkPlayer) {

        var gameName = GameNameService.getGameName(event);
        var managedGame = GameManager.getManagedGame(gameName);
        if (managedGame == null) {
            event.getHook()
                    .editOriginal(
                            "'" + event.getFullCommandName() + "' command canceled. Game name '" + gameName
                                    + "' is not valid. "
                                    + "Execute command in correctly named channel that starts with the game name. For example, for game `pbd123`, the channel name should start with `pbd123-`")
                    .queue(Consumers.nop(), BotLogger::catchRestError);
            return false;
        }
        if (checkChannel && !event.getChannel().getName().startsWith(managedGame.getName() + "-")) {
            event.getHook()
                    .editOriginal("'" + event.getFullCommandName() + "' can only be executed in a game channel.")
                    .queue(Consumers.nop(), BotLogger::catchRestError);
            return false;
        }
        return true;
    }

    /**
     * Supported inputs include:
     * <ul>
     * <li>id: 228999251328368640
     * <li>username: Jazzxhands
     * <li>autocomplete: No Color / No Faction / Jazzxhands
     * </ul>
     */
    @Nullable
    public static Player getPlayerFromReplaceEvent(Game game, GenericCommandInteractionEvent event) {
        String nameOrID = event.getOption(Constants.PLAYER_FACTION).getAsString();
        int replaceIndex = nameOrID.lastIndexOf(" / ");
        String name = nameOrID.substring(replaceIndex < 0 ? 0 : replaceIndex + 3);

        for (Player player : game.getPlayers().values()) {
            if (player.getUserID().equals(nameOrID) || player.getUserName().equals(name)) {
                return player;
            }
        }
        return null;
    }

    @Nullable
    public static Player getPlayerFromGame(Game game, Member member, String userId) {

        return null;
    }

    @Nullable
    private static Player getPlayerByFactionColor(String factionColor, Game game) {
        factionColor = StringUtils.substringBefore(factionColor, " "); // TO HANDLE UNRESOLVED AUTOCOMPLETE
        for (Player player_ : game.getPlayers().values()) {
            if (Objects.equals(factionColor, player_.getFaction())
                    || Objects.equals(factionColor, player_.getColor())) {
                return player_;
            }
        }
        return null;
    }

    @Nullable
    private static Player getPlayerFromChannel(Game game, GenericCommandInteractionEvent event) {
        String channelId = event.getChannel().getId();
        for (Player player : game.getPlayers().values()) {
            if (channelId.equals(player.getPrivateChannelID()) || channelId.equals(player.getCardsInfoThreadID())) {
                return player;
            }
        }
        return null;
    }

    @Nullable
    public static Player getOtherPlayerFromEvent(Game game, SlashCommandInteractionEvent event) {
        OptionMapping playerOption = event.getOption(Constants.TARGET_PLAYER);

        return null;
    }

    // Return game.getRealPlayers() if target is ALL, otherwise supports comma
    // separated list
    public static List<Player> getTargetPlayersFromOption(Game game, SlashCommandInteractionEvent event) {
        List<Player> targetPlayers = new ArrayList<>();
        String targetOption = event.getOption(Constants.TARGET_FACTION_OR_COLOR, null, OptionMapping::getAsString);
        if (targetOption == null) {
            return targetPlayers;
        }

        return targetPlayers;
    }

    public static boolean acceptIfHasRoles(SlashCommandInteractionEvent event, Collection<Role> acceptedRoles) {

        return false;
    }

    public static boolean hasRole(Interaction event, Collection<Role> acceptedRoles) {
        Member member = event.getMember();
        if (member == null) {
            return false;
        }
        List<Role> roles = member.getRoles();
        for (Role role : acceptedRoles) {
            if (roles.contains(role)) {
                return true;
            }
        }
        return false;
    }

    public static String getHeaderText(GenericInteractionCreateEvent event) {
        if (event instanceof SlashCommandInteractionEvent) {
            return " used `" + ((CommandInteractionPayload) event).getCommandString() + "`";
        }
        if (event instanceof ButtonInteractionEvent) {
            return " pressed `" + ((ButtonInteraction) event).getButton().getCustomId() + "`";
        }
        return " used the force";
    }
}
