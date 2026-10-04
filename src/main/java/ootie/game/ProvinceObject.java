package ootie.game;

import java.util.List;

import ootie.image.Mapper;
import ootie.model.AreaModel;
import ootie.model.ProvinceModel;

public class ProvinceObject extends ProvinceObjectProperties {
    
    public ProvinceObject(String id, boolean isLarge) {
        this.setId(id);
        ProvinceModel provinceModel = Mapper.getProvinces().get(id);
        this.setLarge(isLarge);
        this.setX(provinceModel.getX());
        this.setY(provinceModel.getY());


    }
}
