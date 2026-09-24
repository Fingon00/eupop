package ootie.discord.commands;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.List;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.utils.FileUpload;
import ootie.ResourceHelper;
import ootie.helpers.Constants;
import ootie.image.DrawingUtil;
import ootie.image.ImageHelper;
import ootie.image.Mapper;
import ootie.message.MessageHelper;
import ootie.model.ProvinceModel;
import ootie.service.image.FileUploadService;

public class ShowGameCommand extends GameStateCommand {

    public ShowGameCommand() {
        super(false, false);
    }

    @Override
    public String getName() {
        return Constants.SHOW_GAME;
    }

    @Override
    public String getDescription() {
        return "Show selected map";
    }

    @Override
    public List<OptionData> getOptions() {
        return List.of(
                new OptionData(OptionType.STRING, Constants.GAME_NAME, "Map name to be shown").setAutoComplete(true));
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {

        BufferedImage mapImage = ImageHelper.read(ResourceHelper.getInstance().getMapFile("fullmap1444"));
        Graphics2D g = mapImage.createGraphics();
        g.setStroke(DrawingUtil.stroke(20));
        for (ProvinceModel model : Mapper.getProvinces().values()) {
            String province = model.getAlias();
            double x = model.getX();
            double y = model.getY();
            int w = 6803;
            int h = 4104;
            double minx = -21.45; // -18.45 //-20.95 //-21
            double miny = -12.74; // -12.14
            double maxx = 21.53; // 20.81 //21
            double maxy = 13.35; // 11.65
            double nx = (x - minx) / (maxx - minx);
            double ny = (y - miny) / (maxy - miny);
            int px = (int) (nx * w);
            int py = (int) ((1 - ny) * h);
            if ("paris".equalsIgnoreCase(province) || "maine".equalsIgnoreCase(province)) {
                System.out.println(province + " " + nx + " " + ny + " " + px + " " + py + " " + x + " " + y);
            }
            DrawingUtil.superDrawStringCentered(g, province, px, py, Color.BLACK, DrawingUtil.stroke(6), Color.WHITE);
        }

        FileUpload fileUpload = FileUploadService.createFileUpload(mapImage, "fullmap1444");
        MessageHelper.sendFileUploadToChannel(event.getChannel(), fileUpload);
    }
}
