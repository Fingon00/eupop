package ootie.game;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import ootie.discord.JdaService;
import ootie.helpers.Constants;
import ootie.image.Mapper;
import ootie.json.JsonMapperManager;
import ootie.model.ActionCardModel;
import ootie.model.ProvinceModel;
import tools.jackson.databind.json.JsonMapper;

public class Game extends GameProperties {
    private static final JsonMapper mapper = JsonMapperManager.basic();

    @Setter
    @Getter
    private Map<String, Player> players = new LinkedHashMap<>();

    public List<Player> getGamePlayers() {
        return new ArrayList<>(getPlayers().values());
    }

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

    public Player getNextPlayer(Player player) {
        List<String> playerOrder = new ArrayList<>();
        playerOrder.addAll(getPlayerOrder());
        Collections.rotate(playerOrder, -1 * playerOrder.indexOf(player.getRealm()) - 1);
        for (String realm : playerOrder) {
            Player p2 = getPlayerByRealmOrColor(realm);
            if (!p2.isPassed()) {
                return p2;
            }
        }
        return null;
    }

    public List<Player> getPassedPlayers() {
        List<Player> passed = new ArrayList<>();
        for (Player player : getGamePlayers()) {
            if (player.isPassed()) {
                passed.add(player);
            }
        }
        return passed;
    }

    public boolean canPlayerTakeANormalTurn(Player player) {
        String key = "remainingPlayersWithTurn";
        if (!getStoredValue("passMoneyRanOut").isEmpty() && !getStoredValue(key).contains(player.getRealm())) {
            return false;
        } else {
            setStoredValue(key, getStoredValue(key).replace(player.getRealm(), ""));
            return true;
        }
    }

    public Player getProvinceOwner(String province) {
        Player owner = null;
        for (Player player : getGamePlayers()) {
            if (player.getAllProvinces().contains(province)) {
                return player;
            }
        }
        return owner;
    }

    public List<Player> getPlayersWhoCompletedMilestone(String milestone) {
        List<Player> milestonePs = new ArrayList<>();
        for (Player player : getGamePlayers()) {
            if (player.getCompletedMilestones().contains(milestone)) {
                milestonePs.add(player);
            }
        }
        return milestonePs;
    }

    public List<Player> getPlayersWhoResearchedIdea(String idea) {
        List<Player> milestonePs = new ArrayList<>();
        for (Player player : getGamePlayers()) {
            if (player.getIdeas().contains(idea)) {
                milestonePs.add(player);
            }
        }
        return milestonePs;
    }

    public void convertArea(String area, String religion) {
        getConvertedAreas().put(area, religion);
    }

    public int getProvinceTaxValue(String province) {
        Player owner = getProvinceOwner(province);
        if (owner != null) {
            if (owner.getLargeProvinces().contains(province)) {
                return 2;
            } else {
                return 1;
            }
        } else {
            ProvinceModel prov = Mapper.getProvinces().get(province);
            if (prov.isLarge()) {
                return 2;
            }
        }
        return 1;
    }

    public List<String> getInfluenceInArea(String area) {
        List<String> influence = new ArrayList<>();
        for (Player player : getGamePlayers()) {
            for (String inf : player.getInfluence()) {
                if (inf.equalsIgnoreCase(area)) {
                    influence.add(player.getColor());
                }
            }
        }
        return influence;
    }

    public List<String> getClaimsInArea(String area) {
        List<String> claims = new ArrayList<>();
        for (Player player : getGamePlayers()) {
            for (String claim : player.getClaims()) {
                if (claim.equalsIgnoreCase(area)) {
                    claims.add(player.getColor());
                }
            }
        }
        return claims;
    }

    public List<String> getUnitsInArea(String area) {
        List<String> units = new ArrayList<>();
        for (Player player : getGamePlayers()) {
            for (String inf : player.getSingularInfantry()) {
                if (inf.equalsIgnoreCase(area)) {
                    units.add(player.getColor() + "_infantry");
                }
            }
            if (area.equalsIgnoreCase(player.getArmy1().getLocation())) {
                units.add(player.getColor() + "_army1");
            }
            if (area.equalsIgnoreCase(player.getArmy2().getLocation())) {
                units.add(player.getColor() + "_army2");
            }
            if (area.equalsIgnoreCase(player.getArmy3().getLocation())) {
                units.add(player.getColor() + "_army3");
            }
            if (area.equalsIgnoreCase(player.getFleet().getLocation())) {
                units.add(player.getColor() + "_fleet");
            }
            int[] ships = player.getLightShips().get(area);
            if (ships != null && ships.length != 0) {
                for (int x = 0; x < ships[0]; x++) {
                    if (x < ships[1]) {
                        units.add(player.getColor() + "_ship_protected");
                    } else {
                        units.add(player.getColor() + "_ship_unprotected");
                    }
                }
            }
        }

        for (String rebel : getRebels()) {
            if (rebel.equalsIgnoreCase(area)) {
                units.add("rebel_rebel");
            }
        }

        return units;
    }

    public TextChannel getMainGameChannel() {
        if (JdaService.jda == null) return null;
        try {
            return JdaService.jda.getTextChannelById(getSavedChannelID());
        } catch (Exception e) {
            List<TextChannel> gameChannels =
                    JdaService.jda.getTextChannelsByName(getName() + Constants.ACTIONS_CHANNEL_SUFFIX, true);
            if (gameChannels.size() == 1) {
                TextChannel mainGameChannel = gameChannels.getFirst();
                setSavedChannelID(mainGameChannel.getId());
                return mainGameChannel;
            }
        }
        return null;
    }

    public Player getPlayer(String id) {
        return players.get(id);
    }

    public Player getPlayerByRealmOrColor(String id) {
        if (id == null) {
            return null;
        }
        for (Player p : players.values()) {
            if (id.equalsIgnoreCase(p.getColor()) || id.equalsIgnoreCase(p.getRealm())) {
                return p;
            }
        }
        return null;
    }

    public void addActionCard(String id) {
        ActionCardModel mod = Mapper.getActionCards().get(id);
        if (mod != null) {
            switch (mod.getType()) {
                case "administrative":
                    getAdministrativeActionCardDeck().add(mod.getId());
                    Collections.shuffle(getAdministrativeActionCardDeck());
                    break;
                case "military":
                    getMilitaryActionCardDeck().add(mod.getId());
                    Collections.shuffle(getMilitaryActionCardDeck());
                    break;
                case "diplomatic":
                    getDiplomaticActionCardDeck().add(mod.getId());
                    Collections.shuffle(getDiplomaticActionCardDeck());
                    break;
            }
        }
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
