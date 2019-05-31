package database.list;

import control.reload.Reload;
import database.object.DataObject;
import database.object.TagObject;
import system.Instances;

import java.util.ArrayList;

public class DataObjectList extends ArrayList<DataObject> implements Instances {
    public boolean add(DataObject dataObject) {
        if (super.add(dataObject)) {
            reload.notifyChangeIn(Reload.Control.FILTER);
            return true;
        }
        return false;
    }
    public boolean setAll(DataObjectList dataObjects) {
        this.clear();
        reload.notifyChangeIn(Reload.Control.FILTER);
        return this.addAll(dataObjects);
    }
    public void clear() {
        super.clear();
        reload.notifyChangeIn(Reload.Control.FILTER);
    }

    public TagList getIntersectingTags() {
        if (this.size() < 1) return new TagList();

        TagList intersectingTags = new TagList();
        DataObject lastObject = this.get(this.size() - 1);
        for (TagObject tagObject : this.get(0).getTagList()) {
            for (DataObject dataObject : this) {
                if (dataObject.getTagList().contains(tagObject)) {
                    if (dataObject.equals(lastObject)) {
                        intersectingTags.add(tagObject);
                    }
                } else break;
            }
        }
        return intersectingTags;
    }
    public TagList getSharedTags() {
        if (this.size() < 1) return new TagList();

        TagList sharedTags = new TagList();
        for (DataObject dataObject : this) {
            for (TagObject tagObject : dataObject.getTagList()) {
                if (!sharedTags.contains(tagObject)) {
                    sharedTags.add(tagObject);
                }
            }
        }
        return sharedTags;
    }
}
