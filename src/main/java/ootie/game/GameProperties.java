package ootie.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    public Map<String, AreaObject> areas;
    private List<String> diplomaticActionCardDeck = new ArrayList<>();
    private List<String> militaryActionCardDeck = new ArrayList<>();
    private List<String> administrativeActionCardDeck = new ArrayList<>();

    private List<String> eventDeck = new ArrayList<>();
    private List<String> revealedEventCards = new ArrayList<>();
    private List<String> unrevealedEventCards = new ArrayList<>();

    private int age = 1;

    // private Map<String, Integer> discardActionCards = new LinkedHashMap<>();
    // private Set<String> playedActionCards = new LinkedHashSet<>();

    // Stored Values
    private Map<String, String> storedValueMap = new HashMap<>();

    // Misc Helpers
    public String getID() {
        return name;
    }
}
