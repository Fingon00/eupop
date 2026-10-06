package ootie.game;

import java.util.List;
import ootie.image.Mapper;
import ootie.model.AreaModel;

public class AreaObject extends AreaObjectProperties {

    public AreaObject(
            String id, String religion, List<String> units, List<String> influenceCubes, List<String> claims) {
        this.setId(id);
        if (religion != null) {
            this.setReligion(religion);
        }
        AreaModel areaModel = Mapper.getAreas().get(id);
        areaModel.getProvinces().forEach(province -> {
            this.getProvinces().put(province, new ProvinceObject(province, false));
        });
        areaModel.getBorders().forEach(border -> this.getBorders().add(border));
        areaModel.getMountainBorders().forEach(mountainBorder -> this.getMountainBorders()
                .add(mountainBorder));
        this.setType(areaModel.getType());
        this.setReligiousCenter(areaModel.getReligiousCenter());
        if (units != null) {
            this.setUnits(units);
        }
        if (influenceCubes != null) {
            this.setInfluenceCubes(influenceCubes);
        }
        if (claims != null) {
            this.setClaims(claims);
        }
    }
}
