package ootie.game;

import static java.util.function.Predicate.*;
import static org.apache.commons.collections4.CollectionUtils.*;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.internal.utils.tuple.Pair;
import ootie.helpers.DisplayType;
import ootie.json.JsonMapperManager;
import tools.jackson.databind.json.JsonMapper;

public class Game extends GameProperties {
    private static final JsonMapper mapper = JsonMapperManager.basic();

    @Setter
    @Getter
    private Map<String, Player> players = new LinkedHashMap<>();

    @Getter
    private final Map<Integer, Boolean> scPlayed = new HashMap<>();

    private List<String> listOfTilePinged = new ArrayList<>();

    // TODO (Jazz): These should be easily added to GameProperties
    @Getter
    private Map<String, Integer> thalnosUnits = new HashMap<>();

    @Getter
    private Map<String, String> currentAgendaVotes = new HashMap<>();

    @Setter
    @Getter
    private DisplayType displayTypeForced;

    @Setter
    @Getter
    private Date lastActivePlayerChange = new Date(0);

    private boolean autoPingEnabled;

    @Setter
    @Getter
    private Map<String, Integer> discardedEvents = new LinkedHashMap<>();

    @Getter
    @Setter
    private Map<String, Integer> eventsInEffect = new LinkedHashMap<>();

    @Setter
    @Getter
    private Map<String, Integer> discardAgendas = new LinkedHashMap<>();

    @Getter
    @Setter
    private Map<String, Integer> sentAgendas = new LinkedHashMap<>();

    @Setter
    @Getter
    private Map<String, Integer> laws = new LinkedHashMap<>();

    @Setter
    @Getter
    private Map<String, String> lawsInfo = new LinkedHashMap<>();

    @Getter
    @Setter
    private Map<String, Integer> revealedPublicObjectives = new LinkedHashMap<>();

    @Getter
    @Setter
    private Map<String, Integer> customPublicVP = new LinkedHashMap<>();

    @Getter
    @Setter
    private Map<String, List<String>> scoredPublicObjectives = new LinkedHashMap<>();

    @Getter
    @Setter
    private Map<String, List<String>> customAdjacentTiles = new LinkedHashMap<>();

    @Getter
    @Setter
    private Map<String, String> customHyperlaneData = new LinkedHashMap<>();

    private LinkedHashMap<Pair<String, Integer>, String> adjacencyOverrides = new LinkedHashMap<>();

    @Getter
    @Setter
    private List<String> publicObjectives1;

    @Getter
    @Setter
    private List<String> publicObjectives2;

    @Getter
    @Setter
    private List<String> publicObjectives1Peekable = new ArrayList<>();

    @Getter
    @Setter
    private List<String> publicObjectives2Peekable = new ArrayList<>();

    @Getter
    @Setter
    private Map<String, List<String>> publicObjectives1Peeked = new LinkedHashMap<>();

    @Getter
    @Setter
    private Map<String, List<String>> publicObjectives2Peeked = new LinkedHashMap<>();

    @Getter
    @Setter
    private List<String> savedButtons = new ArrayList<>();

    @Setter
    @Getter
    private List<String> soToPoList = new ArrayList<>();

    @Getter
    private List<String> purgedPN = new ArrayList<>();

    private List<String> explore;
    private List<String> discardExplore = new ArrayList<>();
    private List<String> relics;
    private boolean hasEnded;
    private boolean isActive;

    private List<SimpleEntry<String, String>> tileNameAutocompleteOptionsCache;

    private final Set<String> runDataMigrations = new HashSet<>();

    @Getter
    @Setter
    private Map<String, Integer> tileDistances = new HashMap<>();

    @Setter
    @Getter
    private String miltyDraftString;

    @Setter
    @Getter
    private String draftSystemSettingsJson;

    private Map<String, String> debtPoolIcons = new HashMap<>();

    public Game getSelf() {
        return this;
    }

    public Game() {
        long currentTimeMillis = System.currentTimeMillis();
    }

    public void newGameSetup() {
        // Normal Decks
    }

    /**
     * @param includeDummies also consider dummy players, which control planets in
     *                       some setups (e.g. the
     *                       Unstable Planet target list is built from real players
     *                       and dummies).
     */
}
