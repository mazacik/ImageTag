package control.maintags;

import database.object.DataObject;
import database.object.TagCollection;
import database.object.TagObject;
import gui.component.ColorText;
import gui.component.NodeEnum;
import gui.template.specific.TagEditor;
import javafx.scene.control.TreeCell;
import utils.MainUtil;

public class TagCollectionMain extends TagCollection implements MainUtil {
    public void initialize() {
        for (DataObject dataIterator : mainData) {
            TagCollection tagCollection = dataIterator.getTagCollection();
            for (TagObject tagIterator : tagCollection) {
                if (this.contains(tagIterator)) {
                    tagCollection.set(tagCollection.indexOf(tagIterator), getTagObject(tagIterator));
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
            reload.queue(NodeEnum.LEFTPANE);
            return true;
        }
        return false;
    }
    public boolean remove(TagObject tagObject) {
        if (tagObject == null) return false;
        if (super.remove(tagObject)) {
            filter.unlistTagObject(tagObject);
            filter.apply();
            reload.queue(NodeEnum.LEFTPANE, NodeEnum.GALLERYPANE);
            return true;
        }
        return false;
    }
    public boolean edit(TagObject tagObject) {
        if (tagObject == null) return false;
        TagObject newTagObject = new TagEditor(tagObject).getResult();
        if (newTagObject != null) {
            tagObject.setValue(newTagObject.getGroup(), newTagObject.getName());
            super.sort();
            reload.queue(NodeEnum.LEFTPANE);
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
        String[] split = groupAndName.split("-");
        String tagObjectGroup = split[0].trim();
        String tagObjectName = split[1].trim();
        return getTagObject(tagObjectGroup, tagObjectName);
    }
    public TagObject getTagObject(TreeCell<ColorText> treeCell) {
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
