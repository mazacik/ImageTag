package database.list;

import control.reload.Reload;
import database.object.DataObject;
import database.object.InfoObject;
import javafx.scene.control.TreeCell;
import system.InstanceRepo;
import user_interface.factory.stage.InfoObjectEditStage;
import user_interface.singleton.side.InfoListCell;

public class InfoObjectListMain extends InfoObjectList implements InstanceRepo {
    public void initialize() {
        for (DataObject dataIterator : mainDataList) {
            InfoObjectList infoObjectList = dataIterator.getInfoObjectList();
            for (InfoObject tagIterator : infoObjectList) {
                if (this.contains(tagIterator)) {
                    infoObjectList.set(infoObjectList.indexOf(tagIterator), getInfoObject(tagIterator));
                } else {
                    this.add(tagIterator);
                }
            }
        }
        super.sort();
    }

    public boolean add(InfoObject infoObject) {
        if (infoObject == null) return false;
        if (super.add(infoObject)) {
            reload.notifyChangeIn(Reload.Control.INFO);
            return true;
        }
        return false;
    }
    public boolean remove(InfoObject infoObject) {
        if (infoObject == null) return false;
        if (super.remove(infoObject)) {
            //filter.apply();
            reload.notifyChangeIn(Reload.Control.INFO);
            return true;
        }
        return false;
    }
    public boolean edit(InfoObject infoObject) {
        if (infoObject == null) return false;
        InfoObject newInfoObject = new InfoObjectEditStage(infoObject).getResult();
        if (newInfoObject != null) {
            infoObject.setValue(newInfoObject.getGroup(), newInfoObject.getName());
            super.sort();
            reload.notifyChangeIn(Reload.Control.INFO);
            return true;
        }
        return false;
    }

    public InfoObject getInfoObject(String group, String name) {
        for (InfoObject iterator : this) {
            String iteratorGroup = iterator.getGroup();
            String iteratorName = iterator.getName();
            if (group.equals(iteratorGroup) && name.equals(iteratorName)) {
                return iterator;
            }
        }
        return null;
    }
    public InfoObject getInfoObject(InfoObject infoObject) {
        String tagObjectGroup = infoObject.getGroup();
        String tagObjectName = infoObject.getName();
        return getInfoObject(tagObjectGroup, tagObjectName);
    }
    public InfoObject getInfoObject(String groupAndName) {
        String[] split = groupAndName.split("-");
        String tagObjectGroup = split[0].trim();
        String tagObjectName = split[1].trim();
        return getInfoObject(tagObjectGroup, tagObjectName);
    }
    public InfoObject getInfoObject(TreeCell<InfoListCell> treeCell) {
        if (treeCell == null) return null;
        String tagObjectGroup;
        try {
            tagObjectGroup = treeCell.getTreeItem().getParent().getValue().getText();
        } catch (NullPointerException e) {
            return null;
        }
        String tagObjectName = treeCell.getText();
        return getInfoObject(tagObjectGroup, tagObjectName);
    }
}
