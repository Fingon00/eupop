package ootie.helpers;

import java.awt.AlphaComposite;
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
        color = "Purple";
        BufferedImage mapImage = ImageHelper.read(ResourceHelper.getInstance().getPlayerFile(color, "PlayerArea"));

        BufferedImage finalImage =
                new BufferedImage(mapImage.getWidth(), mapImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = finalImage.createGraphics();
        g.setComposite(AlphaComposite.SrcOver);
        g.drawImage(mapImage, 0, 0, null);

        g.setFont(g.getFont().deriveFont(80f));
        DrawingUtil.superDrawStringCentered(
                g, "" + player.getDucats(), 215, 638, Color.BLACK, DrawingUtil.stroke(7), Color.WHITE);
        
        int powerStringy = 720;
        
        DrawingUtil.superDrawStringCentered(
                g, "" + player.getAdminPower(), 740, powerStringy, Color.BLACK, DrawingUtil.stroke(7), Color.WHITE);
        DrawingUtil.superDrawStringCentered(
                g, "" + player.getDiploPower(), 1125, powerStringy, Color.BLACK, DrawingUtil.stroke(7), Color.WHITE);
        DrawingUtil.superDrawStringCentered(
                g, "" + player.getMilitaryPower(), 1515, powerStringy, Color.BLACK, DrawingUtil.stroke(7), Color.WHITE);

        BufferedImage townImage =
                ImageHelper.readScaled(ResourceHelper.getInstance().getPlayerFile(color, "Town"), 0.46f);
        float alpha = 0.55f;
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));

        for (int x = 0; x < 20; x++) {
            if (x < player.getSmallProvinces().size()) continue;
            int xMod = x % 10;
            int yMod = 0;
            if (x >= 10) {
                yMod = townImage.getHeight() + 15;
            }
            g.drawImage(townImage, 580 + xMod * (townImage.getWidth() + 15), 72 + yMod, null);
        }
        BufferedImage largeTownImage =
                ImageHelper.readScaled(ResourceHelper.getInstance().getPlayerFile(color, "Town"), 0.595f);
        for (int x = 0; x < 8; x++) {
            if (x < player.getLargeProvinces().size()) continue;
            g.drawImage(largeTownImage, 580 + x * (largeTownImage.getWidth() + 13), 295, null);
        }

        BufferedImage vassalImage =
                ImageHelper.readScaled(ResourceHelper.getInstance().getPlayerFile(color, "Vassal"), 0.51f);
        for (int x = 0; x < 10; x++) {
            if (x < player.getLargeProvinces().size()) continue;
            g.drawImage(vassalImage, 580 + x * (vassalImage.getWidth() + 15), 480, null);
        }
        g.setComposite(AlphaComposite.SrcOver);


        String stateReligion = player.getStateReligion();
        if (stateReligion != null) {
            BufferedImage religionImage =
                    ImageHelper.readScaled(ResourceHelper.getInstance().getMiscFile(stateReligion), 0.60f);
            g.drawImage(religionImage, 429, 728, null);
        }







        FileUpload fileUpload = FileUploadService.createFileUpload(finalImage, "playerArea");
        MessageHelper.sendFileUploadToChannel(event.getChannel(), fileUpload);
    }
}
