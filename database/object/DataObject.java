package database.object;

import database.list.InfoList;
import system.CommonUtil;
import user_interface.single_instance.center.BaseTile;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;

public class DataObject implements Serializable {
    private String name;
    private int mergeID;
    private InfoList infoList;
    private transient BaseTile baseTile;

    public DataObject(File file) {
        this.name = file.getName();
        this.mergeID = 0;
        this.infoList = new InfoList();
    }

    public void generateTileEffect() {
        baseTile.generateEffect();
    }
    public ArrayList<DataObject> getMergeGroup() {
        if (mergeID == 0) {
            return new ArrayList<>();
        } else {
            ArrayList<DataObject> mergedObjects = new ArrayList<>();
            for (DataObject dataObject : CommonUtil.mainListData) {
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
    public InfoList getInfoList() {
        return infoList;
    }
    public void setBaseTile(BaseTile baseTile) {
        this.baseTile = (baseTile != null) ? baseTile : new BaseTile(this, null);
    }
    public void setInfoList(InfoList infoList) {
        this.infoList = infoList;
    }
}
