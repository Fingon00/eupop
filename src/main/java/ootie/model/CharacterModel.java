package ootie.model;

import java.awt.Color;
import java.awt.image.BufferedImage;

import lombok.Data;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.utils.FileUpload;
import ootie.ResourceHelper;
import ootie.image.ImageHelper;
import ootie.message.MessageHelper;
import ootie.model.Source.ComponentSource;
import ootie.service.image.FileUploadService;

@Data
public class CharacterModel implements ModelInterface, EmbeddableModel {

    private String id;
    private String name;
    private int diploPower;
    private int adminPower;
    private int militaryPower;
    private int cavDice;
    private int infantryDice;
    private int artilleryDice;
    private boolean advisor;
    private boolean wounded;
    private String symbol;
    private ComponentSource source = ComponentSource.base;
    private int cost;

    @Override
    public boolean isValid() {
        return id != null && name != null && source != null;
    }

    @Override
    public String getAlias() {
        return id;
    }


    public MessageEmbed getRepresentationEmbed() {
        return getRepresentationEmbed(false);
    }

    public MessageEmbed getRepresentationEmbed(boolean includeID) {
        EmbedBuilder eb = new EmbedBuilder();

        // TITLE
        String title = name;
        eb.setTitle(title);

        String description = "";
        // DESCRIPTION
        // if (getPermanentEffect().isPresent()) {
        //     description += getPermanentEffect().get();
        // }

        // // FIELDS
        // if (getWindow().isPresent()) {
        //     if (!description.isEmpty()) description += "\n\n";
        //     description += getWindow().get() + "\n" + getWindowEffect().orElse("");
        // }

        // if (notes != null) {
        //     description += "\n-# [" + notes + "]";
        // }

        eb.setDescription(description);

        // FOOTER
        StringBuilder footer = new StringBuilder();
        if (includeID) footer.append("ID: ").append(id).append("    Source: ").append(source);
        eb.setFooter(footer.toString());

        eb.setColor(Color.black);
        eb.setImage(description);
        return eb.build();
    }

    public String getNameRepresentation() {
        String eventName = name;

        StringBuilder sb = new StringBuilder();
        sb.append("").append(eventName).append(" (" + id + ")");

        return sb.toString();
    }

    public String getRepresentation() {
        String eventName = name;

        StringBuilder sb = new StringBuilder();
        sb.append("").append(eventName).append(" (" + id + ")");

        return sb.toString();
    }

    public void drawCharacterImage(MessageChannel messageChannel) {
        BufferedImage eventImage = ImageHelper.read(ResourceHelper.getInstance().getEventFile(id));

        FileUpload fileUpload =
                FileUploadService.createFileUpload(eventImage, id).setDescription(name);
        MessageHelper.sendFileUploadToChannel(messageChannel, fileUpload);
    }

    @Override
    public boolean search(String searchString) {
        return id.contains(searchString)
                || name.toLowerCase().contains(searchString.toLowerCase());
    }

    @Override
    public String getAutoCompleteName() {
        return name + " (" + id + ")";
    }
}
