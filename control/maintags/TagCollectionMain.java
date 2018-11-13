package control.maintags;

import database.object.DataObject;
import database.object.TagCollection;
import database.object.TagObject;
import gui.component.GUINode;
import gui.component.leftpane.ColoredText;
import gui.custom.specific.TagEditor;
import javafx.scene.control.TreeCell;
import utils.MainUtil;

import java.util.ArrayList;
import java.util.Comparator;

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
            reload.queue(GUINode.LEFTPANE);
            return true;
        }
        return false;
    }
    public boolean remove(TagObject tagObject) {
        if (tagObject == null) return false;
        if (super.remove(tagObject)) {
            filter.unlistTagObject(tagObject);
            filter.apply();
            reload.queue(GUINode.LEFTPANE, GUINode.GALLERYPANE);
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
            reload.queue(GUINode.LEFTPANE, GUINode.RIGHTPANE);
            return true;
        }
        return false;
    }
    public boolean contains(TagObject tagObject) {
        return super.contains(tagObject);
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
    public TagObject getTagObject(TreeCell<ColoredText> treeCell) {
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

    public ArrayList<String> getGroups() {
        ArrayList<String> groups = new ArrayList<>();
        for (TagObject iterator : this) {
            if (!groups.contains(iterator.getGroup())) {
                groups.add(iterator.getGroup());
            }
        }
        groups.sort(Comparator.naturalOrder());
        return groups;
    }
    public ArrayList<String> getNames(String group) {
        ArrayList<String> names = new ArrayList<>();
        for (TagObject iterator : this) {
            String iteratorGroup = iterator.getGroup();
            String iteratorName = iterator.getName();
            if (iteratorGroup.equals(group) && !names.contains(iteratorName)) {
                names.add(iteratorName);
            }
        }
        return names;
    }
}
