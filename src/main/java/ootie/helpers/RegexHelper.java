package ootie.helpers;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.experimental.UtilityClass;
import ootie.game.Game;
import ootie.image.PositionMapper;

@UtilityClass
public class RegexHelper {
    /**
     * Matches the boundary (0-width position) between a digit & a non-digit character in either direction.
     * For example, "123abc456def".split(DIGIT_BOUNDARY) == ["123", "abc", "456", "def"]
     */
    public static final String DIGIT_BOUNDARY = "(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)";

    private static String regexBuilder(String groupname, Collection<String> options) {
        return "(?<" + groupname + ">(" + String.join("|", options) + "))";
    }

    private static String regexBuilder(String groupname, String pattern) {
        return "(?<" + groupname + ">" + pattern + ")";
    }

    private static Set<String> legalColors(Game game) {
        Set<String> colors = new HashSet<>();
        return colors;
    }

    public static String optional(String regex) {
        return "(" + regex + ")?";
    }

    public static String oneOf(List<String> regex) {
        return "(" + "(" + String.join(")|(", regex) + ")" + ")";
    }

    /**
     * @param game if provided, only match colors present in this game
     * @return group "color" matching any color in the bot
     */
    public static String colorRegex(Game game) {
        Set<String> colorNames = legalColors(game);
        return regexBuilder("color", colorNames);
    }

    /**
     * @param game if provided, only match colors present in this game
     * @return group "color" matching any color in the bot
     */
    public static String colorRegex(Game game, String group) {
        Set<String> colorNames = legalColors(game);
        return regexBuilder(group, colorNames);
    }

    /** @return group matching any legal tile position in the bot */
    public static String posRegex(String group) {
        return regexBuilder(group, PositionMapper.getTilePositions());
    }

    /** @return group "pos" matching any legal tile position in the bot */
    public static String posRegex() {
        return posRegex("pos");
    }

    /** @return group "ring" matching "ring<#>" or "ringcorners", and returns "<#>" or "corners" respectively */
    public static String ringRegex() {
        return ringRegex("ring");
    }

    private static String ringRegex(String group) {
        return "ring" + regexBuilder(group, List.of("[\\+\\-]?[0-9]+", "corners"));
    }
}
