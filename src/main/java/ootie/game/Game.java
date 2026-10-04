package ootie.game;

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
import ootie.image.Mapper;
import ootie.json.JsonMapperManager;
import ootie.model.AreaModel;
import tools.jackson.databind.json.JsonMapper;

public class Game extends GameProperties {
    private static final JsonMapper mapper = JsonMapperManager.basic();

    @Setter
    @Getter
    private Map<String, Player> players = new LinkedHashMap<>();

    // @Getter
    // private final Map<Integer, Boolean> scPlayed = new HashMap<>();

    // private List<String> listOfTilePinged = new ArrayList<>();

    // // TODO (Jazz): These should be easily added to GameProperties
    // @Getter
    // private Map<String, Integer> thalnosUnits = new HashMap<>();

    // @Getter
    // private Map<String, String> currentAgendaVotes = new HashMap<>();

    // @Setter
    // @Getter
    // private DisplayType displayTypeForced;

    @Setter
    @Getter
    private Date lastActivePlayerChange = new Date(0);

    private boolean autoPingEnabled;

    

    @Getter
    @Setter
    private List<String> savedButtons = new ArrayList<>();

    private boolean hasEnded;
    private boolean isActive;

    private List<SimpleEntry<String, String>> tileNameAutocompleteOptionsCache;

    private final Set<String> runDataMigrations = new HashSet<>();

   
    public Game getSelf() {
        return this;
    }

    public Game() {
        long currentTimeMillis = System.currentTimeMillis();
    }

    public Player addPlayer(String id, String name) {
        Player player = new Player(this);
        player.setUserID(id);
        player.setUserName(name);
        players.put(id, player);
        return player;
    }

    public void addAreas(){
        if(getAreas() == null || getAreas().isEmpty()){
            for(AreaModel areaModel : Mapper.getAreas().values()){
                if(!areas.keySet().contains(areaModel.getId())){
                    areas.put(areaModel.getId(), new AreaObject(areaModel.getId(), "Catholic", new ArrayList<>(), new ArrayList<>()));
                }
            }
        }
    }

    public Player getPlayer(String id) {
        return players.get(id);
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
