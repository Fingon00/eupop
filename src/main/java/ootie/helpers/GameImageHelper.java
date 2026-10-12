package ootie.helpers;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.List;
import net.dv8tion.jda.api.utils.FileUpload;
import ootie.ResourceHelper;
import ootie.game.Game;
import ootie.image.ImageHelper;
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
}
