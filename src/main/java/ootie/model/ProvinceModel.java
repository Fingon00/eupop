package ootie.model;

import lombok.Data;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import ootie.model.Source.ComponentSource;

@Data
public class ProvinceModel implements ModelInterface, EmbeddableModel {

    private String province;
    private int x;
    private int y;
    private String realm1444;
    private String realm1618;
    private boolean capital;
    private ComponentSource source = ComponentSource.base;

    @Override
    public boolean isValid() {
        return province != null;
    }

    @Override
    public String getAlias() {
        return province;
    }

    public MessageEmbed getRepresentationEmbed() {
        return getRepresentationEmbed(false);
    }

    public MessageEmbed getRepresentationEmbed(boolean includeID) {
        EmbedBuilder eb = new EmbedBuilder();
        return eb.build();
    }

    public String getNameRepresentation() {
        String eventName = province;

        StringBuilder sb = new StringBuilder();
        sb.append("").append(eventName);

        return sb.toString();
    }

    public String getRepresentation() {
        String eventName = province;

        StringBuilder sb = new StringBuilder();
        sb.append("").append(eventName);

        return sb.toString();
    }

    @Override
    public boolean search(String searchString) {
        return province.contains(searchString);
    }

    @Override
    public String getAutoCompleteName() {
        return province;
    }
}
