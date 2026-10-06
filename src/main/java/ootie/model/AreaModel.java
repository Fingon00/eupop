package ootie.model;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import ootie.model.Source.ComponentSource;

@Data
public class AreaModel implements ModelInterface {
    private ComponentSource source = ComponentSource.base;
    private String id;
    private List<String> borders = new ArrayList<>();
    private List<String> mountainBorders = new ArrayList<>();
    private List<String> provinces = new ArrayList<>();
    private String religion;
    private String type;
    private int[] religiousCenter;

    @Override
    public boolean isValid() {
        return id != null;
    }

    @Override
    public String getAlias() {
        return id;
    }
}
