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
import ootie.image.DrawingUtil;
import ootie.image.ImageHelper;
import ootie.image.Mapper;
import ootie.model.IdeaModel;
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
