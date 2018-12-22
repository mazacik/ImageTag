package database.object;

import database.list.BaseListInfo;
import gui.node.center.BaseTile;

import java.io.File;
import java.io.Serializable;

public class DataObject implements Serializable {
    private String name;
    private BaseListInfo baseListInfo;
    private transient BaseTile baseTile;

    public DataObject(String name, BaseListInfo tagObjects) {
        this.name = name;
        this.baseListInfo = tagObjects;
    }
    public DataObject(File file) {
        this(file.getName(), new BaseListInfo());
    }

    public void generateTileEffect() {
        baseTile.generateEffect();
    }
    public String getName() {
        return name;
    }
    public BaseListInfo getBaseListInfo() {
        return baseListInfo;
    }
    public BaseTile getBaseTile() {
        return baseTile;
    }
    public void setBaseTile(BaseTile baseTile) {
        this.baseTile = (baseTile != null) ? baseTile : new BaseTile(this, null);
    }
}
