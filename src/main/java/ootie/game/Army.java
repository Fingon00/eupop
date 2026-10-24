package ootie.game;

import lombok.Data;

@Data
public class Army {
    private String location;
    private int infantry;
    private int cavalry;
    private int artillery;
    private String leader;
    private boolean leaderWounded;
}
