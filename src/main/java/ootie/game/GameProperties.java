package ootie.game;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GameProperties {
    // Game metadata

    // Discord Snowflakes
    private String activePlayerID;
    private String launchPostThreadID;
    private String savedChannelID;
    private String name;
    private boolean hasEnded;
    // Decks
    // private List<String> secretObjectives;

    // private Map<String, Integer> discardActionCards = new LinkedHashMap<>();
    // private Set<String> playedActionCards = new LinkedHashSet<>();

    // Stored Values
    private Map<String, String> storedValueMap = new HashMap<>();

    // Misc Helpers
    public String getID() {
        return name;
    }
}
