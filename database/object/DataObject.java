package database.object;

import database.list.InfoObjectList;
import system.CommonUtil;
import user_interface.singleton.center.BaseTile;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;

public class DataObject implements Serializable {
    private String name;
    private int mergeID;
    private InfoObjectList infoObjectList;
    private transient BaseTile baseTile;

    public DataObject(File file) {
        this.name = file.getName();
        this.mergeID = 0;
        this.infoObjectList = new InfoObjectList();
    }

    public void generateTileEffect() {
        baseTile.generateEffect();
    }
    public ArrayList<DataObject> getMergeGroup() {
        if (mergeID == 0) {
            return new ArrayList<>();
        } else {
            ArrayList<DataObject> mergedObjects = new ArrayList<>();
            for (DataObject dataObject : CommonUtil.mainDataList) {
                if (dataObject.getMergeID() == mergeID) {
                    mergedObjects.add(dataObject);
                }
            }
            return mergedObjects;
        }
    }

    public String getName() {
        return name;
    }
    public int getMergeID() {
        return mergeID;
    }
    public void setMergeID(int mergeID) {
        this.mergeID = mergeID;
    }
    public BaseTile getBaseTile() {
        return baseTile;
    }
    public InfoObjectList getInfoObjectList() {
        return infoObjectList;
    }
    public void setBaseTile(BaseTile baseTile) {
        this.baseTile = (baseTile != null) ? baseTile : new BaseTile(this, null);
    }
    public void setInfoObjectList(InfoObjectList infoObjectList) {
        this.infoObjectList = infoObjectList;
    }
}
