package control.select;

import control.reload.Reload;
import database.list.DataObjectList;
import database.list.InfoObjectList;
import database.object.DataObject;
import database.object.InfoObject;
import system.CommonUtil;
import system.InstanceRepo;

import java.util.ArrayList;

public class Select extends DataObjectList implements InstanceRepo {
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
        InfoObjectList infoObjectList = new InfoObjectList();
        for (DataObject dataObject : this) {
            for (InfoObject infoObject : dataObject.getInfoObjectList()) {
                if (!infoObjectList.contains(infoObject)) {
                    infoObjectList.add(infoObject);
                }
            }
        }

        int mergeID = CommonUtil.getHash();
        for (DataObject dataObject : this) {
            dataObject.setMergeID(mergeID);
            dataObject.setInfoObjectList(infoObjectList);
        }

        reload.notifyChangeIn(Reload.Control.DATA, Reload.Control.INFO);
    }

    public void addTagObject(InfoObject infoObject) {
        if (!infoObject.isEmpty()) {
            if (!mainInfoList.contains(infoObject)) {
                mainInfoList.add(infoObject);
            }

            DataObjectList selectHelper = new DataObjectList();
            selectHelper.addAll(select);

            InfoObjectList infoObjectList;
            for (DataObject dataObject : selectHelper) {
                infoObjectList = dataObject.getInfoObjectList();
                if (!infoObjectList.contains(infoObject)) {
                    infoObjectList.add(infoObject);
                }
            }
        }
    }
    public void removeTagObject(InfoObject infoObject) {
        for (DataObject dataObject : this) {
            dataObject.getInfoObjectList().remove(infoObject);
        }

        boolean tagExists = false;
        for (DataObject dataObject : mainDataList) {
            if (dataObject.getInfoObjectList().contains(infoObject)) {
                tagExists = true;
                break;
            }
        }
        if (!tagExists) {
            filter.unlistTagObject(infoObject);
            mainInfoList.remove(infoObject);
        }
    }

    public InfoObjectList getIntersectingTags() {
        if (this.size() < 1) return new InfoObjectList();

        InfoObjectList intersectingTags = new InfoObjectList();
        DataObject lastObject = this.get(this.size() - 1);
        for (InfoObject infoObject : this.get(0).getInfoObjectList()) {
            for (DataObject dataObject : this) {
                if (dataObject.getInfoObjectList().contains(infoObject)) {
                    if (dataObject.equals(lastObject)) {
                        intersectingTags.add(infoObject);
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
            for (InfoObject infoObject : dataObject.getInfoObjectList()) {
                if (!sharedTags.contains(infoObject)) {
                    sharedTags.add(infoObject);
                }
            }
        }
        return sharedTags;
    }
}
