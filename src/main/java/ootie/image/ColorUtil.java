package ootie.image;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Rectangle;
import lombok.experimental.UtilityClass;

@UtilityClass
class ColorUtil {

    public static final Color EliminatedColor = new Color(150, 0, 24); // Carmine
    public static final Color ActiveColor = new Color(80, 200, 120); // Emerald
    public static final Color PassedColor = new Color(220, 20, 60); // Crimson
    public static final Color Stage1RevealedColor = new Color(230, 126, 34);
    public static final Color LawColor = new Color(228, 255, 0);
    public static final Color TradeGoodColor = new Color(241, 176, 0);

    public static final Color PropulsionTech = Color.decode("#509dce");
    public static final Color CyberneticTech = Color.decode("#e2da6a");
    public static final Color BioticTech = Color.decode("#7cba6b");
    public static final Color WarfareTech = Color.decode("#dc6569");

    public Paint gradient(Color main, Color accent, Rectangle rect) {
        if (accent == null) return main;
        if (main == null) return accent;
        Point p1 = new Point(rect.getLocation());
        Point p2 = new Point(rect.getLocation());
        p2.translate(rect.width, rect.height);
        return new GradientPaint(p1, main, p2, accent);
    }
}
