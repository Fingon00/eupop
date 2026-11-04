package ootie.model;

import java.awt.Color;
import lombok.Data;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import ootie.model.Source.ComponentSource;

@Data
public class BotCardModel implements ModelInterface, EmbeddableModel {

    private String id;
    private String action1;
    private String action2;
    private String warAction;
    private ComponentSource source = ComponentSource.base;
    private String actionCard;
    private int botPower;

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
        String title = action1;
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
        String eventName = action1;

        StringBuilder sb = new StringBuilder();
        sb.append("").append(eventName).append(" (" + id + ")");

        return sb.toString();
    }

    public String getRepresentation() {
        String eventName = action1;

        StringBuilder sb = new StringBuilder();
        sb.append("").append(eventName).append(" (" + id + ")");

        return sb.toString();
    }

    @Override
    public boolean search(String searchString) {
        return id.contains(searchString)
                || action1.toLowerCase().contains(searchString.toLowerCase())
                || getAutoCompleteName().toLowerCase().contains(searchString.toLowerCase());
    }

    @Override
    public String getAutoCompleteName() {
        String auto = action1 + " (" + id + ")";
        return auto;
    }
}
