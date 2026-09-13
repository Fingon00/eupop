package ootie.game;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GameProperties {
    // Game metadata

    // Discord Snowflakes
    private String speakerUserID = "";
    private String tyrantUserID = "";
    private String activePlayerID;
    private String launchPostThreadID;
    private String savedChannelID;
    private String name;

    // Decks
    private List<String> secretObjectives;
    private List<String> actionCards;
    private List<String> agendas;
    private List<String> mandates;
    private List<String> events; // ignis_aurora

    private Map<String, Integer> discardActionCards = new LinkedHashMap<>();
    private Set<String> playedActionCards = new LinkedHashSet<>();

    // Stored Values
    private final Map<String, String> storedValueMap = new HashMap<>();

    // Misc Helpers
    public String getID() {
        return name;
    }
}
