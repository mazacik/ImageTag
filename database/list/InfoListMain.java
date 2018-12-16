package database.list;

import control.reload.Reload;
import database.object.DataObject;
import database.object.InfoObject;
import gui.singleton.side.CustomTreeCell;
import gui.template.specific.TagEditor;
import javafx.scene.control.TreeCell;
import utils.MainUtil;

public class InfoListMain extends InfoList implements MainUtil {
    public void initialize() {
        for (DataObject dataIterator : dataListMain) {
            InfoList infoList = dataIterator.getInfoList();
            for (InfoObject tagIterator : infoList) {
                if (this.contains(tagIterator)) {
                    infoList.set(infoList.indexOf(tagIterator), getTagObject(tagIterator));
                } else {
                    this.add(tagIterator);
                }
            }
        }
        super.sort();
    }

    public boolean add(InfoObject infoObject) {
        if (infoObject == null) return false;
        reload.notifyChangeIn(Reload.Control.TAGS);
        return super.add(infoObject);
    }
    public boolean remove(InfoObject infoObject) {
        if (infoObject == null) return false;
        if (super.remove(infoObject)) {
            filter.unlistTagObject(infoObject);
            filter.apply();
            reload.notifyChangeIn(Reload.Control.TAGS);
            return true;
        }
        return false;
    }
    public boolean edit(InfoObject infoObject) {
        if (infoObject == null) return false;
        InfoObject newInfoObject = new TagEditor(infoObject).getResult();
        if (newInfoObject != null) {
            infoObject.setValue(newInfoObject.getGroup(), newInfoObject.getName());
            super.sort();
            reload.notifyChangeIn(Reload.Control.TAGS);
            return true;
        }
        return false;
    }

    public InfoObject getTagObject(String group, String name) {
        for (InfoObject iterator : this) {
            String iteratorGroup = iterator.getGroup();
            String iteratorName = iterator.getName();
            if (group.equals(iteratorGroup) && name.equals(iteratorName)) {
                return iterator;
            }
        }
        return null;
    }
    public InfoObject getTagObject(InfoObject infoObject) {
        String tagObjectGroup = infoObject.getGroup();
        String tagObjectName = infoObject.getName();
        return getTagObject(tagObjectGroup, tagObjectName);
    }
    public InfoObject getTagObject(String groupAndName) {
        String[] split = groupAndName.split("-");
        String tagObjectGroup = split[0].trim();
        String tagObjectName = split[1].trim();
        return getTagObject(tagObjectGroup, tagObjectName);
    }
    public InfoObject getTagObject(TreeCell<CustomTreeCell> treeCell) {
        if (treeCell == null) return null;
        String tagObjectGroup;
        try {
            tagObjectGroup = treeCell.getTreeItem().getParent().getValue().getText();
        } catch (NullPointerException e) {
            return null;
        }
        String tagObjectName = treeCell.getText();
        return getTagObject(tagObjectGroup, tagObjectName);
    }
}
