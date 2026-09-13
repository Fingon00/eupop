package ootie.game;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;

public class Player extends PlayerProperties {

    private static final int EMBED_FIELD_VALUE_LIMIT = 1024;

    @Getter
    private final Game game;

    @Getter
    private final Map<String, Integer> actionCards = new LinkedHashMap<>();

    @Getter
    private final Map<String, Integer> events = new LinkedHashMap<>();

    @Getter
    private final Map<String, Integer> trapCards = new LinkedHashMap<>();

    @Getter
    private final Map<String, Integer> secrets = new LinkedHashMap<>();

    // Map of (SecretObjectiveModel ID, Random Number ID)
    @Getter
    private final Map<String, Integer> secretsScored = new LinkedHashMap<>();

    @Getter
    private final Map<String, Integer> unitCaps = new HashMap<>();

    @Getter
    private final Map<String, String> trapCardsPlanets = new LinkedHashMap<>();

    private final Map<String, String> fowSeenTiles = new HashMap<>();
    private final Map<String, String> fowCustomLabels = new HashMap<>();

    @Setter
    @Getter
    private Map<String, Integer> promissoryNotes = new LinkedHashMap<>();

    private @Getter Map<String, Integer> currentProducedUnits = new HashMap<>();
    // <pool: <color: count>>
    private final Map<String, Map<String, Integer>> debtTokens = new LinkedHashMap<>();

    public Player(String userID, String userName, Game game) {
        setUserID(userID);
        setUserName(userName);
        setStatsTrackedUserID(userID);
        setStatsTrackedUserName(userName);
        this.game = game;
    }

    public boolean is(Player p2) {
        if (p2 == null) return false;
        return getUserID().equals(p2.getUserID());
    }

    public String getFactionCheckerPrefix() {
        return factionButtonChecker();
    }

    public String factionButtonChecker() {
        if (isNpc() || isDummy()) {
            return dummyPlayerSpoof();
        }
        return "FFCC_" + getFaction() + "_";
    }

    public String dummyPlayerSpoof() {
        return "dummyPlayerSpoof" + getFaction() + "_";
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Player that)) return false;
        return Objects.equals(getUserID(), that.getUserID());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getUserID());
    }
}
