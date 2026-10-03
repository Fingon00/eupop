package ootie.helpers;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.utils.FileUpload;
import ootie.ResourceHelper;
import ootie.game.Player;
import ootie.image.DrawingUtil;
import ootie.image.ImageHelper;
import ootie.message.MessageHelper;
import ootie.service.image.FileUploadService;

public class PlayerHelper {

    public static void drawPlayerArea(Player player, SlashCommandInteractionEvent event) {
        String color = player.getColor();

        BufferedImage mapImage = ImageHelper.read(ResourceHelper.getInstance().getPlayerFile("Purple", "PlayerArea"));
        Graphics2D g = mapImage.createGraphics();
        g.setStroke(DrawingUtil.stroke(20));
        DrawingUtil.superDrawStringCentered(
                g, "" + player.getDucats(), 300, 600, Color.BLACK, DrawingUtil.stroke(10), Color.WHITE);

        FileUpload fileUpload = FileUploadService.createFileUpload(mapImage, "playerArea");
        MessageHelper.sendFileUploadToChannel(event.getChannel(), fileUpload);
    }
}
