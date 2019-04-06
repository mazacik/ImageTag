package database.list;

import control.reload.Reload;
import database.object.DataObject;
import database.object.TagObject;
import system.InstanceRepo;
import user_interface.factory.stage.InfoObjectEditStage;

public class InfoObjectListMain extends InfoObjectList implements InstanceRepo {
    public void initialize() {
        for (DataObject dataIterator : mainDataList) {
            InfoObjectList infoObjectList = dataIterator.getInfoObjectList();
            for (TagObject tagIterator : infoObjectList) {
                if (this.contains(tagIterator)) {
                    infoObjectList.set(infoObjectList.indexOf(tagIterator), getTagObject(tagIterator));
                } else {
                    this.add(tagIterator);
                }
            }
        }
        super.sort();
    }

    public boolean add(TagObject tagObject) {
        if (tagObject == null) return false;
        if (super.add(tagObject)) {
            reload.notifyChangeIn(Reload.Control.INFO);
            return true;
        }
        return false;
    }
    public boolean remove(TagObject tagObject) {
        if (tagObject == null) return false;
        if (super.remove(tagObject)) {
            //filter.apply();
            reload.notifyChangeIn(Reload.Control.INFO);
            return true;
        }
        return false;
    }
    public boolean edit(TagObject tagObject) {
        if (tagObject == null) return false;
        TagObject newTagObject = new InfoObjectEditStage(tagObject).getResult();
        if (newTagObject != null) {
            tagObject.setFull(newTagObject.getGroup(), newTagObject.getName());
            super.sort();
            reload.notifyChangeIn(Reload.Control.INFO);
            return true;
        }
        return false;
    }

    public TagObject getTagObject(String group, String name) {
        for (TagObject iterator : this) {
            String iteratorGroup = iterator.getGroup();
            String iteratorName = iterator.getName();
            if (group.equals(iteratorGroup) && name.equals(iteratorName)) {
                return iterator;
            }
        }
        return null;
    }
    public TagObject getTagObject(TagObject tagObject) {
        String tagObjectGroup = tagObject.getGroup();
        String tagObjectName = tagObject.getName();
        return getTagObject(tagObjectGroup, tagObjectName);
    }
    public TagObject getTagObject(String groupAndName) {
        if (!groupAndName.contains(" - ")) return null;
        String[] split = groupAndName.split(" - ");
        String tagObjectGroup = split[0].trim();
        String tagObjectName = split[1].trim();
        return getTagObject(tagObjectGroup, tagObjectName);
    }
}
