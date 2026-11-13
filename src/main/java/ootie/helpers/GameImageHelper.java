package ootie.helpers;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.dv8tion.jda.api.utils.FileUpload;
import ootie.ResourceHelper;
import ootie.discord.commands.search.SearchImageHelper;
import ootie.game.Game;
import ootie.game.Player;
import ootie.image.DrawingUtil;
import ootie.image.ImageHelper;
import ootie.image.Mapper;
import ootie.model.AreaModel;
import ootie.model.IdeaModel;
import ootie.model.MilestoneModel;
import ootie.model.ProvinceModel;
import ootie.model.SeazoneModel;
import ootie.model.Source.ComponentSource;
import ootie.service.image.FileUploadService;

public class GameImageHelper {

    public static BufferedImage getDecksImage(Game game) {
        BufferedImage exampleImage =
                ImageHelper.read(ResourceHelper.getInstance().getCardBackFile("AdministrativeAction"));
        BufferedImage finalImage = new BufferedImage(
                (exampleImage.getWidth() + 10) * 4 - 10, exampleImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = finalImage.createGraphics();
        int x = 0;
        g.drawImage(exampleImage, x, 0, null);
        x += exampleImage.getWidth() + 10;

        BufferedImage diploImage = ImageHelper.read(ResourceHelper.getInstance().getCardBackFile("MilitaryAction"));
        g.drawImage(diploImage, x, 0, null);
        x += exampleImage.getWidth() + 10;

        BufferedImage warfareImage =
                ImageHelper.read(ResourceHelper.getInstance().getCardBackFile("DiplomaticAction"));
        g.drawImage(warfareImage, x, 0, null);
        x += exampleImage.getWidth() + 10;

        BufferedImage tradeImage = ImageHelper.read(ResourceHelper.getInstance().getCardBackFile("TradeCard"));
        g.drawImage(tradeImage, x, 0, null);

        return finalImage;
    }

    public static BufferedImage getMilestonesImage(Game game) {
        MilestoneModel model = Mapper.getMilestones().get("Dynastic Ties");
        BufferedImage exampleImage = getMilestoneImage(model);
        BufferedImage finalImage = new BufferedImage(
                (exampleImage.getWidth() + 10) * 4 - 10, exampleImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = finalImage.createGraphics();

        int x = 0;
        if (game.getWarfareMilestones() != null && !game.getWarfareMilestones().isEmpty()) {
            String wMilestone = game.getWarfareMilestones().getFirst();
            BufferedImage wBufferedImage =
                    getMilestoneImage(Mapper.getMilestones().get(wMilestone));
            g.drawImage(wBufferedImage, x, 0, null);
            x += exampleImage.getWidth() + 10;
        }
        if (game.getPoliticalMilestones() != null
                && !game.getPoliticalMilestones().isEmpty()) {
            String pMilestone = game.getPoliticalMilestones().getFirst();
            BufferedImage pBufferedImage =
                    getMilestoneImage(Mapper.getMilestones().get(pMilestone));
            g.drawImage(pBufferedImage, x, 0, null);
            x += exampleImage.getWidth() + 10;
        }
        if (game.getEconomicMilestones() != null
                && !game.getEconomicMilestones().isEmpty()) {
            String eMilestone = game.getEconomicMilestones().getFirst();
            BufferedImage eBufferedImage =
                    getMilestoneImage(Mapper.getMilestones().get(eMilestone));
            g.drawImage(eBufferedImage, x, 0, null);
            x += exampleImage.getWidth() + 10;
        }
        if (game.getExpansionMilestones() != null
                && !game.getExpansionMilestones().isEmpty()) {
            String exMilestone = game.getExpansionMilestones().getFirst();
            BufferedImage exBufferedImage =
                    getMilestoneImage(Mapper.getMilestones().get(exMilestone));
            g.drawImage(exBufferedImage, x, 0, null);
            x += exampleImage.getWidth() + 10;
        }
        return finalImage;
    }

    public static BufferedImage getMilestoneImage(MilestoneModel model) {
        String age =
                switch (model.getAge()) {
                    case 1 -> "I";
                    case 2 -> "II";
                    case 3 -> "III";
                    case 4 -> "IV";
                    default -> throw new IllegalArgumentException("Invalid milestone age: " + model.getAge());
                };
        String folder = model.getSource() == ComponentSource.defying_destiny
                ? "images/Milestones/DD Milestones Age " + age
                : "images/Milestones/Milestones_Age_" + age;
        BufferedImage exampleImage = SearchImageHelper.getImage(folder, model.getId());
        return exampleImage;
    }

    public static FileUpload drawEvents(Game game) {
        FileUpload fileUpload = FileUploadService.createFileUpload(getEventsImage(game), "events");
        return fileUpload;
    }

    public static BufferedImage getEventsImage(Game game) {
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
            return new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
        }
        BufferedImage cardBackImage =
                ImageHelper.readScaled(ResourceHelper.getInstance().getCardBackFile("Age" + age + "Event"), 600, 924);
        BufferedImage finalImage = new BufferedImage(
                ((cardBackImage.getWidth() + 10) * (unrevealedEvents.size() + revealedEvents.size()) - 10) / 2,
                cardBackImage.getHeight() / 2,
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = finalImage.createGraphics();
        g.scale(0.5, 0.5);

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

        return finalImage;
    }

    public static BufferedImage drawPlayerAreas(Game game) {
        Player p1 = game.getGamePlayers().get(0);
        BufferedImage pBoard = PlayerHelper.getPlayerAreaImage(p1);
        BufferedImage pUnits = PlayerHelper.getArmysNFleetsImage(p1);
        int maxH = Math.max(pBoard.getHeight(), pUnits.getHeight())
                * game.getGamePlayers().size();
        int maxW = pBoard.getWidth() + pUnits.getWidth();

        BufferedImage statsImage = new BufferedImage(maxW, maxH, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = statsImage.createGraphics();
        int y = 0;
        for (Player p : game.getGamePlayers()) {
            int x = 0;
            g.drawImage(PlayerHelper.getPlayerAreaImage(p), x, y, null);
            x = pBoard.getWidth();
            g.drawImage(PlayerHelper.getArmysNFleetsImage(p), x, y, null);
            y += maxH / game.getGamePlayers().size();
        }
        return statsImage;
    }

    public static FileUpload drawStats(Game game) {

        BufferedImage eventsImage = getEventsImage(game);
        BufferedImage milestonesImage = getMilestonesImage(game);
        BufferedImage decksImage = getDecksImage(game);
        BufferedImage hreImage = getHREnPCImage(game);
        BufferedImage ideaImage = getIdeasImage(game);
        BufferedImage playerAreas = drawPlayerAreas(game);
        int maxH = Math.max(
                playerAreas.getHeight(),
                eventsImage.getHeight()
                        + milestonesImage.getHeight()
                        + decksImage.getHeight()
                        + hreImage.getHeight()
                        + ideaImage.getHeight());
        int maxW = playerAreas.getWidth()
                + 10
                + Math.max(
                        eventsImage.getWidth(),
                        Math.max(
                                milestonesImage.getWidth(),
                                Math.max(decksImage.getWidth(), Math.max(ideaImage.getWidth(), hreImage.getWidth()))));

        BufferedImage statsImage = new BufferedImage(maxW, maxH, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = statsImage.createGraphics();
        int x = 0;
        int y = 0;
        g.drawImage(playerAreas, x, y, null);
        x = playerAreas.getWidth() + 10;
        g.drawImage(eventsImage, x, y, null);
        y += eventsImage.getHeight();
        g.drawImage(milestonesImage, x, y, null);
        y += milestonesImage.getHeight();
        g.drawImage(decksImage, x, y, null);
        y += decksImage.getHeight();
        g.drawImage(hreImage, x, y, null);
        y += hreImage.getHeight();
        g.drawImage(ideaImage, x, y, null);
        System.out.println(statsImage.getWidth() + " " + statsImage.getHeight());

        return FileUploadService.createFileUpload(statsImage, "stats");
    }

    public static FileUpload drawHREnPC(Game game) {
        return FileUploadService.createFileUpload(getHREnPCImage(game), "hre_pc");
    }

    public static BufferedImage getHREnPCImage(Game game) {
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
            BufferedImage playerChit = ImageHelper.readScaled(
                    ResourceHelper.getInstance()
                            .getPlayerFile(
                                    game.getPlayerByRealmOrColor(game.getRomaCardinal())
                                            .getColor(),
                                    "Cube"),
                    50,
                    50);
            g.drawImage(playerChit, 155, 175, null);
        }
        playerCount = 0;
        for (String realm : game.getNonRomaCardinals()) {
            BufferedImage playerChit = ImageHelper.readScaled(
                    ResourceHelper.getInstance()
                            .getPlayerFile(game.getPlayerByRealmOrColor(realm).getColor(), "Cube"),
                    50,
                    50);
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
                    ImageHelper.readScaled(ResourceHelper.getInstance().getReligionFile(stateReligion), 0.60f);
            g.drawImage(religionImage, 100, 593, null);
        }

        return mapImage;
    }

    public static FileUpload drawMap(Game game) {
        BufferedImage ogImage = ImageHelper.read(ResourceHelper.getInstance().getMapFile("fullmap1444"));
        if (game.getScenario().equalsIgnoreCase("S1-00")) {
            ogImage = ogImage.getSubimage(0, 0, 4409, ogImage.getHeight());
        }
        int outputWidth = ogImage.getWidth() / 2;
        int outputHeight = ogImage.getHeight() / 2;
        BufferedImage mapImage = new BufferedImage(outputWidth, outputHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = mapImage.createGraphics();
        g.drawImage(ogImage, 0, 0, outputWidth, outputHeight, null);

        g.scale(0.5, 0.5);
        for (Player player : game.getPlayers().values()) {
            if (player.getColor() == null) {
                continue;
            }
            BufferedImage townImage = ImageHelper.readScaled(
                    ResourceHelper.getInstance().getPlayerFile(player.getColor(), "Town"), 0.36f);
            BufferedImage unrestImage = ImageHelper.readScaled(
                    ResourceHelper.getInstance().getPlayerFile(player.getColor(), "Unrest_White"), 0.36f);
            for (String prov : player.getSmallProvinces()) {
                if (Mapper.getProvinces().containsKey(prov)) {
                    ProvinceModel model = Mapper.getProvinces().get(prov);
                    int px = model.getX();
                    int py = model.getY();
                    if (player.getUnrestProvinces().contains(prov)) {
                        px -= unrestImage.getWidth() / 2;
                        py = py - unrestImage.getHeight() / 2 - 45;
                        g.drawImage(unrestImage, px, py, null);
                    } else {
                        px -= townImage.getWidth() / 2;
                        py = py - townImage.getHeight() / 2 - 45;
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
                    int px = model.getX();
                    int py = model.getY();
                    if (player.getUnrestProvinces().contains(prov)) {
                        px -= unrestImage.getWidth() / 2;
                        py = py - unrestImage.getHeight() / 2 - 45;
                        g.drawImage(unrestImage, px, py, null);
                    } else {
                        px -= townImage.getWidth() / 2;
                        py = py - townImage.getHeight() / 2 - 45;
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
        BufferedImage rebelImage =
                ImageHelper.readScaled(ResourceHelper.getInstance().getMiscFile("Rebel"), 0.3f);

        for (SeazoneModel seazoneModel : Mapper.getSeazones().values()) {
            int x = 0;
            int protectedShips = 0;
            for (String ship : game.getUnitsInArea(seazoneModel.getId())) {
                float scale = 0.25f;
                if (ship.contains("fleet")) {
                    scale = 0.35f;
                }
                BufferedImage playerShip = ImageHelper.readScaled(
                        ResourceHelper.getInstance().getPlayerFile(ship.split("_")[0], "Ship"), scale);
                int xCoord = seazoneModel.getTradeProtection1()[0] - playerShip.getWidth() / 2;
                int yCoord = seazoneModel.getTradeProtection1()[1] - playerShip.getHeight() / 2 - 5;

                if (ship.contains("_protected")) {
                    protectedShips++;
                    switch (protectedShips) {
                        case 2:
                            xCoord = seazoneModel.getTradeProtection2()[0] - playerShip.getWidth() / 2;
                            yCoord = seazoneModel.getTradeProtection2()[1] - playerShip.getHeight() / 2 - 5;
                            break;
                        case 3:
                            xCoord = seazoneModel.getTradeProtection3()[0] - playerShip.getWidth() / 2;
                            yCoord = seazoneModel.getTradeProtection3()[1] - playerShip.getHeight() / 2 - 5;
                            break;

                        default:
                            break;
                    }
                } else {
                    xCoord += x * 40;
                    yCoord -= playerShip.getHeight();
                }
                g.drawImage(playerShip, xCoord, yCoord, null);
                x++;
            }
        }
        for (AreaModel area : Mapper.getAreas().values()) {
            // g.drawImage(
            //         religionImage,
            //         area.getReligiousCenter()[0] - religionImage.getWidth() / 2,
            //         area.getReligiousCenter()[1] - religionImage.getHeight() / 2,
            //         null);
            // g.drawImage(
            //         rebelImage,
            //         area.getInfantryPlacement()[0] - rebelImage.getWidth() / 2,
            //         area.getInfantryPlacement()[1] - rebelImage.getHeight() / 2,
            //         null);
            if (game.getStoredAreaReligions().containsKey(area.getId())) {
                BufferedImage religionImage = ImageHelper.readScaled(
                        ResourceHelper.getInstance()
                                .getReligionFile(game.getStoredAreaReligions().get(area.getId())),
                        0.60f);
                g.drawImage(
                        religionImage,
                        area.getReligiousCenter()[0] - religionImage.getWidth() / 2,
                        area.getReligiousCenter()[1] - religionImage.getHeight() / 2,
                        null);
            }
            int x = 0;
            for (String influence : game.getInfluenceInArea(area.getId())) {
                int cosResult = (int) (50 * Math.cos(Math.toRadians(x)));
                int sinResult = (int) (50 * Math.sin(Math.toRadians(x)));
                BufferedImage playerCube =
                        ImageHelper.readScaled(ResourceHelper.getInstance().getPlayerFile(influence, "Cube"), 30, 30);
                g.drawImage(
                        playerCube,
                        area.getReligiousCenter()[0] + cosResult - playerCube.getWidth() / 2,
                        area.getReligiousCenter()[1] + sinResult - playerCube.getHeight() / 2,
                        null);
                x += 72;
            }
            x = 0;
            for (String claim : game.getClaimsInArea(area.getId())) {
                int cosResult = (int) (20 * Math.cos(Math.toRadians(x)));
                int sinResult = (int) (20 * Math.sin(Math.toRadians(x)));
                BufferedImage playerCube =
                        ImageHelper.readScaled(ResourceHelper.getInstance().getPlayerFile(claim, "Claim"), 30, 30);
                g.drawImage(
                        playerCube,
                        area.getReligiousCenter()[0] + cosResult - playerCube.getWidth() / 2,
                        area.getReligiousCenter()[1] + sinResult - playerCube.getHeight() / 2,
                        null);
                x += 72;
            }
            x = 0;
            for (String infantry : game.getUnitsInArea(area.getId())) {
                if (infantry.contains("rebel")) {
                    g.drawImage(
                            rebelImage,
                            area.getInfantryPlacement()[0] - rebelImage.getWidth() / 2 + x,
                            area.getInfantryPlacement()[1] - rebelImage.getHeight() / 2,
                            null);
                }
                float scale = 0.25f;
                if (infantry.contains("army")) {
                    scale = 0.35f;
                }
                BufferedImage playerInf = ImageHelper.readScaled(
                        ResourceHelper.getInstance().getPlayerFile(infantry.split("_")[0], "Infantry"), scale);
                g.drawImage(
                        playerInf,
                        area.getInfantryPlacement()[0] - rebelImage.getWidth() + x,
                        area.getInfantryPlacement()[1] - rebelImage.getWidth(),
                        null);
                x += 40;
            }
        }
        g.dispose();

        FileUpload fileUpload = FileUploadService.createFileUpload(mapImage, "fullmap1444");
        return fileUpload;
    }

    public static FileUpload drawSpecificLocation(String location) {
        BufferedImage ogImage = ImageHelper.read(ResourceHelper.getInstance().getMapFile("fullmap1444"));
        int radius = 400;
        BufferedImage mapImage = new BufferedImage(radius * 2, radius * 2, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = mapImage.createGraphics();
        int px = 0;
        int py = 0;
        if (Mapper.getProvinces().containsKey(location)) {
            ProvinceModel model = Mapper.getProvinces().get(location);
            px = model.getX();
            py = model.getY();
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
        return FileUploadService.createFileUpload(getIdeasImage(game), "ideas");
    }

    public static BufferedImage getIdeasImage(Game game) {

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

        return finalImage;
    }
}
