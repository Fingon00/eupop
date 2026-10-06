package ootie.game;

import lombok.Data;

@Data
public class ProvinceObjectProperties {
    private String id;
    private String owner;
    private String sieger;
    private String coreOwner;
    private String vassalOverlord;
    private boolean unrest;
    private boolean large;
    private double x;
    private double y;
}
