package control;

import database.list.ObjectList;
import database.list.TagList;
import database.object.DataObject;
import database.object.TagObject;
import lifecycle.InstanceManager;
import system.CommonUtil;

import java.util.ArrayList;

public class Select extends ObjectList {
    public Select() {

    }

    public boolean add(DataObject dataObject) {
        if (dataObject == null) return false;
        if (dataObject.getMergeID() != 0 && !InstanceManager.getGalleryPane().getExpandedGroups().contains(dataObject.getMergeID())) {
            return this.addAll(dataObject.getMergeGroup());
        }
        if (super.add(dataObject)) {
            dataObject.generateTileEffect();
            InstanceManager.getReload().notifyChangeIn(Reload.Control.SELECT);
            return true;
        }
        return false;
    }
    public boolean addAll(ArrayList<DataObject> arrayList) {
        if (arrayList == null) return false;
        if (super.addAll(arrayList)) {
            arrayList.forEach(DataObject::generateTileEffect);
            InstanceManager.getReload().notifyChangeIn(Reload.Control.SELECT);
            return true;
        }
        return false;
    }
    public boolean remove(DataObject dataObject) {
        if (dataObject == null) return false;
        if (dataObject.getMergeID() != 0 && !InstanceManager.getGalleryPane().getExpandedGroups().contains(dataObject.getMergeID())) {
            return this.removeAll(dataObject.getMergeGroup());
        }
        if (super.remove(dataObject)) {
            dataObject.generateTileEffect();
            InstanceManager.getReload().notifyChangeIn(Reload.Control.SELECT);
            return true;
        }
        return false;
    }
    public boolean removeAll(ArrayList<DataObject> arrayList) {
        if (arrayList == null) return false;
        if (super.removeAll(arrayList)) {
            arrayList.forEach(DataObject::generateTileEffect);
            InstanceManager.getReload().notifyChangeIn(Reload.Control.SELECT);
            return true;
        }
        return false;
    }

    public void set(DataObject dataObject) {
        this.clear();
        this.add(dataObject);
    }
    public void setRandom() {
        DataObject dataObject = CommonUtil.getRandomDataObject();
        InstanceManager.getSelect().set(dataObject);
        InstanceManager.getTarget().set(dataObject);
    }
    public void clear() {
        ObjectList helper = new ObjectList();
        helper.addAll(this);
        super.clear();
        helper.forEach(DataObject::generateTileEffect);
        InstanceManager.getReload().notifyChangeIn(Reload.Control.SELECT);
    }
    public void swapState(DataObject dataObject) {
        if (super.contains(dataObject)) {
            this.remove(dataObject);
        } else {
            this.add(dataObject);
        }
    }
    public void merge() {
        TagList tagList = new TagList();
        for (DataObject dataObject : this) {
            for (TagObject tagObject : dataObject.getTagList()) {
                if (!tagList.contains(tagObject)) {
                    tagList.add(tagObject);
                }
            }
        }

        int mergeID = CommonUtil.getRandomHash();
        for (DataObject dataObject : this) {
            dataObject.setMergeID(mergeID);
            dataObject.setTagList(tagList);
        }

        InstanceManager.getReload().notifyChangeIn(Reload.Control.DATA, Reload.Control.INFO);
    }

    public void addTagObject(TagObject tagObject) {
        if (!tagObject.isEmpty()) {
            if (!InstanceManager.getTagListMain().contains(tagObject)) {
                InstanceManager.getTagListMain().add(tagObject);
            }

            ObjectList selectHelper = new ObjectList();
            selectHelper.addAll(InstanceManager.getSelect());

            TagList tagList;
            for (DataObject dataObject : selectHelper) {
                tagList = dataObject.getTagList();
                if (!tagList.contains(tagObject)) {
                    tagList.add(tagObject);
                }
            }
        }
    }
    public void removeTagObject(TagObject tagObject) {
        for (DataObject dataObject : this) {
            dataObject.getTagList().remove(tagObject);
        }
    }
}
