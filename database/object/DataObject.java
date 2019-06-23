package database.object;

import database.list.TagList;
import database.loader.ThumbnailCreator;
import lifecycle.InstanceManager;
import user_interface.main.center.BaseTile;
import utils.FileUtil;
import utils.enums.FileType;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;

public class DataObject implements Serializable {
    private String name;
    private long size;
    private int mergeID;
    private TagList tagList;

    private transient String path;
    private transient BaseTile baseTile;

    public DataObject(File file) {
        this.name = file.getName();
        this.size = file.length();
        this.mergeID = 0;
        this.tagList = new TagList();

        this.path = file.getAbsolutePath();
    }

    public void generateTileEffect() {
        if (baseTile != null) baseTile.generateEffect();
    }
    public ArrayList<DataObject> getMergeGroup() {
        if (mergeID == 0) {
            return new ArrayList<>();
        } else {
            ArrayList<DataObject> mergedObjects = new ArrayList<>();
            for (DataObject dataObject : InstanceManager.getObjectListMain()) {
                if (dataObject.getMergeID() == mergeID) {
                    mergedObjects.add(dataObject);
                }
            }
            return mergedObjects;
        }
    }
    public FileType getFileType() {
        for (String ext : FileUtil.getImageExtensions()) {
            if (getName().endsWith(ext)) {
                return FileType.IMAGE;
            }
        }
        for (String ext : FileUtil.getGifExtensions()) {
            if (getName().endsWith(ext)) {
                return FileType.GIF;
            }
        }
        for (String ext : FileUtil.getVideoExtensions()) {
            if (getName().endsWith(ext)) {
                return FileType.VIDEO;
            }
        }
        InstanceManager.getLogger().error("file type not supported");
        return null;
    }

    public String getName() {
        return name;
    }
    public long getSize() {
        return size;
    }
    public String getPath() {
        return path;
    }
    public int getMergeID() {
        return mergeID;
    }
    public TagList getTagList() {
        return tagList;
    }
    public BaseTile getBaseTile() {
        return baseTile;
    }
    public String getCacheFile() {
        return FileUtil.getDirCache() + name + "-" + size + ThumbnailCreator.getCacheExtension();
    }

    public void setName(String name) {
        this.name = name;
    }
    public void setPath(String path) {
        this.path = path;
    }
    public void setSize(long size) {
        this.size = size;
    }
    public void setBaseTile(BaseTile baseTile) {
        this.baseTile = (baseTile != null) ? baseTile : new BaseTile(this, null);
    }
    public void setTagList(TagList tagList) {
        this.tagList = tagList;
    }
    public void setMergeID(int mergeID) {
        this.mergeID = mergeID;
    }
}
