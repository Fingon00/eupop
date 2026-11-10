package ootie.game;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import lombok.Data;

@Data
public class PlayerProperties {
    // Basic information
    private String userID;
    private String userName;
    private String realm;
    private String color;
    private String autoCompleteRepresentation;
    // Channels & IDs
    private @Nullable String cardsInfoThreadID;
    // State and settings
    private boolean passed;
    private int ducats = 15;
    private int adminPower = 3;
    private int diploPower = 3;
    private int militaryPower = 3;
    private int stability;
    private int botPower;
    private int spentBotPower;
    private int colonists;
    private int prestige;
    private int manpowerExhausted;
    private int manpowerAvailable;
    private Army army1 = new Army();
    private Army army2 = new Army();
    private Army army3 = new Army();
    private Fleet fleet = new Fleet();
    private String stateReligion = "Catholic";
    private String diploAdvisor;
    private String militaryAdvisor;
    private String adminAdvisor;
    private String ruler;
    private boolean isBot;
    private boolean changedNatFocus;
    private boolean usedEvent;
    private boolean defendingHRE;
    private boolean committedToCrusade;
    private String botDifficulty = "Normal";

    // private Map<String, Integer> breakthroughTGs = new LinkedHashMap<>();

    // private Set<String> abilities = new HashSet<>();
    private List<String> alliances = new ArrayList<>();
    private List<String> truces = new ArrayList<>();
    private List<String> royalMarriages = new ArrayList<>();
    private List<String> claims = new ArrayList<>();
    private List<String> cbs = new ArrayList<>();
    private List<String> largeProvinces = new ArrayList<>();
    private List<String> smallProvinces = new ArrayList<>();
    private List<String> vassalProvinces = new ArrayList<>();
    private List<String> unrestProvinces = new ArrayList<>();
    private List<String> coredAreas = new ArrayList<>();
    private List<String> botFocusCubes = new ArrayList<>();
    private List<String> actionCards = new ArrayList<>();
    private List<String> missions = new ArrayList<>();
    private List<String> unusedMissions = new ArrayList<>();
    private List<String> ideas = new ArrayList<>();
    private List<String> botDeckDiscard = new ArrayList<>();
    private List<String> botDeck = new ArrayList<>();
    private List<String> atWarWith = new ArrayList<>();
    private List<String> singularInfantry = new ArrayList<>();
    private List<String> influence = new ArrayList<>();
    private Map<String, int[]> lightShips = new LinkedHashMap<>();
    private Map<String, Boolean> merchants = new LinkedHashMap<>();
}
