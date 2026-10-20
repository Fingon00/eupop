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
    private List<String> diplomaticActionCardDeck = new ArrayList<>();
    private List<String> militaryActionCardDeck = new ArrayList<>();
    private List<String> administrativeActionCardDeck = new ArrayList<>();

    private List<String> diplomaticIdeas = new ArrayList<>();
    private List<String> militaryIdeas = new ArrayList<>();
    private List<String> administrativeIdeas = new ArrayList<>();

    private List<String> eventDeck = new ArrayList<>();
    private List<String> powerStruggleDeck = new ArrayList<>();
    private String activePowerStruggle;
    private List<String> nonRomaCardinals = new ArrayList<>();
    private List<String> passedPlayerOrder = new ArrayList<>();
    private List<String> playerOrder = new ArrayList<>();
    private String romaCardinal;
    private String emperorer;
    private String hreReligion;
    private int imperialAuthority = 3;
    private int imperialManpower = 0;
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
