package ootie.image;

import static org.apache.commons.lang3.StringUtils.*;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.entities.User;
import ootie.discord.JdaService;
import ootie.image.MapGenerator.HorizontalAlign;
import ootie.image.MapGenerator.VerticalAlign;
import ootie.logging.BotLogger;

@UtilityClass
public class DrawingUtil {

    private static final Pattern PATTERN = Pattern.compile("[\\n\n]");

    private static BasicStroke stroke(int size) {
        return new BasicStroke(size);
    }

    private static final int DELTA_Y = 26;
    private static final double NEGATIVE_NINETY_DEGREES_RADIANS = -1.570_796_326_794_896_6;
    public static final int DISCORD_AVATAR_SIZE = 32;

    public static void superDrawString(
            Graphics g,
            String txt,
            int x,
            int y,
            Color textColor,
            MapGenerator.HorizontalAlign h,
            MapGenerator.VerticalAlign v,
            Stroke outlineSize,
            Color outlineColor) {
        superDrawString((Graphics2D) g, txt, x, y, textColor, h, v, outlineSize, outlineColor);
    }

    /**
     *
     * @param g graphics object
     * @param txt string to print
     * @param x x-position of the string (Left side, unless horizontalAlignment is set)
     * @param y y-position of the string (Bottom side, unless verticalAlignment is set)
     * @param textColor
     * @param horizontalAlignment location of the provided x relative to the (default = Left)
     * @param verticalAlignment location of the provided y relative to the text (default = Bottom)
     * @param outlineSize use global variable "strokeX" where X = outline size e.g. stroke1 for 1px outline
     * @param outlineColor
     */
    public static void superDrawString(
            Graphics2D g,
            String txt,
            int x,
            int y,
            Color textColor,
            MapGenerator.HorizontalAlign horizontalAlignment,
            MapGenerator.VerticalAlign verticalAlignment,
            Stroke outlineSize,
            Color outlineColor) {
        if (txt == null) return;

        int width = g.getFontMetrics().stringWidth(txt);
        if (horizontalAlignment != null) {
            switch (horizontalAlignment) {
                case Center -> x -= (int) (width / 2.0);
                case Right -> x -= width;
                case Left -> {}
            }
        }

        int height = g.getFontMetrics().getAscent() - g.getFontMetrics().getDescent();
        if (verticalAlignment != null) {
            switch (verticalAlignment) {
                case Center -> y += height / 2;
                case Top -> y += height;
                case Bottom -> {}
            }
        }

        if (outlineSize == null) outlineSize = stroke(2);
        if (outlineColor == null && textColor == null) {
            outlineColor = Color.BLACK;
            textColor = Color.WHITE;
        }
        if (outlineColor == null) {
            g.drawString(txt, x, y);
        } else {
            drawStringOutlined(g, txt, x, y, outlineSize, outlineColor, textColor);
        }
    }

    private static void drawStringOutlined(
            Graphics2D g2, String text, int x, int y, Stroke outlineStroke, Color outlineColor, Color fillColor) {
        if (text == null) return;
        Color origColor = g2.getColor();
        AffineTransform originalTileTransform = g2.getTransform();
        Stroke origStroke = g2.getStroke();
        RenderingHints origHints = g2.getRenderingHints();

        GlyphVector gv = g2.getFont().createGlyphVector(g2.getFontRenderContext(), text);
        Shape textShape = gv.getOutline();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.translate(x, y);
        g2.setColor(outlineColor);
        g2.setStroke(outlineStroke);
        g2.draw(textShape);

        g2.setColor(fillColor);
        g2.fill(textShape);

        g2.setColor(origColor);
        g2.setStroke(origStroke);
        g2.setTransform(originalTileTransform);
        g2.setRenderingHints(origHints);
    }

    public static void superDrawStringCenteredDefault(Graphics2D g2, String txt, int x, int y) {
        superDrawString(
                g2, txt, x, y, Color.white, HorizontalAlign.Center, VerticalAlign.Center, stroke(2), Color.black);
    }

    public static void superDrawStringCenteredDefault(Graphics graphics, String txt, int x, int y) {
        superDrawString(
                (Graphics2D) graphics,
                txt,
                x,
                y,
                Color.white,
                HorizontalAlign.Center,
                VerticalAlign.Center,
                stroke(2),
                Color.black);
    }

    public static void superDrawStringCentered(
            Graphics g, String txt, int x, int y, Color textColor, Stroke outlineSize, Color outlineColor) {
        superDrawString(
                (Graphics2D) g,
                txt,
                x,
                y,
                textColor,
                HorizontalAlign.Center,
                VerticalAlign.Center,
                outlineSize,
                outlineColor);
    }

    public static void superDrawStringCentered(
            Graphics2D g2, String txt, int x, int y, Color textColor, Stroke outlineSize, Color outlineColor) {
        superDrawString(
                g2, txt, x, y, textColor, HorizontalAlign.Center, VerticalAlign.Center, outlineSize, outlineColor);
    }

    public static void drawRedX(Graphics2D g2, int x, int y, int size, boolean thick) {
        int offset = size / 2;
        int strokeSize = thick ? 6 : 4;
        RenderingHints origHints = g2.getRenderingHints();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // draw outline
        g2.setStroke(stroke(strokeSize));
        g2.setColor(Color.black);
        g2.drawLine(x - offset, y - offset, x + offset, y + offset);
        g2.drawLine(x - offset, y + offset, x + offset, y - offset);

        // draw overline
        g2.setStroke(stroke(strokeSize - 2));
        g2.setColor(Color.red);
        g2.drawLine(x - offset, y - offset, x + offset, y + offset);
        g2.drawLine(x - offset, y + offset, x + offset, y - offset);

        g2.setRenderingHints(origHints);
    }

    @Nullable
    public static Image getUserDiscordAvatar(String userID) {
        try {
            User user = JdaService.jda.getUserById(userID);
            if (user == null) return null;
            return ImageHelper.readURLScaled(
                    user.getEffectiveAvatar().getUrl(), DISCORD_AVATAR_SIZE, DISCORD_AVATAR_SIZE);
        } catch (Exception e) {
            // BotLogger.error("Could not get Avatar", e);
        }
        return null;
    }

    public static void drawCenteredString(Graphics g, String text, Rectangle rect, Font font) {
        int x = (int) Math.round(rect.getCenterX());
        int y = (int) Math.round(rect.getCenterY());
        drawCenteredString(g, text, x, y, font);
    }

    private static void drawCenteredString(Graphics g, String text, int x, int y, Font font) {
        g.setFont(font);
        superDrawString(g, text, x, y, null, HorizontalAlign.Center, VerticalAlign.Center, null, null);
    }

    public BufferedImage tintedBackground(Color color, float alpha) {
        BufferedImage bgImg = new BufferedImage(345, 299, BufferedImage.TYPE_INT_ARGB);
        Polygon p = new Polygon();
        for (int i = 0; i < 6; i++) {
            int theta = i * 60;
            int x = Math.clamp(Math.round(172.0 * Math.cos(Math.toRadians(theta)) + 172.5), 0, 345);
            int y = Math.clamp(Math.round(172.0 * Math.sin(Math.toRadians(theta)) + 149.5), 0, 299);
            p.addPoint(x, y);
        }
        Graphics2D g2 = bgImg.createGraphics();
        g2.setColor(color);
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        g2.fillPolygon(p);
        return bgImg;
    }

    private static int width(Graphics2D g, String str) {
        int w = 0;
        if (str != null && !str.isBlank()) w = g.getFontMetrics().stringWidth(str);
        return w;
    }

    public static List<String> layoutText(Graphics2D g2, String inputText, int maxWidth) {
        List<String> initialSplit = new ArrayList<>(Arrays.asList(PATTERN.split(inputText)));
        List<String> finalSplit = new ArrayList<>();
        try {
            for (String line : initialSplit) {
                line = line.trim();
                while (width(g2, line) > maxWidth) {
                    int splitIndex = -1;
                    int nextSpace = line.indexOf(' ');

                    // Prefer splitting at spaces
                    while (nextSpace != -1 && width(g2, line.substring(0, nextSpace)) < maxWidth) {
                        splitIndex = nextSpace;
                        nextSpace = line.indexOf(' ', splitIndex + 1);
                    }

                    // If no space is found or no valid split, break at max width
                    if (splitIndex == -1) {
                        for (int i = 1; i < line.length(); i++) {
                            if (width(g2, line.substring(0, i)) > maxWidth) {
                                splitIndex = i - 1;
                                break;
                            }
                        }
                    }

                    finalSplit.add(line.substring(0, splitIndex).trim());
                    line = line.substring(splitIndex).trim();
                }

                if (!line.isEmpty()) {
                    finalSplit.add(line);
                }
            }
        } catch (Exception e) {
            BotLogger.warning("Error laying out text with width `" + maxWidth + "`:\n```" + inputText + "\n```", e);
            return initialSplit;
        }
        return finalSplit;
    }

    public static void drawRectWithTwoColorGradient(
            Graphics2D g2, Color mainColor, Color accentColor, int x, int y, int width, int height) {
        Rectangle rect = new Rectangle(x, y, width, height);
        drawRectWithTwoColorGradient(g2, mainColor, accentColor, rect);
    }

    public static void drawRectWithTwoColorGradient(Graphics2D g2, Color mainColor, Color accentColor, Rectangle rect) {
        Paint gradient = ColorUtil.gradient(mainColor, accentColor, rect);
        Paint old = g2.getPaint();
        g2.setPaint(gradient);
        g2.draw(rect);
        g2.setPaint(old);
    }

    /**
     * @param graphics
     * @param text text to draw vertically
     * @param x left
     * @param y bottom
     * @param font
     */
    public static void drawTextVertically(Graphics graphics, String text, int x, int y, Font font) {
        drawTextVertically(graphics, text, x, y, font, false);
    }

    public static void drawTextVertically(Graphics graphics, String text, int x, int y, Font font, boolean rightAlign) {
        Graphics2D graphics2D = (Graphics2D) graphics;
        AffineTransform originalTransform = graphics2D.getTransform();
        graphics2D.rotate(NEGATIVE_NINETY_DEGREES_RADIANS);
        graphics2D.setFont(font);

        if (rightAlign) {
            y += graphics.getFontMetrics().stringWidth(text);
        }

        // DRAW A 1px BLACK BORDER AROUND TEXT
        Color originalColor = graphics2D.getColor();
        graphics2D.setColor(Color.BLACK);
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                graphics2D.drawString(
                        text,
                        (y + j) * -1, // See
                        // https://www.codejava.net/java-se/graphics/how-to-draw-text-vertically-with-graphics2d
                        x + graphics2D.getFontMetrics().getHeight() / 2 + i);
            }
        }
        graphics2D.setColor(originalColor);

        graphics2D.drawString(
                text,
                (y) * -1, // See https://www.codejava.net/java-se/graphics/how-to-draw-text-vertically-with-graphics2d
                x + graphics2D.getFontMetrics().getHeight() / 2);
        graphics2D.setTransform(originalTransform);
    }

    public static void drawTwoLinesOfTextVertically(Graphics graphics, String text, int x, int y, int maxWidth) {
        drawTwoLinesOfTextVertically(graphics, text, x, y, maxWidth, false);
    }

    private static void drawTwoLinesOfTextVertically(
            Graphics graphics, String text, int x, int y, int maxWidth, boolean rightAlign) {
        int spacing = graphics.getFontMetrics().getAscent()
                + graphics.getFontMetrics().getLeading();
        text = text.toUpperCase();
        String firstRow = substringBefore(text, "\n");
        firstRow = trimTextToPixelWidth(graphics, firstRow, maxWidth);
        String secondRow = text.substring(firstRow.length()).replace("\n", "");
        secondRow = trimTextToPixelWidth(graphics, secondRow, maxWidth);
        drawTextVertically(graphics, firstRow, x, y, graphics.getFont(), rightAlign);
        if (isNotBlank(secondRow)) {
            drawTextVertically(graphics, secondRow, x + spacing, y, graphics.getFont(), rightAlign);
        }
    }

    public static void drawOneOrTwoLinesOfTextVertically(Graphics graphics, String text, int x, int y, int maxWidth) {
        drawOneOrTwoLinesOfTextVertically(graphics, text, x, y, maxWidth, false);
    }

    public static void drawOneOrTwoLinesOfTextVertically(
            Graphics graphics, String text, int x, int y, int maxWidth, boolean rightAlign) {
        // vertically prints text on one line, centred horizontally, if it fits,
        // otherwise prints it over two lines

        // if the text contains a linebreak, print it over two lines
        if (text.contains("\n")) {
            drawTwoLinesOfTextVertically(graphics, text, x, y, maxWidth, rightAlign);
            return;
        }

        int spacing = graphics.getFontMetrics().getAscent()
                + graphics.getFontMetrics().getLeading();
        text = text.toUpperCase();

        // if the text is short enough to fit on one line, print it on one
        if (text.equals(trimTextToPixelWidth(graphics, text, maxWidth))) {
            drawTextVertically(graphics, text, x + spacing / 2, y, graphics.getFont(), rightAlign);
            return;
        }

        // if there's a space in the text, try to split it
        // as close to the centre as possible
        if (text.contains(" ")) {
            float center = text.length() / 2.0f + 0.5f;
            String front = text.substring(0, (int) center);
            // String back = text.substring((int) (center - 0.5f));
            int before = front.lastIndexOf(' ');
            int after = text.indexOf(' ', (int) (center - 0.5f));

            // if there's only a space in the back half, replace the first space with a newline
            if (before == -1) {
                text = text.substring(0, after) + "\n" + text.substring(after + 1);
            }
            // if there's only a space in the front half, or if the last space in the
            // front half is closer to the centre than the first space in the back half,
            // replace the last space in the front half with a newline
            else if (after == -1 || (center - before - 1 <= after - center + 1)) {
                text = text.substring(0, before) + "\n" + text.substring(before + 1);
            }
            // otherwise, the first space in the back half is closer to the centre
            // than the last space in the front half, so replace
            // the first space in the back half with a newline
            else {
                text = text.substring(0, after) + "\n" + text.substring(after + 1);
            }
        }
        drawTwoLinesOfTextVertically(graphics, text, x, y, maxWidth, rightAlign);
    }

    public static void drawDebtBoxText(Graphics graphics, String text, int x, int y, int maxWidth) {
        int spacing = graphics.getFontMetrics().getAscent()
                + graphics.getFontMetrics().getLeading();
        text = text.toUpperCase();

        for (int i = 0; i < 9; i++) {
            String trimmedText = trimTextToPixelWidth(graphics, text, maxWidth);
            if (text.equals(trimmedText)) {
                drawTextVertically(graphics, text, x + spacing / 2, y, graphics.getFont());
                return;
            }

            int spaceIndex = trimmedText.lastIndexOf(' ');
            if (spaceIndex == -1) {
                drawTextVertically(graphics, trimmedText, x + spacing / 2, y, graphics.getFont());
                text = text.substring(trimmedText.length());
            } else {
                drawTextVertically(graphics, text.substring(0, spaceIndex), x + spacing / 2, y, graphics.getFont());
                text = text.substring(spaceIndex + 1);
            }
            x += spacing;
        }
    }

    private static String trimTextToPixelWidth(Graphics graphics, String text, int pixelLength) {
        for (int i = 0; i < text.length(); i++) {
            if (graphics.getFontMetrics().stringWidth(text.substring(0, i + 1)) > pixelLength) {
                return text.substring(0, i);
            }
        }
        return text;
    }
}
