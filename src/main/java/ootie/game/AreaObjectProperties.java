package ootie.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import lombok.Data;

@Data
public class AreaObjectProperties {
    private String id;
    private List<String> borders = new ArrayList<>();
    private List<String> mountainBorders = new ArrayList<>();
    private Map<String, ProvinceObject> provinces = new HashMap<>();
    private String religion;
    private List<String> units = new ArrayList<>();
    private List<String> influenceCubes = new ArrayList<>();
    private String type;
    private int[] religiousCenter;
    
    
}
