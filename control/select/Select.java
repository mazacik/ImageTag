package control.select;

import control.reload.Reload;
import database.list.InfoList;
import database.list.MainListData;
import database.object.DataObject;
import database.object.InfoObject;
import system.CommonUtil;
import system.InstanceRepo;

import java.util.ArrayList;

public class Select extends MainListData implements InstanceRepo {
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
        MainListData helper = new MainListData();
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
        InfoList infoList = new InfoList();
        for (DataObject dataObject : this) {
            for (InfoObject infoObject : dataObject.getInfoList()) {
                if (!infoList.contains(infoObject)) {
                    infoList.add(infoObject);
                }
            }
        }

        int mergeID = CommonUtil.getHash();
        for (DataObject dataObject : this) {
            dataObject.setMergeID(mergeID);
            dataObject.setInfoList(infoList);
        }

        reload.notifyChangeIn(Reload.Control.DATA);
    }

    public void addTagObject(InfoObject infoObject) {
        if (!infoObject.isEmpty()) {
            if (!mainListInfo.contains(infoObject)) {
                mainListInfo.add(infoObject);
            }

            MainListData selectHelper = new MainListData();
            selectHelper.addAll(select);

            InfoList infoList;
            for (DataObject dataObject : selectHelper) {
                infoList = dataObject.getInfoList();
                if (!infoList.contains(infoObject)) {
                    infoList.add(infoObject);
                }
            }
        }
    }
    public void removeTagObject(InfoObject infoObject) {
        for (DataObject dataObject : this) {
            dataObject.getInfoList().remove(infoObject);
        }

        boolean tagExists = false;
        for (DataObject dataObject : mainListData) {
            if (dataObject.getInfoList().contains(infoObject)) {
                tagExists = true;
                break;
            }
        }
        if (!tagExists) {
            filter.unlistTagObject(infoObject);
            mainListInfo.remove(infoObject);
        }
    }

    public InfoList getIntersectingTags() {
        if (this.size() < 1) return new InfoList();

        InfoList intersectingTags = new InfoList();
        DataObject lastObject = this.get(this.size() - 1);
        for (InfoObject infoObject : this.get(0).getInfoList()) {
            for (DataObject dataObject : this) {
                if (dataObject.getInfoList().contains(infoObject)) {
                    if (dataObject.equals(lastObject)) {
                        intersectingTags.add(infoObject);
                    }
                } else break;
            }
        }
        return intersectingTags;
    }
    public InfoList getSharedTags() {
        if (this.size() < 1) return new InfoList();

        InfoList sharedTags = new InfoList();
        for (DataObject dataObject : this) {
            for (InfoObject infoObject : dataObject.getInfoList()) {
                if (!sharedTags.contains(infoObject)) {
                    sharedTags.add(infoObject);
                }
            }
        }
        return sharedTags;
    }
}
