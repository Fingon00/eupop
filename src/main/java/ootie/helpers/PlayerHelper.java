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
        if (player.getColor() == null || player.getColor().isEmpty()) {
            color = "Purple";
        }
        BufferedImage mapImage = ImageHelper.read(ResourceHelper.getInstance().getPlayerFile(color, "PlayerArea"));

        BufferedImage finalImage =
                new BufferedImage(mapImage.getWidth(), mapImage.getHeight()+91, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = finalImage.createGraphics();
        g.setComposite(AlphaComposite.SrcOver);
        g.drawImage(mapImage, 0, 0, null);
        g.setFont(g.getFont().deriveFont(80f));


        DrawingUtil.superDrawStringCentered(
                g, "" + player.getDucats(), 215, 638, Color.BLACK, DrawingUtil.stroke(7), Color.WHITE);

        int powerY = 720;
        DrawingUtil.superDrawStringCentered(
                g, "" + player.getAdminPower(), 740, powerY, Color.BLACK, DrawingUtil.stroke(7), Color.WHITE);
        DrawingUtil.superDrawStringCentered(
                g, "" + player.getDiploPower(), 1125, powerY, Color.BLACK, DrawingUtil.stroke(7), Color.WHITE);
        DrawingUtil.superDrawStringCentered(
                g, "" + player.getMilitaryPower(), 1515, powerY, Color.BLACK, DrawingUtil.stroke(7), Color.WHITE);

        
        drawTownsandVassals(player, g, color);
        drawStability(player, g, color);
        drawStateReligion(player, g);
        drawCharacters(player, g, color);

        FileUpload fileUpload = FileUploadService.createFileUpload(finalImage, "playerArea");
        MessageHelper.sendFileUploadToChannel(event.getChannel(), fileUpload);
    }

    public static void drawCharacters(Player player, Graphics2D g, String color) {
        int cropX = 0;
        int cropY = 464;
        int cropWidth = 376;
        int cropHeight = 91;
        if(player.getRuler() != null){
                BufferedImage eventImage = ImageHelper.read(ResourceHelper.getInstance().getCharacterFile(player.getRuler()));
                BufferedImage croppedImage = new BufferedImage(cropWidth, cropHeight, eventImage.getType());
                Graphics2D g2d = croppedImage.createGraphics();
                g2d.drawImage(
                eventImage, 
                0, 0, cropWidth, cropHeight,              
                cropX, cropY, cropX + cropWidth, cropY + cropHeight, 
                null);
                g2d.dispose();
                g.drawImage(croppedImage,200, 789, null);
        }
        if(player.getAdminAdvisor() != null){
                BufferedImage eventImage = ImageHelper.read(ResourceHelper.getInstance().getCharacterFile(player.getAdminAdvisor()));
                BufferedImage croppedImage = new BufferedImage(cropWidth, cropHeight, eventImage.getType());
                Graphics2D g2d = croppedImage.createGraphics();
                g2d.drawImage(
                eventImage, 
                0, 0, cropWidth, cropHeight,              
                cropX, cropY, cropX + cropWidth, cropY + cropHeight, 
                null);
                g2d.dispose();
                g.drawImage(croppedImage,550, 789, null);
        }

    }

    public static void drawTownsandVassals(Player player, Graphics2D g, String color) {
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
    }



    public static void drawStability(Player player, Graphics2D g, String color) {
        BufferedImage stabilityImage =
                ImageHelper.readScaled(ResourceHelper.getInstance().getPlayerFile(color, "Town"), 0.31f);
        g.drawImage(stabilityImage, 107 + (player.getStability() + 2) * (stabilityImage.getWidth() + 8), 70, null);
    }

    public static void drawStateReligion(Player player, Graphics2D g) {
        String stateReligion = player.getStateReligion();
        if (stateReligion != null) {
            BufferedImage religionImage =
                    ImageHelper.readScaled(ResourceHelper.getInstance().getMiscFile(stateReligion), 0.60f);
            g.drawImage(religionImage, 429, 728, null);
        }
    }
}
