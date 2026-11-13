package ootie.service.emoji;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import ootie.logging.BotLogger;
import ootie.service.emoji.ApplicationEmojiCacheService.CachedEmoji;

@FunctionalInterface
public interface OotieEmoji {

    default Emoji asEmoji() {
        String mention = toString();
        if (mention.isBlank()) return null;
        return Emoji.fromFormatted(mention);
    }

    /** Returns the formatted emoji string {@code <EmojiName:ident>} */
    default String emojiString() {
        CachedEmoji emoji = ApplicationEmojiService.getApplicationEmoji(name());
        if (emoji == null) {
            BotLogger.warning("Could not find requested emoji: " + name());
            return ApplicationEmojiService.fallbackEmoji;
        }
        return emoji.getFormatted();
    }

    String name();

    // -------------------------------------------------------------------------------------------------------------------------------
    // Static Stuff
    // -------------------------------------------------------------------------------------------------------------------------------

    static Set<OotieEmoji> allEmojiEnums() {
        Set<OotieEmoji> values = new HashSet<>();
        values.addAll(Arrays.asList(DiceEmojis.values()));
        // And then everything else

        return values;
    }

    static <T extends OotieEmoji> T getEmojiFromName(Class<T> clazz, String name) {
        if (!clazz.isEnum()) return null;
        List<T> consts = Arrays.asList(clazz.getEnumConstants());
        return consts.stream()
                .filter(c -> c.name().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    static OotieEmoji findEmoji(String category, String name) {
        return switch (category.toLowerCase()) {
            case "dice", "diceemojis" -> getEmojiFromName(DiceEmojis.class, name);
            default -> null;
        };
    }

    @Nullable
    static OotieEmoji findEmojiFromJustName(String name) {
        OotieEmoji uniq = null;
        for (OotieEmoji emoji : allEmojiEnums()) {
            if (emoji.name().equals(name)) {
                if (uniq == null) uniq = emoji;
                else return null;
            }
        }
        return uniq;
    }
}
