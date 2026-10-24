package ootie.game;

import java.util.LinkedHashMap;
import java.util.Map;

import lombok.Data;

@Data
public class Fleet {
    private String location;
    private int lightShips;
    private Map<String, Boolean> heavyShips = new LinkedHashMap<>();
    private int galleys;
    private String leader;
    private boolean leaderWounded;
}
