package ootie.game;

import lombok.Data;

@Data
public class Fleet {
    private String location;
    private int lightShips;
    private int heavyShips;
    private int damagedHeavyShips;
    private int galleys;
    private String leader;
    private boolean leaderWounded;
}
