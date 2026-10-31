package ootie.model;

import java.awt.Color;
import lombok.Data;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import ootie.model.Source.ComponentSource;

@Data
public class MilestoneModel implements ModelInterface, EmbeddableModel {

    private String id;
    private String text;
    private ComponentSource source;
    private String type;
    private int age;

    @Override
    public boolean isValid() {
        return id != null && source != null;
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
        String title = id;
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
        String eventName = id;

        StringBuilder sb = new StringBuilder();
        sb.append("").append(eventName).append(" (" + id + ")");

        return sb.toString();
    }

    public String getRepresentation() {
        String eventName = id;

        StringBuilder sb = new StringBuilder();
        sb.append("").append(eventName).append(" (" + id + ")");

        return sb.toString();
    }

    @Override
    public boolean search(String searchString) {
        return id.contains(searchString)
                || text.toLowerCase().contains(searchString.toLowerCase())
                || getAutoCompleteName().toLowerCase().contains(searchString.toLowerCase());
    }

    @Override
    public String getAutoCompleteName() {
        String auto = id + " (" + type + ")";
        return auto;
    }
}
