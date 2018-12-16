package database.object;

import database.list.InfoList;
import gui.singleton.center.BaseTile;

import java.io.File;
import java.io.Serializable;

public class DataObject implements Serializable {
    private String name;
    private InfoList infoList;
    private transient BaseTile baseTile;

    public DataObject(String name, InfoList tagObjects) {
        this.name = name;
        this.infoList = tagObjects;
    }
    public DataObject(File file) {
        this(file.getName(), new InfoList());
    }

    public void generateTileEffect() {
        baseTile.generateEffect();
    }
    public String getName() {
        return name;
    }
    public InfoList getInfoList() {
        return infoList;
    }
    public BaseTile getBaseTile() {
        return baseTile;
    }
    public void setBaseTile(BaseTile baseTile) {
        this.baseTile = (baseTile != null) ? baseTile : new BaseTile(this, null);
    }
}
