package project.database.control;

import javafx.scene.control.TreeCell;
import project.control.FilterControl;
import project.control.ReloadControl;
import project.database.element.DataCollection;
import project.database.element.DataObject;
import project.database.element.TagCollection;
import project.database.element.TagObject;
import project.gui.component.gallerypane.GalleryPane;
import project.gui.component.leftpane.ColoredText;
import project.gui.component.leftpane.LeftPane;
import project.gui.component.rightpane.RightPane;
import project.gui.custom.specific.TagEditor;

import java.util.ArrayList;
import java.util.Comparator;

public abstract class TagControl {
    /* vars */
    private static final TagCollection collection = new TagCollection();

    /* init */
    public static void initialize() {
        DataCollection dataCollection = DataControl.getCollection();
        for (DataObject dataIterator : dataCollection) {
            TagCollection tagCollection = dataIterator.getTagCollection();
            for (TagObject tagIterator : tagCollection) {
                if (!collection.contains(tagIterator)) {
                    collection.add(tagIterator);
                } else {
                    tagCollection.set(tagCollection.indexOf(tagIterator), TagControl.getTagObject(tagIterator));
                }
            }
        }
        FilterControl.getCollection().setAll(dataCollection);
    }

    /* public */
    public static boolean add(TagObject tagObject) {
        if (collection.add(tagObject)) {
            //does this not need FilterControl.doWork() ?
            ReloadControl.reload(LeftPane.class, RightPane.class);
            return true;
        }
        return false;
    }
    public static boolean remove(TagObject tagObject) {
        if (collection.remove(tagObject)) {
            FilterControl.removeTagObject(tagObject);
            FilterControl.doWork();
            ReloadControl.reload(LeftPane.class, GalleryPane.class, RightPane.class);
            return true;
        }
        return false;
    }
    public static boolean edit(TagObject tagObject) {
        TagObject newTagObject = new TagEditor(tagObject).getResult();
        if (newTagObject != null) {
            TagControl.getTagObject(tagObject).setValue(newTagObject.getGroup(), newTagObject.getName());
            // ^ this relies on the value to change everywhere
            collection.sort();
            ReloadControl.reload(LeftPane.class, RightPane.class);
            return true;
        }
        return false;
    }

    /* get */
    public static TagObject getTagObject(String group, String name) {
        for (TagObject iterator : collection) {
            String iteratorGroup = iterator.getGroup();
            String iteratorName = iterator.getName();
            if (group.equals(iteratorGroup) && name.equals(iteratorName)) {
                return iterator;
            }
        }
        return null;
    }
    public static TagObject getTagObject(TagObject tagObject) {
        String tagObjectGroup = tagObject.getGroup();
        String tagObjectName = tagObject.getName();
        return TagControl.getTagObject(tagObjectGroup, tagObjectName);
    }
    public static TagObject getTagObject(String groupAndName) {
        String[] split = groupAndName.split("-");
        String tagElementGroup = split[0].trim();
        String tagElementName = split[1].trim();
        return TagControl.getTagObject(tagElementGroup, tagElementName);
    }
    public static TagObject getTagObject(TreeCell<ColoredText> treeCell) {
        if (treeCell == null) return null;
        String tagElementGroup;
        try {
            tagElementGroup = treeCell.getTreeItem().getParent().getValue().getText();
        } catch (NullPointerException e) {
            return null;
        }
        String tagElementName = treeCell.getText();
        return TagControl.getTagObject(tagElementGroup, tagElementName);
    }

    public static ArrayList<String> getGroups() {
        ArrayList<String> groups = new ArrayList<>();
        for (TagObject iterator : collection) {
            if (!groups.contains(iterator.getGroup())) {
                groups.add(iterator.getGroup());
            }
        }
        groups.sort(Comparator.naturalOrder());
        return groups;
    }
    public static ArrayList<String> getNames(String group) {
        ArrayList<String> names = new ArrayList<>();
        for (TagObject iterator : collection) {
            String iteratorGroup = iterator.getGroup();
            String iteratorName = iterator.getName();
            if (iteratorGroup.equals(group) && !names.contains(iteratorName)) {
                names.add(iteratorName);
            }
        }
        return names;
    }

    public static TagCollection getCollection() {
        return collection;
    }
}
