package ootie.model;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
public class AreaModel implements ModelInterface {

    private String id;
    private List<String> borders = new ArrayList<>();
    private List<String> mountainBorders = new ArrayList<>();
    private List<String> provinces = new ArrayList<>();
    private String religion;
    private String type;
    private int[] religiousCenter;

    @Override
    public boolean isValid() {
        return id != null;
    }

    @Override
    public String getAlias() {
        return id;
    }

}
