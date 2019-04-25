package database.list;

import database.object.DataObject;
import database.object.TagObject;
import system.InstanceRepo;

import java.util.ArrayList;

public class DataObjectList extends ArrayList<DataObject> implements InstanceRepo {
    public boolean setAll(DataObjectList dataObjects) {
        this.clear();
        return this.addAll(dataObjects);
    }

    public InfoObjectList getIntersectingTags() {
        if (this.size() < 1) return new InfoObjectList();

        InfoObjectList intersectingTags = new InfoObjectList();
        DataObject lastObject = this.get(this.size() - 1);
        for (TagObject tagObject : this.get(0).getInfoObjectList()) {
            for (DataObject dataObject : this) {
                if (dataObject.getInfoObjectList().contains(tagObject)) {
                    if (dataObject.equals(lastObject)) {
                        intersectingTags.add(tagObject);
                    }
                } else break;
            }
        }
        return intersectingTags;
    }
    public InfoObjectList getSharedTags() {
        if (this.size() < 1) return new InfoObjectList();

        InfoObjectList sharedTags = new InfoObjectList();
        for (DataObject dataObject : this) {
            for (TagObject tagObject : dataObject.getInfoObjectList()) {
                if (!sharedTags.contains(tagObject)) {
                    sharedTags.add(tagObject);
                }
            }
        }
        return sharedTags;
    }
}
