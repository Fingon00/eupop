package ootie.model;

import java.awt.Color;
import java.util.List;
import lombok.Data;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import ootie.model.Source.ComponentSource;

@Data
public class ScenarioModel implements ModelInterface, EmbeddableModel {

    private String id;
    private ComponentSource source;
    private String name;
    private String firstplayer;
    private int numberOfRounds;
    private List<String> realms;
    private List<String> eventsA1H1;
    private int startingAge;

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
        String title = name;
        eb.setTitle(title);

        String description = "";

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

    @Override
    public boolean search(String searchString) {
        return id.contains(searchString)
                || name.toLowerCase().contains(searchString.toLowerCase())
                || getAutoCompleteName().toLowerCase().contains(searchString.toLowerCase());
    }

    @Override
    public String getAutoCompleteName() {
        String auto = name + " (" + id + ")";
        return auto;
    }
}
