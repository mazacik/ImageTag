package control.select;

import control.reload.Reload;
import database.list.DataListMain;
import database.object.DataObject;
import database.list.InfoList;
import database.object.InfoObject;
import utils.MainUtil;

public class Select extends DataListMain implements MainUtil {
    public boolean add(DataObject dataObject) {
        if (dataObject == null) return false;
        if (super.add(dataObject)) {
            dataObject.generateTileEffect();
            reload.notifyChangeIn(Reload.Control.SELECTION);
            return true;
        }
        return false;
    }
    public boolean addAll(DataListMain dataListMain) {
        if (dataListMain == null) return false;
        if (super.addAll(dataListMain)) {
            dataListMain.forEach(DataObject::generateTileEffect);
            reload.notifyChangeIn(Reload.Control.SELECTION);
            return true;
        }
        return false;
    }
    public boolean remove(DataObject dataObject) {
        if (dataObject == null) return false;
        if (super.remove(dataObject)) {
            dataObject.generateTileEffect();
            reload.notifyChangeIn(Reload.Control.SELECTION);
            return true;
        }
        return false;
    }
    public void set(DataObject dataObject) {
        this.clear();
        this.add(dataObject);
    }
    public void clear() {
        DataListMain helper = new DataListMain();
        helper.addAll(this);
        super.clear();
        helper.forEach(DataObject::generateTileEffect);
        reload.notifyChangeIn(Reload.Control.SELECTION);
    }
    public void swapState(DataObject dataObject) {
        if (super.contains(dataObject)) {
            this.remove(dataObject);
        } else {
            this.add(dataObject);
        }
    }

    public void addTagObject(InfoObject infoObject) {
        if (!infoObject.isEmpty()) {
            if (!infoListMain.contains(infoObject)) {
                infoListMain.add(infoObject);
            }

            InfoList infoList;
            for (DataObject dataIterator : this) {
                infoList = dataIterator.getInfoList();
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
        for (DataObject dataObject : dataListMain) {
            if (dataObject.getInfoList().contains(infoObject)) {
                tagExists = true;
                break;
            }
        }
        if (!tagExists) {
            filter.unlistTagObject(infoObject);
            infoListMain.remove(infoObject);
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
