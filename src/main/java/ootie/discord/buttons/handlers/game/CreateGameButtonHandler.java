package ootie.discord.buttons.handlers.game;

import java.util.ArrayList;
import java.util.List;
import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.components.label.Label;
import net.dv8tion.jda.api.components.selections.EntitySelectMenu;
import net.dv8tion.jda.api.components.selections.EntitySelectMenu.SelectTarget;
import net.dv8tion.jda.api.components.textinput.TextInput;
import net.dv8tion.jda.api.components.textinput.TextInputStyle;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.modals.Modal;
import ootie.discord.routing.ButtonHandler;
import ootie.discord.routing.ModalHandler;
import ootie.game.Game;
import ootie.game.persistence.GameManager;
import ootie.logging.BotLogger;
import ootie.message.MessageHelper;
import ootie.service.game.CreateGameService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.function.Consumers;

@UtilityClass
public class CreateGameButtonHandler {

    @ButtonHandler("launchGame")
    public static void createGameChannelsButton(ButtonInteractionEvent event) {
        createGameAndChannels(event);
    }

    @ModalHandler("signupModal")
    public static void finishSignup(ModalInteractionEvent event) {
        List<Member> members = event.getValue("players").getAsMentions().getMembers();
        List<Member> membersOG = fetchMembersFromMessage(event);
        boolean addedByPlayerInTheGame = membersOG.contains(event.getMember());
        List<String> blocked = new ArrayList<>();
        List<String> exemptedIds = new ArrayList<>();
        for (Member member : members) {
            if (membersOG.contains(member)) continue;

            membersOG.add(member);
            if (addedByPlayerInTheGame) exemptedIds.add(member.getId());
            MessageHelper.sendMessageToEventChannel(
                    event, event.getUser().getEffectiveName() + " added " + member.getAsMention() + " to the game.");
        }
        event.getMessage()
                .editMessage(generateMemberListMessage(membersOG, fetchSillyNameFromMessage(event)))
                .queue();
        if (!blocked.isEmpty()) {
            event.getHook()
                    .setEphemeral(true)
                    .sendMessage(String.join("\n", blocked))
                    .queue(Consumers.nop(), BotLogger::catchRestError);
        }
    }

    @ButtonHandler(value = "editPlayers~MDL", save = false)
    public static void editPlayers(ButtonInteractionEvent event) {
        String modalID = "signupModal";
        String fieldID = "players";
        EntitySelectMenu menu = EntitySelectMenu.create(fieldID, SelectTarget.USER)
                .setPlaceholder("Choose your players") // shows the placeholder indicating what this menu is for
                .setRequiredRange(1, 8)
                .build();

        Modal modal = Modal.create(modalID, "Players For The Game")
                .addComponents(Label.of("Select Players", menu))
                .build();
        event.replyModal(modal).queue(Consumers.nop(), BotLogger::catchRestError);
    }

    @ModalHandler("removeSignupModal")
    public static void removeSignup(ModalInteractionEvent event) {
        List<Member> members = event.getValue("players").getAsMentions().getMembers();
        List<Member> membersOG = fetchMembersFromMessage(event);
        for (Member member : members) {
            if (!membersOG.contains(member)) continue;
            membersOG.remove(member);
            MessageHelper.sendMessageToEventChannel(
                    event,
                    event.getUser().getEffectiveName() + " removed " + member.getAsMention() + " from the game.");
        }
        event.getMessage()
                .editMessage(generateMemberListMessage(membersOG, fetchSillyNameFromMessage(event)))
                .queue();
    }

    @ModalHandler("addSillyNameModal")
    public static void addSillyNameModal(ModalInteractionEvent event) {
        String sillyName = event.getValue("sillyName").getAsString();
        List<Member> membersOG = fetchMembersFromMessage(event);
        event.getMessage()
                .editMessage(generateMemberListMessage(membersOG, sillyName))
                .queue();
        MessageHelper.sendMessageToEventChannel(
                event,
                event.getUser().getEffectiveName() + " set the game name to **" + sillyName.replace(":", "") + "**.");
    }

    @ButtonHandler(value = "addSillyName~MDL", save = false)
    public static void addSillyName(ButtonInteractionEvent event) {
        String modalID = "addSillyNameModal";
        String fieldID = "sillyName";
        TextInput summary = TextInput.create(fieldID, TextInputStyle.PARAGRAPH)
                .setPlaceholder("Specify a fun game name here")
                .setValue(CreateGameService.autoGenerateGameName())
                .build();
        Modal modal = Modal.create(modalID, "Fun Game Name")
                .addComponents(Label.of("Edit game name", summary))
                .build();
        event.replyModal(modal).queue(Consumers.nop(), BotLogger::catchRestError);
    }

    @ButtonHandler(value = "removePlayers~MDL", save = false)
    public static void removePlayers(ButtonInteractionEvent event) {
        String modalID = "removeSignupModal";
        String fieldID = "players";
        EntitySelectMenu menu = EntitySelectMenu.create(fieldID, SelectTarget.USER)
                .setPlaceholder(
                        "Choose your players to remove") // shows the placeholder indicating what this menu is for
                .setRequiredRange(1, 8)
                .build();

        Modal modal = Modal.create(modalID, "Removing Players In The Game")
                .addComponents(Label.of("Select Players", menu))
                .build();
        event.replyModal(modal).queue(Consumers.nop(), BotLogger::catchRestError);
    }

    private static List<Member> fetchMembersFromMessage(String buttonMsg, Guild guild) {
        List<Member> members = new ArrayList<>();
        for (int i = 0; i < StringUtils.countMatches(buttonMsg, "<@"); i++) {
            String user = buttonMsg.split("@")[i + 1];
            user = StringUtils.substringBefore(user, ">");
            Member member = guild.getMemberById(user);
            if (member != null) {
                members.add(member);
            }
        }
        return members;
    }

    public static List<Member> fetchMembersFromMessage(Message message, Guild guild) {
        return fetchMembersFromMessage(message.getContentRaw(), guild);
    }

    private static List<String> memberIds(List<Member> members) {
        return members.stream().map(Member::getId).toList();
    }

    private static List<Member> fetchMembersFromMessage(ButtonInteractionEvent event) {
        return fetchMembersFromMessage(event.getMessage().getContentRaw(), event.getGuild());
    }

    private static List<Member> fetchMembersFromMessage(ModalInteractionEvent event) {
        return fetchMembersFromMessage(event.getMessage().getContentRaw(), event.getGuild());
    }

    private static String fetchSillyNameFromMessage(String buttonMsg) {
        return StringUtils.substringBetween(buttonMsg, "Game Fun Name: ", "\n");
    }

    private static String fetchSillyNameFromMessage(ModalInteractionEvent event) {
        return fetchSillyNameFromMessage(event.getMessage().getContentRaw());
    }

    private static String fetchSillyNameFromMessage(ButtonInteractionEvent event) {
        return fetchSillyNameFromMessage(event.getMessage().getContentRaw());
    }

    public static String generateMemberListMessage(List<Member> members, String gameFunName) {
        return generateMemberListMessage(members, gameFunName, true);
    }

    public static String generateMemberListMessage(List<Member> members, String gameFunName, boolean ping) {
        StringBuilder memberList = new StringBuilder();

        if (gameFunName == null || gameFunName.isEmpty()) {
            if (ping) {
                memberList.append("## Players Signed Up:\n");
            } else {
                memberList.append("## Players:\n");
            }
        } else {
            if (ping) {
                memberList
                        .append("## Game Fun Name: ")
                        .append(gameFunName.replace(":", ""))
                        .append("\n\nPlayers Signed Up:");
            } else {
                memberList.append(gameFunName.replace(":", "")).append("\n\nPlayers:");
            }
        }

        StringBuilder activityList = new StringBuilder();
        int playerNumber = 1;
        for (Member member : members) {
            String mention = ping ? member.getUser().getAsMention() : member.getEffectiveName();
            memberList.append('\n').append(playerNumber).append(". ").append(mention);
            playerNumber++;
        }
        return memberList.toString() + activityList;
    }

    @ButtonHandler(value = "joinGameList", save = false)
    public static void joinGameList(ButtonInteractionEvent event) {
        List<Member> members = fetchMembersFromMessage(event);
        if (!members.contains(event.getMember())) {
            members.add(event.getMember());
        }
        event.getMessage()
                .editMessage(generateMemberListMessage(members, fetchSillyNameFromMessage(event)))
                .queue(Consumers.nop(), BotLogger::catchRestError);
        MessageHelper.sendMessageToEventChannel(event, event.getUser().getEffectiveName() + " joined the game.");
    }

    @ButtonHandler(value = "leaveGameList", save = false)
    public static void leaveGameList(ButtonInteractionEvent event) {
        List<Member> members = fetchMembersFromMessage(event);
        members.remove(event.getMember());
        event.getMessage()
                .editMessage(generateMemberListMessage(members, fetchSillyNameFromMessage(event)))
                .queue(Consumers.nop(), BotLogger::catchRestError);
        MessageHelper.sendMessageToEventChannel(event, event.getUser().getEffectiveName() + " left the game.");
    }

    private static synchronized void createGameAndChannels(ButtonInteractionEvent event) {
        String userName = event.getUser().getEffectiveName();
        MessageHelper.sendMessageToEventChannel(event, userName + " pressed the [Create Game] button");

        if (!CreateGameService.isGameCreationAllowed()) {
            MessageHelper.sendMessageToChannel(
                    event.getMessageChannel(),
                    "Admins have temporarily turned off game creation, most likely to contain a bug. Please be patient.");
            return;
        }

        String buttonMessage = event.getMessage().getContentRaw();

        List<Member> members = resolveMembers(event, buttonMessage);
        if (members.isEmpty()) {
            MessageHelper.sendMessageToChannel(event.getChannel(), "No valid members found.");
            return;
        }

        Member gameOwner = members.isEmpty() ? null : members.getFirst();

        String gameName = CreateGameService.getNextPbdGameName();
        Category categoryChannel = resolveOrCreateCategory(gameName, event);
        if (categoryChannel == null) {
            resetPbdNumber(gameName);
            return;
        }

        event.getMessage().delete().queue(Consumers.nop(), BotLogger::catchRestError);

        String gameSillyName = parseOrGenerateSillyName(buttonMessage);

        Game game = CreateGameService.createGameChannels(
                members, event, gameSillyName, gameName, gameOwner, categoryChannel);

        if (game == null) {
            resetPbdNumber(gameName);
            MessageHelper.sendMessageToEventChannel(event, "Something went wrong...");
            return;
        }

        MessageHelper.sendMessageToEventChannel(event, "Message for posterity:\n\n" + buttonMessage);
        GameManager.save(game, "Created game channels");
    }

    private static void resetPbdNumber(String gameName) {
        int pbdNumber = Integer.parseInt(gameName.replace("pbd", ""));
        GameManager.resetLatestPbdNumberFrom(pbdNumber);
    }

    private static String parseOrGenerateSillyName(String buttonMessage) {
        String gameSillyName = StringUtils.substringBetween(buttonMessage, "Game Fun Name: ", "\n");
        if (gameSillyName == null || gameSillyName.isEmpty()) {
            gameSillyName = CreateGameService.autoGenerateGameName();
        }
        return gameSillyName;
    }

    private static Category resolveOrCreateCategory(String gameName, ButtonInteractionEvent event) {
        String categoryChannelName = CreateGameService.getCategoryNameForGame(gameName);
        Category categoryChannel = null;
        List<Category> categories = CreateGameService.getAllAvailablePBDCategories();
        for (Category category : categories) {
            if (category.getName().toUpperCase().startsWith(categoryChannelName)) {
                categoryChannel = category;
                break;
            }
        }
        if (categoryChannel == null) categoryChannel = CreateGameService.createNewGameCategory(categoryChannelName);
        if (categoryChannel == null) {
            MessageHelper.sendMessageToEventChannel(
                    event,
                    "Could not automatically find a category that begins with **" + categoryChannelName
                            + "** - Please create this category.\n# Warning, this may mean all servers are at capacity.");
        }
        return categoryChannel;
    }

    private static List<Member> resolveMembers(ButtonInteractionEvent event, String buttonMessage) {
        List<Member> members = fetchMembersFromMessage(event);
        if (!members.isEmpty()) {
            return members;
        }
        return parseMembersFromButtonMessage(event, buttonMessage);
    }

    private static List<Member> parseMembersFromButtonMessage(ButtonInteractionEvent event, String buttonMsg) {
        List<Member> members = new ArrayList<>();
        String[] parts = buttonMsg.split(":");

        for (int i = 3; i < parts.length; i++) {
            String userId = StringUtils.substringBefore(parts[i], ".");
            Member member = event.getGuild().getMemberById(userId);
            if (member != null) {
                members.add(member);
            }
        }
        return members;
    }
}
