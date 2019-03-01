package database.list;

import control.reload.Reload;
import database.object.DataObject;
import database.object.InfoObject;
import javafx.scene.control.TreeCell;
import system.InstanceRepo;
import user_interface.node_factory.template.InfoObjectEditor;
import user_interface.single_instance.side.CustomTreeCell;

public class MainInfoList extends InfoList implements InstanceRepo {
    public void initialize() {
        for (DataObject dataIterator : mainListData) {
            InfoList infoList = dataIterator.getInfoList();
            for (InfoObject tagIterator : infoList) {
                if (this.contains(tagIterator)) {
                    infoList.set(infoList.indexOf(tagIterator), getInfoObject(tagIterator));
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
            //filter.apply(); //todo this needs some work. rn filter gets updated but fx tileview doesnt refresh
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
        InfoObject newInfoObject = new InfoObjectEditor(infoObject).getResult();
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
    public InfoObject getInfoObject(TreeCell<CustomTreeCell> treeCell) {
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
