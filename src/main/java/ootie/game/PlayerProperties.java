package ootie.game;

import java.util.ArrayList;
import java.util.List;
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

    // private Map<String, Integer> breakthroughTGs = new LinkedHashMap<>();

    // private Set<String> abilities = new HashSet<>();
    private List<String> largeProvinces = new ArrayList<>();
    private List<String> smallProvinces = new ArrayList<>();
}
