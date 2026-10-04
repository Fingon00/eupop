package ootie.game;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import lombok.Data;

@Data
public class ProvinceObjectProperties {
    private String id;
    private List<String> claims = new ArrayList<>();
    private String owner;
    private String sieger;
    private String coreOwner;
    private String vassalOverlord;
    private boolean unrest;
    private boolean large;
    private double x;
    private double y;
    
    
    
}
