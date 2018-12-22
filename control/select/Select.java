package control.select;

import control.reload.Reload;
import database.list.BaseListInfo;
import database.list.MainListData;
import database.object.DataObject;
import database.object.InfoObject;
import utils.MainUtil;

public class Select extends MainListData implements MainUtil {
    public boolean add(DataObject dataObject) {
        if (dataObject == null) return false;
        if (super.add(dataObject)) {
            dataObject.generateTileEffect();
            reload.notifyChangeIn(Reload.Control.SELECTION);
            return true;
        }
        return false;
    }
    public boolean addAll(MainListData mainListData) {
        if (mainListData == null) return false;
        if (super.addAll(mainListData)) {
            mainListData.forEach(DataObject::generateTileEffect);
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
        MainListData helper = new MainListData();
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
            if (!mainListInfo.contains(infoObject)) {
                mainListInfo.add(infoObject);
            }

            BaseListInfo baseListInfo;
            for (DataObject dataIterator : this) {
                baseListInfo = dataIterator.getBaseListInfo();
                if (!baseListInfo.contains(infoObject)) {
                    baseListInfo.add(infoObject);
                }
            }
        }
    }
    public void removeTagObject(InfoObject infoObject) {
        for (DataObject dataObject : this) {
            dataObject.getBaseListInfo().remove(infoObject);
        }

        boolean tagExists = false;
        for (DataObject dataObject : mainListData) {
            if (dataObject.getBaseListInfo().contains(infoObject)) {
                tagExists = true;
                break;
            }
        }
        if (!tagExists) {
            filter.unlistTagObject(infoObject);
            mainListInfo.remove(infoObject);
        }
    }

    public BaseListInfo getIntersectingTags() {
        if (this.size() < 1) return new BaseListInfo();

        BaseListInfo intersectingTags = new BaseListInfo();
        DataObject lastObject = this.get(this.size() - 1);
        for (InfoObject infoObject : this.get(0).getBaseListInfo()) {
            for (DataObject dataObject : this) {
                if (dataObject.getBaseListInfo().contains(infoObject)) {
                    if (dataObject.equals(lastObject)) {
                        intersectingTags.add(infoObject);
                    }
                } else break;
            }
        }
        return intersectingTags;
    }
    public BaseListInfo getSharedTags() {
        if (this.size() < 1) return new BaseListInfo();

        BaseListInfo sharedTags = new BaseListInfo();
        for (DataObject dataObject : this) {
            for (InfoObject infoObject : dataObject.getBaseListInfo()) {
                if (!sharedTags.contains(infoObject)) {
                    sharedTags.add(infoObject);
                }
            }
        }
        return sharedTags;
    }
}
