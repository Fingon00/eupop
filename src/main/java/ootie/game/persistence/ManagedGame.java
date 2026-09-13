package ootie.game.persistence;

import static java.util.stream.Collectors.*;

import java.util.Objects;
import lombok.Getter;
import ootie.game.Game;
import org.apache.commons.lang3.StringUtils;

@Getter
public class ManagedGame {

    private static final long SIXTY_DAYS_MILLISECONDS = 1000L * 60 * 60 * 24 * 60;

    // BE CAREFUL ADDING FIELDS TO THIS CLASS, AS IT CAN EASILY BALLOON THE DATA ON
    // THE HEAP BY MEGABYTES PER FIELD
    private final String name;

    public ManagedGame(Game game) {
        name = game.getName();
    }

    private static String sanitizeToNull(String str) {
        if (StringUtils.isBlank(str) || "null".equalsIgnoreCase(str)) {
            return null;
        }
        return str;
    }

    public Game getGame() {
        return GameManager.get(name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ManagedGame that)) return false;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name);
    }
}
