package ootie.helpers;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.dv8tion.jda.api.utils.FileUpload;
import ootie.ResourceHelper;
import ootie.game.Game;
import ootie.game.Player;
import ootie.image.DrawingUtil;
import ootie.image.ImageHelper;
import ootie.image.Mapper;
import ootie.model.AreaModel;
import ootie.model.IdeaModel;
import ootie.model.ProvinceModel;
import ootie.service.image.FileUploadService;

public class GameImageHelper {

    public static FileUpload drawEvents(Game game) {

        List<String> revealedEvents = game.getRevealedEventCards();
        if (revealedEvents.isEmpty()) {
            revealedEvents.add("11A-1");
            revealedEvents.add("11A-2");
            revealedEvents.add("11A-3");
        }
        int age = game.getAge();
        List<String> unrevealedEvents = game.getUnrevealedEventCards();
        if (unrevealedEvents.isEmpty()) {
            unrevealedEvents.add("11A-4");
            unrevealedEvents.add("12A-1");
            unrevealedEvents.add("12A-2");
        }
        // 376x585
        // 600x924
        if (revealedEvents.size() + unrevealedEvents.size() < 1) {
            return FileUploadService.createFileUpload(
                    new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB), "events");
        }
        BufferedImage cardBackImage =
                ImageHelper.readScaled(ResourceHelper.getInstance().getCardBackFile("Age" + age + "Event"), 600, 924);
        BufferedImage finalImage = new BufferedImage(
                (cardBackImage.getWidth() + 10) * (unrevealedEvents.size() + revealedEvents.size()) - 10,
                cardBackImage.getHeight(),
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = finalImage.createGraphics();

        int x = 0;
        for (String event : revealedEvents) {
            BufferedImage eventImage =
                    ImageHelper.read(ResourceHelper.getInstance().getEventFile(event));
            g.drawImage(eventImage, x, 0, null);
            x += cardBackImage.getWidth() + 10;
        }
        for (String event : unrevealedEvents) {
            g.drawImage(cardBackImage, x, 0, null);
            x += cardBackImage.getWidth() + 10;
        }

        FileUpload fileUpload = FileUploadService.createFileUpload(finalImage, "events");
        return fileUpload;
    }

    public static FileUpload drawHREnPC(Game game) {
        BufferedImage ogImage = ImageHelper.read(ResourceHelper.getInstance().getMiscFile("HRE_PC"));
        BufferedImage mapImage =
                new BufferedImage(ogImage.getWidth(), ogImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = mapImage.createGraphics();
        g.drawImage(ogImage, 0, 0, ogImage.getWidth(), ogImage.getHeight(), null);
        int playerCount = 0;
        for (Player p : game.getGamePlayers()) {
            BufferedImage playerChit =
                    ImageHelper.read(ResourceHelper.getInstance().getPlayerFile(p, "Claim"));
            if (p.isDefendingHRE()) {
                g.drawImage(playerChit, 986, 433, null);
                g.setFont(g.getFont().deriveFont(80f));
                DrawingUtil.superDrawStringCentered(
                        g, "" + game.getImperialManpower(), 883, 687, Color.BLACK, DrawingUtil.stroke(7), Color.WHITE);
            }
            if (p.isCommittedToCrusade()) {
                int xMod = playerCount % 3 * playerChit.getWidth();
                int yMod = playerCount / 3 * playerChit.getHeight();
                g.drawImage(playerChit, 920 + xMod, 270 + yMod, null);
            }
            int xMod = playerCount % 2 * playerChit.getWidth() - 20;
            int yMod = playerCount * playerChit.getHeight();
            if (p.isUsedEvent()) {
                if (p.isPassed()) {
                    int order = game.getPassedPlayerOrder().indexOf(p.getColor());
                    yMod = (playerChit.getHeight() + 20) * order;
                    xMod = 0;
                    if (order == 5) {
                        xMod = 50;
                    }
                    g.drawImage(playerChit, 1340 + xMod, 132 + yMod, null);
                } else {
                    g.drawImage(playerChit, 1217 + xMod, 125 + yMod, null);
                }
            } else {
                g.drawImage(playerChit, 1104 + xMod, 125 + yMod, null);
            }
            playerCount++;
        }

        if (game.getRomaCardinal() != null) {
            BufferedImage playerChit = ImageHelper.read(ResourceHelper.getInstance()
                    .getPlayerFile(
                            game.getPlayerByRealmOrColor(game.getRomaCardinal()).getColor(), "Cube"));
            g.drawImage(playerChit, 155, 175, null);
        }
        playerCount = 0;
        for (String realm : game.getNonRomaCardinals()) {
            BufferedImage playerChit = ImageHelper.read(ResourceHelper.getInstance()
                    .getPlayerFile(game.getPlayerByRealmOrColor(realm).getColor(), "Cube"));
            int xMod = playerCount * 145;
            g.drawImage(playerChit, 283 + xMod, 175, null);
            playerCount++;
        }

        BufferedImage stabilityImage =
                ImageHelper.readScaled(ResourceHelper.getInstance().getMiscFile("Imperial Authority"), 0.31f);
        g.drawImage(stabilityImage, 550 + game.getImperialAuthority() * (stabilityImage.getWidth() + 8), 70, null);

        String stateReligion = game.getHreReligion();
        if (stateReligion != null) {
            BufferedImage religionImage =
                    ImageHelper.readScaled(ResourceHelper.getInstance().getMiscFile(stateReligion), 0.60f);
            g.drawImage(religionImage, 100, 593, null);
        }

        FileUpload fileUpload = FileUploadService.createFileUpload(mapImage, "hre_pc");
        return fileUpload;
    }

    public static FileUpload drawMap(Game game) {
        BufferedImage ogImage = ImageHelper.read(ResourceHelper.getInstance().getMapFile("fullmap1444"));
        int outputWidth = ogImage.getWidth() / 2;
        int outputHeight = ogImage.getHeight() / 2;
        BufferedImage mapImage = new BufferedImage(outputWidth, outputHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = mapImage.createGraphics();
        g.drawImage(ogImage, 0, 0, outputWidth, outputHeight, null);
        g.scale(0.5, 0.5);
        int w = 6803;
        int h = 4104;
        double minx = -21.45; // -18.45 //-20.95 //-21
        double miny = -12.74; // -12.14
        double maxx = 21.53; // 20.81 //21
        double maxy = 13.35; // 11.65
        for (Player player : game.getPlayers().values()) {
            if (player.getColor() == null) {
                continue;
            }
            BufferedImage townImage = ImageHelper.readScaled(
                    ResourceHelper.getInstance().getPlayerFile(player.getColor(), "Town"), 0.46f);
            BufferedImage unrestImage = ImageHelper.readScaled(
                    ResourceHelper.getInstance().getPlayerFile(player.getColor(), "Unrest_White"), 0.46f);
            for (String prov : player.getSmallProvinces()) {
                if (Mapper.getProvinces().containsKey(prov)) {
                    ProvinceModel model = Mapper.getProvinces().get(prov);
                    double x = model.getX();
                    double y = model.getY();
                    double nx = (x - minx) / (maxx - minx);
                    double ny = (y - miny) / (maxy - miny);
                    int px = (int) (nx * w);
                    int py = (int) ((1 - ny) * h);
                    px -= 80;
                    py -= 50;
                    if (player.getUnrestProvinces().contains(prov)) {
                        g.drawImage(unrestImage, px, py, null);
                    } else {
                        g.drawImage(townImage, px, py, null);
                    }
                }
            }
            unrestImage = ImageHelper.readScaled(
                    ResourceHelper.getInstance().getPlayerFile(player.getColor(), "Unrest_White"), 0.595f);
            townImage = ImageHelper.readScaled(
                    ResourceHelper.getInstance().getPlayerFile(player.getColor(), "Town"), 0.595f);
            for (String prov : player.getLargeProvinces()) {
                if (Mapper.getProvinces().containsKey(prov)) {
                    ProvinceModel model = Mapper.getProvinces().get(prov);
                    double x = model.getX();
                    double y = model.getY();
                    double nx = (x - minx) / (maxx - minx);
                    double ny = (y - miny) / (maxy - miny);
                    int px = (int) (nx * w);
                    int py = (int) ((1 - ny) * h);
                    px -= 80;
                    py -= 105;
                    if (player.getUnrestProvinces().contains(prov)) {
                        g.drawImage(unrestImage, px, py, null);
                    } else {
                        g.drawImage(townImage, px, py, null);
                    }
                }
            }
            int playerP = player.getPrestige();
            BufferedImage prestigeImage = ImageHelper.readScaled(
                    ResourceHelper.getInstance().getPlayerFile(player.getColor(), "Prestige"), 0.5f);
            BufferedImage negativePImage = ImageHelper.readScaled(
                    ResourceHelper.getInstance().getPlayerFile(player.getColor(), "Negative Prestige"), 0.25f);
            if (playerP > 60) {
                prestigeImage = ImageHelper.readScaled(
                        ResourceHelper.getInstance().getPlayerFile(player.getColor(), "60Prestige"), 0.5f);
            }
            boolean negative = false;
            if (playerP < 0) {
                playerP = Math.abs(playerP);
                negative = true;
            }
            int prestX = 120 + ((((playerP - 1) / 30) % 2) * 129);
            int prestY = 3847 - ((playerP - 1) % 30 * 127);
            if (playerP == 0) {
                prestY = 3975;
            }
            g.drawImage(prestigeImage, prestX, prestY, null);
            if (negative) {
                g.drawImage(negativePImage, prestX, prestY, null);
            }
        }
        g.dispose();

        FileUpload fileUpload = FileUploadService.createFileUpload(mapImage, "fullmap1444");
        return fileUpload;
    }

    public static FileUpload drawSpecificLocation(String location) {
        BufferedImage ogImage = ImageHelper.read(ResourceHelper.getInstance().getMapFile("fullmap1444"));
        int radius = 300;
        BufferedImage mapImage = new BufferedImage(radius * 2, radius * 2, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = mapImage.createGraphics();
        int w = 6803;
        int h = 4104;
        double minx = -21.45;
        double miny = -12.74;
        double maxx = 21.53;
        double maxy = 13.35;
        int px = 0;
        int py = 0;
        if (Mapper.getProvinces().containsKey(location)) {
            ProvinceModel model = Mapper.getProvinces().get(location);
            double x = model.getX();
            double y = model.getY();
            double nx = (x - minx) / (maxx - minx);
            double ny = (y - miny) / (maxy - miny);
            px = (int) (nx * w);
            py = (int) ((1 - ny) * h);
        }
        if (Mapper.getAreas().containsKey(location)) {
            AreaModel model = Mapper.getAreas().get(location);
            px = model.getReligiousCenter()[0];
            py = model.getReligiousCenter()[1];
        }
        g.drawImage(ogImage, 0, 0, radius * 2, radius * 2, px - radius, py - radius, px + radius, py + radius, null);
        g.dispose();

        FileUpload fileUpload = FileUploadService.createFileUpload(mapImage, "fullmap1444");
        return fileUpload;
    }

    public static FileUpload drawIdeas(Game game) {

        int age = game.getAge();

        List<String> militaryIdeas = game.getMilitaryIdeas();
        List<String> unrevealedMilIdeas = new ArrayList<>();
        for (IdeaModel idea : Mapper.getIdeas().values()) {
            if (idea.getAge() > age) {
                continue;
            }
            if (!idea.getType().equalsIgnoreCase("military")) {
                continue;
            }
            unrevealedMilIdeas.add(idea.getId());
            Collections.shuffle(unrevealedMilIdeas);
        }
        if (militaryIdeas.isEmpty()) {
            militaryIdeas.add("MIL-Cannons");
            unrevealedMilIdeas.remove("MIL-Cannons");
            militaryIdeas.add(unrevealedMilIdeas.removeFirst());
            militaryIdeas.add(unrevealedMilIdeas.removeFirst());
        }

        List<String> diplomaticIdeas = game.getDiplomaticIdeas();
        List<String> unrevealedDipIdeas = new ArrayList<>();
        for (IdeaModel idea : Mapper.getIdeas().values()) {
            if (idea.getAge() > age) {
                continue;
            }
            if (!idea.getType().equalsIgnoreCase("diplomatic")) {
                continue;
            }
            unrevealedDipIdeas.add(idea.getId());
            Collections.shuffle(unrevealedDipIdeas);
        }
        if (diplomaticIdeas.isEmpty()) {
            diplomaticIdeas.add("DIP-Quest-for-the-New-World");
            unrevealedDipIdeas.remove("DIP-Quest-for-the-New-World");
            diplomaticIdeas.add(unrevealedDipIdeas.removeFirst());
            diplomaticIdeas.add(unrevealedDipIdeas.removeFirst());
        }

        List<String> administrativeIdeas = game.getAdministrativeIdeas();
        List<String> unrevealedAdmIdeas = new ArrayList<>();
        for (IdeaModel idea : Mapper.getIdeas().values()) {
            if (idea.getAge() > age) {
                continue;
            }
            if (!idea.getType().equalsIgnoreCase("administrative")) {
                continue;
            }
            unrevealedAdmIdeas.add(idea.getId());
            Collections.shuffle(unrevealedAdmIdeas);
        }
        if (administrativeIdeas.isEmpty()) {
            administrativeIdeas.add("ADM-Tolerance");
            unrevealedAdmIdeas.remove("ADM-Tolerance");
            administrativeIdeas.add(unrevealedAdmIdeas.removeFirst());
            administrativeIdeas.add(unrevealedAdmIdeas.removeFirst());
        }

        BufferedImage cardImage = ImageHelper.read(ResourceHelper.getInstance().getIdeaFile("ADM-Tolerance"));

        int maxRows = 1 + Math.max(Math.max(administrativeIdeas.size(), militaryIdeas.size()), diplomaticIdeas.size());

        BufferedImage finalImage = new BufferedImage(
                cardImage.getWidth() * 3 + 60,
                cardImage.getHeight() * maxRows + ((maxRows - 1) * 30),
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = finalImage.createGraphics();

        BufferedImage cardBackImage = ImageHelper.readScaled(
                ResourceHelper.getInstance().getCardBackFile("AdminIdeas"),
                cardImage.getWidth(),
                cardImage.getHeight());
        int x = 0;
        int y = 0;
        int textXAdjuster = 10;
        int textYAdjuster = 90;
        g.setFont(g.getFont().deriveFont(80f));
        g.drawImage(cardBackImage, x, y, null);
        DrawingUtil.superDrawStringCentered(
                g,
                "" + unrevealedAdmIdeas.size(),
                x + cardBackImage.getWidth() / 2 - textXAdjuster,
                y + textYAdjuster,
                Color.BLACK,
                DrawingUtil.stroke(7),
                Color.WHITE);
        y += cardBackImage.getHeight() + 30;
        for (String idea : administrativeIdeas) {
            cardImage = ImageHelper.read(ResourceHelper.getInstance().getIdeaFile(idea));
            g.drawImage(cardImage, x, y, null);
            y += cardImage.getHeight() + 30;
        }

        cardBackImage = ImageHelper.readScaled(
                ResourceHelper.getInstance().getCardBackFile("DiplomaticIdeas"),
                cardImage.getWidth(),
                cardImage.getHeight());
        x += cardBackImage.getWidth() + 30;
        y = 0;
        g.drawImage(cardBackImage, x, y, null);
        DrawingUtil.superDrawStringCentered(
                g,
                "" + unrevealedDipIdeas.size(),
                x + cardBackImage.getWidth() / 2 - textXAdjuster,
                y + textYAdjuster,
                Color.BLACK,
                DrawingUtil.stroke(7),
                Color.WHITE);
        y += cardBackImage.getHeight() + 30;
        for (String idea : diplomaticIdeas) {
            cardImage = ImageHelper.read(ResourceHelper.getInstance().getIdeaFile(idea));
            g.drawImage(cardImage, x, y, null);
            y += cardImage.getHeight() + 30;
        }

        cardBackImage = ImageHelper.readScaled(
                ResourceHelper.getInstance().getCardBackFile("MilitaryIdeas"),
                cardImage.getWidth(),
                cardImage.getHeight());
        x += cardBackImage.getWidth() + 30;
        y = 0;
        g.drawImage(cardBackImage, x, y, null);
        DrawingUtil.superDrawStringCentered(
                g,
                "" + unrevealedMilIdeas.size(),
                x + cardBackImage.getWidth() / 2 - textXAdjuster,
                y + textYAdjuster,
                Color.BLACK,
                DrawingUtil.stroke(7),
                Color.WHITE);
        y += cardBackImage.getHeight() + 30;
        for (String idea : militaryIdeas) {
            cardImage = ImageHelper.read(ResourceHelper.getInstance().getIdeaFile(idea));
            g.drawImage(cardImage, x, y, null);
            y += cardImage.getHeight() + 30;
        }

        FileUpload fileUpload = FileUploadService.createFileUpload(finalImage, "ideas");
        return fileUpload;
    }
}
