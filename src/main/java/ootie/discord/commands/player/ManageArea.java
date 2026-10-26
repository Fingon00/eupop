package ootie.discord.commands.player;

import java.util.Collections;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import ootie.discord.commands.GameStateSubcommand;
import ootie.game.Game;
import ootie.game.Player;
import ootie.helpers.Constants;
import ootie.helpers.GameImageHelper;
import ootie.message.MessageHelper;

class ManageArea extends GameStateSubcommand {

    ManageArea() {
        super(Constants.MANAGE_AREA, "Manage Area", true, true);
        addOptions(new OptionData(OptionType.STRING, Constants.AREA, "Area name")
                .setAutoComplete(true)
                .setRequired(true));
        addOptions(new OptionData(OptionType.STRING, Constants.INFLUENCE, "Add or subtract influence")
                .setAutoComplete(true)
                .setRequired(false));
        addOptions(new OptionData(OptionType.STRING, Constants.INFANTRY, "Add or subtract infantry")
                .setAutoComplete(true)
                .setRequired(false));
        addOptions(new OptionData(OptionType.STRING, Constants.REBELS, "Add or subtract rebels")
                .setAutoComplete(true)
                .setRequired(false));
        addOptions(new OptionData(OptionType.STRING, Constants.RELIGION, "Religion").setAutoComplete(true));
        addOptions(new OptionData(OptionType.STRING, Constants.FORCE, "Force").setAutoComplete(true));
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        Game game = getGame();
        Player player = getPlayer();
        String area = event.getOption(Constants.AREA).getAsString();
        OptionMapping influence = event.getOption(Constants.INFLUENCE);
        if (influence != null) {
            String inf = event.getOption(Constants.INFLUENCE).getAsString();
            int num = Integer.parseInt(inf);
            if (inf.contains("-")) {
                for (int x = 0; x < num; x++) {
                    player.getInfluence().remove(area);
                }
            } else if (inf.contains("+")) {
                for (int x = 0; x < num; x++) {
                    player.getInfluence().add(area);
                }
            } else {
                int dif = Collections.frequency(player.getInfluence(), area) - num;
                int absDif = Math.abs(dif);
                for (int x = 0; x < absDif; x++) {
                    if (dif > 0) {
                        player.getInfluence().remove(area);
                    } else {
                        player.getInfluence().add(area);
                    }
                }
            }
        }

        OptionMapping rebels = event.getOption(Constants.REBELS);
        if (rebels != null) {
            String reb = event.getOption(Constants.REBELS).getAsString();
            int num = Integer.parseInt(reb);
            if (reb.contains("-")) {
                for (int x = 0; x < num; x++) {
                    game.getRebels().remove(area);
                }
            } else if (reb.contains("+")) {
                for (int x = 0; x < num; x++) {
                    game.getRebels().add(area);
                }
            } else {
                int dif = Collections.frequency(game.getRebels(), area) - num;
                int absDif = Math.abs(dif);
                for (int x = 0; x < absDif; x++) {
                    if (dif > 0) {
                        game.getRebels().remove(area);
                    } else {
                        game.getRebels().add(area);
                    }
                }
            }
        }

        OptionMapping singInf = event.getOption(Constants.INFANTRY);
        if (singInf != null) {
            String inf = event.getOption(Constants.INFANTRY).getAsString();
            int num = Integer.parseInt(inf);
            if (inf.contains("-")) {
                for (int x = 0; x < num; x++) {
                    player.getSingularInfantry().remove(area);
                }
            } else if (inf.contains("+")) {
                for (int x = 0; x < num; x++) {
                    player.getSingularInfantry().add(area);
                }
            } else {
                int dif = Collections.frequency(player.getSingularInfantry(), area) - num;
                int absDif = Math.abs(dif);
                for (int x = 0; x < absDif; x++) {
                    if (dif > 0) {
                        player.getSingularInfantry().remove(area);
                    } else {
                        player.getSingularInfantry().add(area);
                    }
                }
            }
        }

        OptionMapping religion = event.getOption(Constants.RELIGION);
        if (religion != null) {
            String rel = religion.getAsString();
            if ("none".equalsIgnoreCase(area)) {
                game.getStoredAreaReligions().remove(rel);
            } else {
                game.getStoredAreaReligions().put(area, rel);
            }
        }

        OptionMapping force = event.getOption(Constants.FORCE);
        if (force != null) {
            String army = force.getAsString();
            if (army.contains("1")) {
                player.getArmy1().setLocation(area);
            }
            if (army.contains("2")) {
                player.getArmy2().setLocation(area);
            }
            if (army.contains("3")) {
                player.getArmy3().setLocation(area);
            }
            if (army.contains("fleet")) {
                player.getFleet().setLocation(area);
            }
        }

        // PlayerHelper.drawPlayerArea(player, event);
        MessageHelper.sendFileUploadToChannel(event.getChannel(), GameImageHelper.drawMap(game));
    }
}
