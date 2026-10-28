package ootie.model;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import ootie.model.Source.ComponentSource;

@Data
public class AreaModel implements ModelInterface, EmbeddableModel {
    private ComponentSource source = ComponentSource.base;
    private String id;
    private List<String> borders = new ArrayList<>();
    private List<String> mountainBorders = new ArrayList<>();
    private List<String> provinces = new ArrayList<>();
    private String religion;
    private String type;
    private int[] religiousCenter;
    private int[] infantryPlacement;

    @Override
    public boolean isValid() {
        return id != null;
    }

    @Override
    public String getAlias() {
        return id;
    }

    @Override
    public MessageEmbed getRepresentationEmbed() {
        return new EmbedBuilder().build();
    }

    @Override
    public boolean search(String searchString) {
        return id != null && id.contains(searchString);
    }

    @Override
    public String getAutoCompleteName() {
        return id;
    }

    @Override
    public String getNameRepresentation() {
        return id;
    }
}
