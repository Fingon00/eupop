package ootie.helpers;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.utils.FileUpload;
import ootie.ResourceHelper;
import ootie.game.Army;
import ootie.game.Fleet;
import ootie.game.Game;
import ootie.game.Player;
import ootie.image.ImageHelper;
import ootie.message.MessageHelper;
import ootie.service.image.FileUploadService;

public class PlayerHelper {

    public static void drawPlayerArea(Player player, SlashCommandInteractionEvent event) {
        FileUpload fileUpload = FileUploadService.createFileUpload(getPlayerAreaImage(player), "playerArea");
        MessageHelper.sendFileUploadToChannel(event.getChannel(), fileUpload);
    }

    public static BufferedImage getBotPlayerAreaImage(Player player) {
        String color = player.getColor();
        if (player.getColor() == null || player.getColor().isEmpty()) {
            color = "Purple";
        }
        BufferedImage mapImage = ImageHelper.read(ResourceHelper.getInstance().getPlayerFile(color, "BotPlayerArea"));

        BufferedImage finalImage =
                new BufferedImage(mapImage.getWidth(), mapImage.getHeight() + 91, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = finalImage.createGraphics();
        g.drawImage(mapImage, 0, 0, null);
        drawTownsandVassals(player, g, color);
        drawStateReligion(player, g);

        int powerY = 140;

        BufferedImage cubeImage =
                ImageHelper.readScaled(ResourceHelper.getInstance().getPlayerFile(color, "Cube"), 50, 50);
        for (int x = 0; x < player.getBotPower(); x++) {
            g.drawImage(
                    cubeImage,
                    70 + x % 5 * (cubeImage.getWidth() + 10),
                    powerY + (x / 5) * (cubeImage.getHeight() + 10),
                    null);
        }
        for (int x = 0; x < player.getSpentBotPower(); x++) {
            g.drawImage(
                    cubeImage,
                    335 + x % 5 * (cubeImage.getWidth() + 10),
                    powerY + (x / 5) * (cubeImage.getHeight() + 10),
                    null);
        }

        for (int x = 0; x < player.getBotFocusCubes().size(); x++) {
            int startingPos = 645;
            switch (player.getBotFocusCubes().get(x)) {
                case "expansion" -> {
                    startingPos = 932;
                }
                case "politics" -> {
                    startingPos = 1213;
                }
                case "warfare" -> {
                    startingPos = 1460;
                }
            }
            g.drawImage(
                    cubeImage,
                    startingPos + x % 5 * (cubeImage.getWidth() + 10),
                    565 + (x / 5) * (cubeImage.getHeight() + 10),
                    null);
        }
        drawArmy(player, player.getArmy1(), g, 80);

        return finalImage;
    }

    public static BufferedImage getPlayerAreaImage(Player player) {
        if (player.isBot()) {
            return getBotPlayerAreaImage(player);
        }
        String color = player.getColor();
        if (player.getColor() == null || player.getColor().isEmpty()) {
            color = "Purple";
        }
        BufferedImage mapImage = ImageHelper.read(ResourceHelper.getInstance().getPlayerFile(color, "PlayerArea"));

        BufferedImage finalImage =
                new BufferedImage(mapImage.getWidth(), mapImage.getHeight() + 91, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = finalImage.createGraphics();
        g.setComposite(AlphaComposite.SrcOver);
        g.drawImage(mapImage, 0, 0, null);
        // g.setFont(g.getFont().deriveFont(80f));
        // DrawingUtil.superDrawStringCentered(
        //         g, "" + player.getDucats(), 215, 638, Color.BLACK, DrawingUtil.stroke(7), Color.WHITE);
        BufferedImage tenDucat =
                ImageHelper.readScaled(ResourceHelper.getInstance().getMiscFile("10Ducats"), 0.3f);
        BufferedImage oneDucat =
                ImageHelper.readScaled(ResourceHelper.getInstance().getMiscFile("1Ducat"), 0.3f);
        BufferedImage fiveDucat =
                ImageHelper.readScaled(ResourceHelper.getInstance().getMiscFile("5Ducats"), 0.3f);
        for (int x = 0; x < player.getDucats() / 10; x++) {
            g.drawImage(tenDucat, 50 + x * (tenDucat.getWidth() + 10), 540, null);
        }
        if (player.getDucats() % 10 > 4) {
            g.drawImage(fiveDucat, 50, 650, null);
        }
        for (int x = 0; x < player.getDucats() % 5; x++) {
            g.drawImage(oneDucat, 150 + x * (oneDucat.getWidth() + 10), 650, null);
        }

        int powerY = 680;

        BufferedImage cubeImage =
                ImageHelper.readScaled(ResourceHelper.getInstance().getPlayerFile(color, "Cube"), 50, 50);
        for (int x = 0; x < player.getAdminPower(); x++) {
            g.drawImage(
                    cubeImage,
                    620 + x % 5 * (cubeImage.getWidth() + 10),
                    powerY + (x / 5) * (cubeImage.getHeight() + 10),
                    null);
        }
        for (int x = 0; x < player.getDiploPower(); x++) {
            g.drawImage(
                    cubeImage,
                    1000 + x % 5 * (cubeImage.getWidth() + 10),
                    powerY + (x / 5) * (cubeImage.getHeight() + 10),
                    null);
        }
        for (int x = 0; x < player.getMilitaryPower(); x++) {
            g.drawImage(
                    cubeImage,
                    1380 + x % 5 * (cubeImage.getWidth() + 10),
                    powerY + (x / 5) * (cubeImage.getHeight() + 10),
                    null);
        }
        // DrawingUtil.superDrawStringCentered(
        //         g, "" + player.getAdminPower(), 740, powerY, Color.BLACK, DrawingUtil.stroke(7), Color.WHITE);
        // DrawingUtil.superDrawStringCentered(
        //         g, "" + player.getDiploPower(), 1125, powerY, Color.BLACK, DrawingUtil.stroke(7), Color.WHITE);
        // DrawingUtil.superDrawStringCentered(
        //         g, "" + player.getMilitaryPower(), 1515, powerY, Color.BLACK, DrawingUtil.stroke(7), Color.WHITE);

        drawTownsandVassals(player, g, color);
        drawStability(player, g, color);
        drawStateReligion(player, g);
        drawCharacters(player, g, color);

        return finalImage;
    }

    public static void drawArmysNFleets(Player player, SlashCommandInteractionEvent event) {
        MessageHelper.sendFileUploadToChannel(
                event.getChannel(), FileUploadService.createFileUpload(getArmysNFleetsImage(player), "forces"));
    }

    public static BufferedImage getArmysNFleetsImage(Player player) {
        String color = player.getColor();
        if (player.getColor() == null || player.getColor().isEmpty()) {
            color = "Purple";
        }
        BufferedImage armyImage = ImageHelper.read(ResourceHelper.getInstance().getPlayerFile(color, "Army"));
        BufferedImage fleetImage = ImageHelper.read(ResourceHelper.getInstance().getPlayerFile(color, "Fleet"));

        BufferedImage finalImage = new BufferedImage(
                armyImage.getWidth() + fleetImage.getWidth(), armyImage.getHeight() + 91, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = finalImage.createGraphics();
        g.drawImage(armyImage, 0, 0, null);
        g.drawImage(fleetImage, armyImage.getWidth(), 0, null);
        drawArmy(player, player.getArmy1(), g, 60);
        drawArmy(player, player.getArmy2(), g, 440);
        drawArmy(player, player.getArmy3(), g, 860);
        drawFleet(player, player.getFleet(), g, 1240);
        drawForceCharacters(player, g, color);
        return finalImage;
    }

    public static void drawActionCard(Player player, String type, Game game) {
        if ("administrative".equalsIgnoreCase(type)) {
            player.getActionCards().add(game.getAdministrativeActionCardDeck().removeFirst());
        }
        if ("military".equalsIgnoreCase(type)) {
            player.getActionCards().add(game.getMilitaryActionCardDeck().removeFirst());
        }
        if ("diplomatic".equalsIgnoreCase(type)) {
            player.getActionCards().add(game.getDiplomaticActionCardDeck().removeFirst());
        }
    }

    public static void showActionCards(Player player) {
        if (player.getActionCards().isEmpty()) {
            return;
        }
        BufferedImage cardImage = ImageHelper.read(ResourceHelper.getInstance()
                .getCharacterFile(player.getActionCards().getFirst()));

        BufferedImage finalImage = new BufferedImage(
                cardImage.getWidth() * player.getActionCards().size()
                        + 20 * (player.getActionCards().size() - 1),
                cardImage.getHeight(),
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = finalImage.createGraphics();
        int x = 0;
        for (String ac : player.getActionCards()) {
            BufferedImage cardImage2 =
                    ImageHelper.read(ResourceHelper.getInstance().getCharacterFile(ac));
            g.drawImage(cardImage2, x, 0, null);
            x += cardImage2.getWidth() + 20;
        }

        FileUpload fileUpload = FileUploadService.createFileUpload(finalImage, "actioncards");
        MessageHelper.sendFileUploadToChannel(player.getCardsInfoThread(), fileUpload);
    }

    public static void showMissions(Player player) {
        if (player.getMissions().isEmpty()) {
            return;
        }
        BufferedImage cardImage = ImageHelper.read(
                ResourceHelper.getInstance().getMissionFile(player.getMissions().getFirst()));

        BufferedImage finalImage = new BufferedImage(
                cardImage.getWidth() * player.getMissions().size()
                        + 20 * (player.getMissions().size() - 1),
                cardImage.getHeight(),
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = finalImage.createGraphics();
        int x = 0;
        for (String ac : player.getMissions()) {
            BufferedImage cardImage2 =
                    ImageHelper.read(ResourceHelper.getInstance().getMissionFile(ac));
            g.drawImage(cardImage2, x, 0, null);
            x += cardImage2.getWidth() + 20;
        }

        FileUpload fileUpload = FileUploadService.createFileUpload(finalImage, "missions");
        MessageHelper.sendFileUploadToChannel(player.getCardsInfoThread(), fileUpload);
    }

    public static void drawCharacters(Player player, Graphics2D g, String color) {
        int cropX = 10;
        int cropY = 464;
        int cropWidth = 356;
        int cropHeight = 91;
        int leaderheightLoc = 789 + 97;
        if (player.getRuler() != null) {
            BufferedImage eventImage =
                    ImageHelper.read(ResourceHelper.getInstance().getCharacterFile(player.getRuler()));
            BufferedImage croppedImage = new BufferedImage(cropWidth, cropHeight, eventImage.getType());
            Graphics2D g2d = croppedImage.createGraphics();
            g2d.drawImage(
                    eventImage, 0, 0, cropWidth, cropHeight, cropX, cropY, cropX + cropWidth, cropY + cropHeight, null);
            g2d.dispose();
            g.drawImage(croppedImage, 118, leaderheightLoc, null);
        }
        if (player.getAdminAdvisor() != null) {
            BufferedImage eventImage =
                    ImageHelper.read(ResourceHelper.getInstance().getCharacterFile(player.getAdminAdvisor()));
            BufferedImage croppedImage = new BufferedImage(cropWidth, cropHeight, eventImage.getType());
            Graphics2D g2d = croppedImage.createGraphics();
            g2d.drawImage(
                    eventImage, 0, 0, cropWidth, cropHeight, cropX, cropY, cropX + cropWidth, cropY + cropHeight, null);
            g2d.dispose();
            g.drawImage(croppedImage, 572, leaderheightLoc, null);
        }
        if (player.getDiploAdvisor() != null) {
            BufferedImage eventImage =
                    ImageHelper.read(ResourceHelper.getInstance().getCharacterFile(player.getDiploAdvisor()));
            BufferedImage croppedImage = new BufferedImage(cropWidth, cropHeight, eventImage.getType());
            Graphics2D g2d = croppedImage.createGraphics();
            g2d.drawImage(
                    eventImage, 0, 0, cropWidth, cropHeight, cropX, cropY, cropX + cropWidth, cropY + cropHeight, null);
            g2d.dispose();
            g.drawImage(croppedImage, 950, leaderheightLoc, null);
        }
        if (player.getMilitaryAdvisor() != null) {
            BufferedImage eventImage =
                    ImageHelper.read(ResourceHelper.getInstance().getCharacterFile(player.getMilitaryAdvisor()));
            BufferedImage croppedImage = new BufferedImage(cropWidth, cropHeight, eventImage.getType());
            Graphics2D g2d = croppedImage.createGraphics();
            g2d.drawImage(
                    eventImage, 0, 0, cropWidth, cropHeight, cropX, cropY, cropX + cropWidth, cropY + cropHeight, null);
            g2d.dispose();
            g.drawImage(croppedImage, 1340, leaderheightLoc, null);
        }
    }

    public static void drawArmy(Player player, Army army, Graphics2D g, int xPos) {
        BufferedImage armyImage =
                ImageHelper.readScaled(ResourceHelper.getInstance().getPlayerFile(player, "Infantry"), 0.25f);
        int yStart = 220;
        int infInRow = 6;
        if (player.isBot()) {
            yStart = 674;
            infInRow = 8;
            for (int x = 0; x < player.getManpowerAvailable(); x++) {
                int y = 440 + ((armyImage.getHeight() + 10) * (x / infInRow));
                int xP = xPos + (armyImage.getWidth() + 10) * (x % infInRow);
                g.drawImage(armyImage, xP, y, null);
            }
        }
        for (int x = 0; x < army.getInfantry(); x++) {
            int y = yStart + ((armyImage.getHeight() + 10) * (x / infInRow));
            int xP = xPos + (armyImage.getWidth() + 10) * (x % infInRow);
            g.drawImage(armyImage, xP, y, null);
        }
        for (int x = 0; x < army.getCavalry(); x++) {
            int y = 470 + ((armyImage.getHeight() + 10) * (x / 6));
            int xP = xPos + (armyImage.getWidth() + 10) * (x % 6);
            g.drawImage(armyImage, xP, y, null);
        }
        for (int x = 0; x < army.getArtillery(); x++) {
            int y = 660 + ((armyImage.getHeight() + 10) * (x / 6));
            int xP = xPos + (armyImage.getWidth() + 10) * (x % 6);
            g.drawImage(armyImage, xP, y, null);
        }
    }

    public static void drawFleet(Player player, Fleet fleet, Graphics2D g, int xPos) {
        BufferedImage armyImage =
                ImageHelper.readScaled(ResourceHelper.getInstance().getPlayerFile(player, "Ship"), 0.15f);
        for (int x = 0; x < fleet.getLightShips(); x++) {
            int y = 220 + ((armyImage.getHeight() + 10) * (x / 4));
            int xP = xPos + (armyImage.getWidth() + 10) * (x % 4);
            g.drawImage(armyImage, xP, y, null);
        }
        for (int x = 0; x < fleet.getHeavyShips(); x++) {
            int y = 470 + ((armyImage.getHeight() + 10) * (x / 4));
            int xP = xPos + (armyImage.getWidth() + 10) * (x % 4);
            g.drawImage(armyImage, xP, y, null);
        }
        for (int x = 0; x < fleet.getGalleys(); x++) {
            int y = 660 + ((armyImage.getHeight() + 10) * (x / 4));
            int xP = xPos + (armyImage.getWidth() + 10) * (x % 4);
            g.drawImage(armyImage, xP, y, null);
        }
    }

    public static void drawForceCharacters(Player player, Graphics2D g, String color) {
        int cropX = 10;
        int cropY = 464;
        int cropWidth = 356;
        int cropHeight = 91;
        int leaderheightLoc = 789 + 97;
        if (player.getArmy1().getLeader() != null) {
            BufferedImage eventImage = ImageHelper.read(ResourceHelper.getInstance()
                    .getCharacterFile(player.getArmy1().getLeader()));
            BufferedImage croppedImage = new BufferedImage(cropWidth, cropHeight, eventImage.getType());
            Graphics2D g2d = croppedImage.createGraphics();
            g2d.drawImage(
                    eventImage, 0, 0, cropWidth, cropHeight, cropX, cropY, cropX + cropWidth, cropY + cropHeight, null);
            g2d.dispose();
            g.drawImage(croppedImage, 40, leaderheightLoc, null);
        }
        if (player.getArmy2().getLeader() != null) {
            BufferedImage eventImage = ImageHelper.read(ResourceHelper.getInstance()
                    .getCharacterFile(player.getArmy2().getLeader()));
            BufferedImage croppedImage = new BufferedImage(cropWidth, cropHeight, eventImage.getType());
            Graphics2D g2d = croppedImage.createGraphics();
            g2d.drawImage(
                    eventImage, 0, 0, cropWidth, cropHeight, cropX, cropY, cropX + cropWidth, cropY + cropHeight, null);
            g2d.dispose();
            g.drawImage(croppedImage, 440, leaderheightLoc, null);
        }
        if (player.getArmy3().getLeader() != null) {
            BufferedImage eventImage = ImageHelper.read(ResourceHelper.getInstance()
                    .getCharacterFile(player.getArmy3().getLeader()));
            BufferedImage croppedImage = new BufferedImage(cropWidth, cropHeight, eventImage.getType());
            Graphics2D g2d = croppedImage.createGraphics();
            g2d.drawImage(
                    eventImage, 0, 0, cropWidth, cropHeight, cropX, cropY, cropX + cropWidth, cropY + cropHeight, null);
            g2d.dispose();
            g.drawImage(croppedImage, 840, leaderheightLoc, null);
        }
        if (player.getFleet().getLeader() != null) {
            BufferedImage eventImage = ImageHelper.read(ResourceHelper.getInstance()
                    .getCharacterFile(player.getFleet().getLeader()));
            BufferedImage croppedImage = new BufferedImage(cropWidth, cropHeight, eventImage.getType());
            Graphics2D g2d = croppedImage.createGraphics();
            g2d.drawImage(
                    eventImage, 0, 0, cropWidth, cropHeight, cropX, cropY, cropX + cropWidth, cropY + cropHeight, null);
            g2d.dispose();
            g.drawImage(croppedImage, 1240, leaderheightLoc, null);
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
        if (!player.isBot()) {
            for (int x = 0; x < 10; x++) {
                if (x < (player.getVassalProvinces().size() + 1) / 2) continue;
                g.drawImage(vassalImage, 580 + x * (vassalImage.getWidth() + 15), 480, null);
            }
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
        int x = 429;
        int y = 728;
        if (player.isBot()) {
            x = 784;
            y = 728;
        }
        if (stateReligion != null) {
            BufferedImage religionImage =
                    ImageHelper.readScaled(ResourceHelper.getInstance().getReligionFile(stateReligion), 0.60f);
            g.drawImage(religionImage, x, y, null);
        }
    }
}
