package ootie.discord.commands.character;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import ootie.discord.commands.GameStateSubcommand;
import ootie.game.Game;
import ootie.game.Player;
import ootie.helpers.Constants;
import ootie.helpers.PlayerHelper;
import ootie.image.Mapper;
import ootie.message.MessageHelper;
import ootie.model.CharacterModel;

class CharacterAdd extends GameStateSubcommand {

    CharacterAdd() {
        super(Constants.ADD, "Add a character", true, true);
        addOptions(new OptionData(OptionType.STRING, Constants.CHARACTER, "Character Name")
                .setAutoComplete(true)
                .setRequired(true));
        addOptions(new OptionData(OptionType.USER, Constants.PLAYER, "Player for which you are editing info")
                .setRequired(false));
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        Game game = getGame();
        String character = event.getOption(Constants.CHARACTER, null, OptionMapping::getAsString);
        Player player = getPlayer();

        CharacterModel charM = Mapper.getCharacters().get(character);
        if (charM != null) {
            if (charM.isAdvisor()) {
                if (charM.getDiploPower() > 0) {
                    player.setDiploAdvisor(character);
                }
                if (charM.getAdminPower() > 0) {
                    player.setAdminAdvisor(character);
                }
                if (charM.getMilitaryPower() > 0) {
                    player.setMilitaryAdvisor(character);
                }
            } else {
                player.setRuler(character);
            }
            PlayerHelper.drawPlayerArea(player, event);
        } else {
            MessageHelper.sendMessageToChannel(event.getChannel(), "Could not locate the character: " + character);
        }
    }
}
