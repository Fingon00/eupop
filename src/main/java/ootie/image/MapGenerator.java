package ootie.image;

import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.utils.FileUpload;
import ootie.game.Game;
import ootie.game.Player;
import ootie.helpers.DateTimeHelper;
import ootie.helpers.DisplayType;
import ootie.message.MessageHelper;
import ootie.service.image.FileUploadService;
import ootie.settings.GlobalSettings;
import org.apache.commons.lang3.time.StopWatch;

public class MapGenerator implements AutoCloseable {

    private static final int RING_MAX_COUNT = 8;

    private static final int RING_MIN_COUNT = 3;
    private static final int PLAYER_STATS_HEIGHT = 650; // + 34 per teammate + 34 if line is long
    private static final int TILE_PADDING = 100;
    private static final int EXTRA_X = 300; // padding at left/right of map
    private static final int EXTRA_Y = 200; // padding at top/bottom of map
    private static final int SPACING_BETWEEN_OBJECTIVE_TYPES = 10;
    private static final int SPACE_FOR_TILE_HEIGHT = 300; // space to calculate tile image height with
    private static final int TILE_WIDTH = 345; // typical width of a tile image
    private static final int MINIMUM_WIDTH_OF_PLAYER_AREA = 1000;
    private static final BasicStroke stroke2 = new BasicStroke(2.0f);
    private static final BasicStroke stroke3 = new BasicStroke(3.0f);
    private static final BasicStroke stroke4 = new BasicStroke(4.0f);
    private static final BasicStroke stroke5 = new BasicStroke(5.0f);
    private static final BasicStroke stroke6 = new BasicStroke(6.0f);
    private static final int WEBP_MAX_DIMENSION = 16_383;
    private static final ColorConvertOp GRAYSCALE_CONVERT_OP =
            new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_GRAY), null);

    private final Graphics graphics;
    private final BufferedImage mainImage;
    private byte[] mainImageBytes;
    private String imageFormat = "webp";
    private final GenericInteractionCreateEvent event;
    private final Game game;
    private final DisplayType displayType;
    private final DisplayType displayTypeBasic;
    private final boolean debug;
    private final int width;
    private final int height;
    private final int heightForGameInfo;

    private final int mapWidth;
    private int minX = -1;
    private int minY = -1;
    private int maxX = -1;
    private int maxY = -1;
    private int fractureYbump;
    private boolean isFoWPrivate;
    private Player fowPlayer;

    public enum HorizontalAlign {
        Left,
        Center,
        Right
    }

    public enum VerticalAlign {
        Top,
        Center,
        Bottom
    }

    // Map to aggregate unit coordinates by faction from all tiles with global coordinates
    private final Map<String, Map<String, List<Point>>> globalUnitCoordinatesByFaction = new HashMap<>();

    private StopWatch debugAbsoluteStartTime;
    private StopWatch debugTileTime;
    private StopWatch debugImageGraphicsTime;
    private StopWatch debugDrawTime;
    private StopWatch debugDiscordTime;
    private StopWatch debugWebsiteTime;

    MapGenerator(Game game, @Nullable DisplayType displayType, @Nullable GenericInteractionCreateEvent event) {
        debug = GlobalSettings.getSetting(
                GlobalSettings.ImplementedSettings.DEBUG.toString(), Boolean.class, Boolean.FALSE);
        if (debug) debugAbsoluteStartTime = StopWatch.createStarted();

        this.game = game;
        this.displayType = defaultIfNull(displayType);
        this.event = event;

        // Height of objectives section (=0 when there is 5 or less objectives in the column with most objectives)
        Set<String> revealedObjectives = game.getRevealedPublicObjectives().keySet();
        int stage1PublicObjCount = 0;
        int stage2PublicObjCount = 0;
        int otherObjCount = revealedObjectives.size() - stage1PublicObjCount - stage2PublicObjCount;
        stage1PublicObjCount += game.getPublicObjectives1Peekable().size();
        stage2PublicObjCount += game.getPublicObjectives2Peekable().size();
        int mostObjectivesInAColumn = Math.max(Math.max(stage1PublicObjCount, stage2PublicObjCount), otherObjCount);
        int heightOfObjectivesSection = Math.max((mostObjectivesInAColumn - 5) * 43, 0);

        // Height of sections of players stats and agendas/events in play and objectives

        int heightOfPlayerAreasSection = getHeightOfPlayerAreasSection(game, 6, heightOfObjectivesSection);

        // Width of map section
        mapWidth = Math.max(MINIMUM_WIDTH_OF_PLAYER_AREA, 600);

        // Other things
        switch (this.displayType) {
            case stats:
                heightForGameInfo = 40;
                height = heightOfPlayerAreasSection;
                displayTypeBasic = DisplayType.stats;
                width = mapWidth;
                break;
            case map:
            case wormholes:
            case anomalies:
            case legendaries:
            case empties:
            case aetherstream:
            case spacecannon:
            case traits:
            case techskips:
            case attachments:
            case shipless:
            case googly:
            default:
                heightForGameInfo = 500;
                height = 5000 + heightOfPlayerAreasSection;
                displayTypeBasic = DisplayType.all;
                width = mapWidth;
        }

        // Create image
        mainImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        graphics = mainImage.getGraphics();
    }

    @Override
    public void close() {
        mainImage.flush();
        graphics.dispose();
        logDebug();
    }

    /**
     * Returns the height for the sections Objectives (above 5) + Laws + Events + Players + idk what EXTRA_Y is for
     */
    private static int getHeightOfPlayerAreasSection(Game game, int playerCountForMap, int objectivesY) {
        final int typicalPlayerAreaHeight = 340;
        int playersY = playerCountForMap * typicalPlayerAreaHeight;
        for (Player player : game.getPlayers().values()) {
            if ("neutral".equalsIgnoreCase(player.getFaction()) || (player.isNpc() && player.isDummy())) {
                playersY -= 350;
            }
            if (player.isEliminated()) {
                playersY -= 190;
            } else if (player.getSecretsScored().size() >= 4) {
                playersY += (player.getSecretsScored().size() - 4) * 43 + 23;
            }
            playersY += (player.getTeamMateIDs().size() - 1) * 35;
        }
        final int columnsOfLaws = 2;
        final int lawHeight = 115;
        int lawsY = (game.getLaws().size() / columnsOfLaws + 1) * lawHeight;
        lawsY += (game.getEventsInEffect().size() / columnsOfLaws + 1) * lawHeight;
        return playersY + lawsY + objectivesY + EXTRA_Y * 3;
    }

    private DisplayType defaultIfNull(DisplayType displayType) {
        if (game.getDisplayTypeForced() != null) {
            return game.getDisplayTypeForced();
        }
        if (displayType == null) {
            return DisplayType.all;
        }
        return displayType;
    }

    FileUpload createFileUpload() {
        if (debug) debugDiscordTime = StopWatch.createStarted();
        FileUpload fileUpload = FileUploadService.createFileUpload(mainImageBytes, game.getName(), imageFormat);
        if (debug) debugDiscordTime.stop();
        if (debug) FileUploadService.saveLocalPng(mainImage, "MapDebug");
        return fileUpload;
    }

    void draw() {
        if (debug) debugDrawTime = StopWatch.createStarted();
        drawGame();
        if (debug) debugDrawTime.stop();
    }

    private void logDebug() {
        if (!debug) return;
        debugAbsoluteStartTime.stop();

        StringBuilder sb = new StringBuilder();

        String totalTimeStr = DateTimeHelper.getTimeRepresentationNanoSeconds(debugAbsoluteStartTime.getNanoTime());
        String totalLine = String.format("%-34s%s", "Total time (" + game.getName() + "):", totalTimeStr);
        sb.append(totalLine);

        sb.append(debugString("  Draw time:", 36, debugDrawTime, debugAbsoluteStartTime));
        sb.append(debugString("    Tile time (of Draw Time):", 38, debugTileTime, debugDrawTime));
        sb.append(debugString("    Graphics time (of Draw Time):", 38, debugImageGraphicsTime, debugDrawTime));
        sb.append(debugString("  Discord time:", 36, debugDiscordTime, debugAbsoluteStartTime));
        sb.append(debugString("  Website time:", 36, debugWebsiteTime, debugAbsoluteStartTime));
        sb.append('\n');

        String message = "```\nDEBUG - GenerateMap Timing:\n" + sb + "\n```";
        MessageHelper.sendMessageToEventServerBotLogChannel(event, message);
    }

    private static String debugString(String name, int padRight, StopWatch subStopWatch, StopWatch totalStopWatch) {
        if (subStopWatch == null || totalStopWatch == null) {
            return "";
        }
        long subTime = subStopWatch.getNanoTime();
        long totalTime = totalStopWatch.getNanoTime();
        double percentage = ((double) subTime / totalTime) * 100.0;
        String timeStr = DateTimeHelper.getTimeRepresentationNanoSeconds(subTime);
        return String.format("\n%-" + padRight + "s%s (%2.2f%%)", name, timeStr, percentage);
    }

    private void drawGame() {

        if (mainImage.getWidth() > WEBP_MAX_DIMENSION || mainImage.getHeight() > WEBP_MAX_DIMENSION) {
            mainImageBytes = ImageHelper.writeJpg(mainImage);
            imageFormat = "jpg";
        } else {
            mainImageBytes = ImageHelper.writeWebp(mainImage);
            imageFormat = "webp";
        }
        if (debug) debugImageGraphicsTime.stop();
    }

    String getGameName() {
        return game.getName();
    }

    /**
     * Get the aggregated global unit coordinates by faction from all tiles
     * @return Map where key is faction/player identifier, secondary key is unit ID, and value is list of global coordinates
     */
    public Map<String, Map<String, List<Point>>> getGlobalUnitCoordinatesByFaction() {
        return new HashMap<>(globalUnitCoordinatesByFaction);
    }

    /**
     * Helper method to aggregate unit coordinates from a TileGenerator with global translation
     * @param tileGenerator The TileGenerator to get coordinates from
     * @param tileX The global X offset for this tile
     * @param tileY The global Y offset for this tile
     */
}
