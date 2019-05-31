package control.select;

import control.reload.Reload;
import database.list.DataObjectList;
import database.list.TagList;
import database.object.DataObject;
import database.object.TagObject;
import system.CommonUtil;
import system.Instances;

import java.util.ArrayList;

public class Select extends DataObjectList implements Instances {
    public boolean add(DataObject dataObject) {
        if (dataObject == null) return false;
        if (dataObject.getMergeID() != 0 && !tileView.getExpandedGroups().contains(dataObject.getMergeID())) {
            return this.addAll(dataObject.getMergeGroup());
        }
        if (super.add(dataObject)) {
            dataObject.generateTileEffect();
            reload.notifyChangeIn(Reload.Control.SELECT);
            return true;
        }
        return false;
    }
    public boolean addAll(ArrayList<DataObject> arrayList) {
        if (arrayList == null) return false;
        if (super.addAll(arrayList)) {
            arrayList.forEach(DataObject::generateTileEffect);
            reload.notifyChangeIn(Reload.Control.SELECT);
            return true;
        }
        return false;
    }
    public boolean remove(DataObject dataObject) {
        if (dataObject == null) return false;
        if (dataObject.getMergeID() != 0 && !tileView.getExpandedGroups().contains(dataObject.getMergeID())) {
            return this.removeAll(dataObject.getMergeGroup());
        }
        if (super.remove(dataObject)) {
            dataObject.generateTileEffect();
            reload.notifyChangeIn(Reload.Control.SELECT);
            return true;
        }
        return false;
    }
    public boolean removeAll(ArrayList<DataObject> arrayList) {
        if (arrayList == null) return false;
        if (super.removeAll(arrayList)) {
            arrayList.forEach(DataObject::generateTileEffect);
            reload.notifyChangeIn(Reload.Control.SELECT);
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
        select.set(dataObject);
        target.set(dataObject);
    }
    public void clear() {
        DataObjectList helper = new DataObjectList();
        helper.addAll(this);
        super.clear();
        helper.forEach(DataObject::generateTileEffect);
        reload.notifyChangeIn(Reload.Control.SELECT);
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

        reload.notifyChangeIn(Reload.Control.DATA, Reload.Control.INFO);
    }

    public void addTagObject(TagObject tagObject) {
        if (!tagObject.isEmpty()) {
            if (!mainInfoList.contains(tagObject)) {
                mainInfoList.add(tagObject);
            }

            DataObjectList selectHelper = new DataObjectList();
            selectHelper.addAll(select);

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
