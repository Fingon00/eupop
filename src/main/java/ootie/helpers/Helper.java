package ootie.helpers;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.unions.DefaultGuildChannelUnion;
import ootie.logging.BotLogger;

public final class Helper {
    private static final int TRIPLE_SYSTEM_TOKEN_PLANET_GAP_OFFSET = 65;

    private static final List<Point> TOKEN_PLANET_POSITIONS = List.of(
            new Point(87, 79),
            new Point(258, 79),
            new Point(87, 221),
            new Point(258, 221),
            new Point(172, 79),
            new Point(172, 221));

    private static final List<Point> TRIPLE_SYSTEM_TOKEN_PLANET_POSITIONS = List.of(
            new Point(87, 221),
            new Point(258, 221),
            new Point(87, 79),
            new Point(258, 79),
            new Point(172, 221),
            new Point(172, 79));

    private static final List<Point> TWO_PLANET_OR_WORMHOLE_TOKEN_PLANET_POSITIONS = List.of(
            new Point(Constants.TOKEN_PLANET_POSITION),
            new Point(78, 178),
            new Point(258, 221),
            new Point(87, 221),
            new Point(172, 79),
            new Point(172, 221));

    public static int getCurrentHour() {
        long currentTime = System.currentTimeMillis();
        currentTime /= 1000;
        currentTime %= (60 * 60 * 24);
        currentTime /= (60 * 60);
        return (int) currentTime;
    }

    /**
     * @return List of Strings
     */
    public static List<String> getListFromCSV(String commaSeparatedString) {
        StringTokenizer tokenizer = new StringTokenizer(commaSeparatedString, ",");
        List<String> values = new ArrayList<>();
        while (tokenizer.hasMoreTokens()) {
            values.add(tokenizer.nextToken().trim());
        }
        return values;
    }

    // Function to find the
    // duplicates in a Stream
    public static <T> Set<T> findDuplicateInList(List<T> list) {
        // Set to store the duplicate elements
        Set<T> items = new HashSet<>();

        // Return the set of duplicate elements
        return list.stream()
                // Set.add() returns false if the element was already present in the set.
                // Hence filter such elements
                .filter(n -> !items.add(n))
                // Collect duplicate elements in the set
                .collect(Collectors.toSet());
    }

    public static String getGuildInviteURL(Guild guild, int uses) {
        return getGuildInviteURL(guild, uses, false);
    }

    public static String getGuildInviteURL(Guild guild, int uses, boolean forever) {
        DefaultGuildChannelUnion defaultChannel = guild.getDefaultChannel();
        if (!(defaultChannel instanceof TextChannel tc)) {
            BotLogger.error("Default channel is not available or is not a text channel on " + guild.getName());
        } else {
            return tc.createInvite()
                    .setMaxUses(uses)
                    .setMaxAge((long) (forever ? 0 : 7), TimeUnit.DAYS)
                    .complete()
                    .getUrl();
        }
        return "Whoops invalid url. Have one of the players on the server generate an invite";
    }

    public static double median(Collection<? extends Number> numbers) {
        if (numbers == null || numbers.isEmpty()) {
            throw new IllegalArgumentException("Cannot calculate median because no numbers were provided.");
        }

        double[] sorted =
                numbers.stream().mapToDouble(Number::doubleValue).sorted().toArray();

        int n = sorted.length;
        return n % 2 != 0 ? sorted[n / 2] : (sorted[n / 2 - 1] + sorted[n / 2]) / 2.0;
    }
}
