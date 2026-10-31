package ootie.discord.commands.search;

import java.awt.image.BufferedImage;
import java.io.File;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import ootie.ResourceHelper;
import ootie.image.ImageHelper;
import ootie.message.MessageHelper;
import ootie.service.image.FileUploadService;

final class SearchImageHelper {

    private SearchImageHelper() {}

    static void sendImage(SlashCommandInteractionEvent event, String folder, String imageName, String description) {
        String normalizedFolder = folder.replace("/", File.separator);
        if (!normalizedFolder.endsWith(File.separator)) {
            normalizedFolder += File.separator;
        }
        String imagePath = ResourceHelper.getResourceFromFolder(normalizedFolder, imageName + ".png");
        if (imagePath == null) {
            MessageHelper.sendMessageToChannel(event.getChannel(), "Could not find image for " + description);
            return;
        }

        BufferedImage image = ImageHelper.read(imagePath);
        var upload = FileUploadService.createFileUpload(image, imageName).setDescription(description);
        MessageHelper.sendFileUploadToChannel(event.getChannel(), upload);
    }
}
