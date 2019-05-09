package database.object;

import database.list.InfoObjectList;
import database.loader.LoaderUtil;
import system.CommonUtil;
import system.FileTypeEnum;
import system.InstanceRepo;
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
    public FileTypeEnum getFileType() {
        for (String ext : LoaderUtil.getImageExtensions()) {
            if (name.endsWith(ext)) {
                return FileTypeEnum.IMAGE;
            }
        }
        for (String ext : LoaderUtil.getGifExtensions()) {
            if (name.endsWith(ext)) {
                return FileTypeEnum.GIF;
            }
        }
        for (String ext : LoaderUtil.getVideoExtensions()) {
            if (name.endsWith(ext)) {
                return FileTypeEnum.VIDEO;
            }
        }
        InstanceRepo.logger.error(this, "file type not supported");
        return null;
    }

    public String getName() {
        return name;
    }
    public int getMergeID() {
        return mergeID;
    }
    public InfoObjectList getInfoObjectList() {
        return infoObjectList;
    }
    public BaseTile getBaseTile() {
        return baseTile;
    }
    public void setBaseTile(BaseTile baseTile) {
        this.baseTile = (baseTile != null) ? baseTile : new BaseTile(this, null);
    }
    public void setInfoObjectList(InfoObjectList infoObjectList) {
        this.infoObjectList = infoObjectList;
    }
    public void setMergeID(int mergeID) {
        this.mergeID = mergeID;
    }
}
