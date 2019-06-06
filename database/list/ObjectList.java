package database.list;

import control.Reload;
import database.object.DataObject;
import database.object.TagObject;
import lifecycle.InstanceManager;
import user_interface.singleton.center.BaseTile;

import java.util.ArrayList;
import java.util.Random;

public class ObjectList extends ArrayList<DataObject> {
    public boolean add(DataObject dataObject) {
        if (super.add(dataObject)) {
            InstanceManager.getReload().flag(Reload.Control.FILTER);
            return true;
        }
        return false;
    }
    public boolean setAll(ObjectList dataObjects) {
        this.clear();
        InstanceManager.getReload().flag(Reload.Control.FILTER);
        return this.addAll(dataObjects);
    }
    public void clear() {
        super.clear();
        InstanceManager.getReload().flag(Reload.Control.FILTER);
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

    public DataObject getRandom() {
        if (!this.isEmpty()) {
            int index = new Random().nextInt(this.size());
            DataObject chosenDataObject = this.get(index);

            if (chosenDataObject.getMergeID() == 0) {
                return chosenDataObject;
            } else {
                ArrayList<DataObject> mergeGroup = chosenDataObject.getMergeGroup();
                return mergeGroup.get(new Random().nextInt(mergeGroup.size()));
            }
        } else {
            return null;
        }
    }
}
