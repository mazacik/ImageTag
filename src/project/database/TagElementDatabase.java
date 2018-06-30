package project.database;

import javafx.scene.control.TreeCell;
import project.control.FilterControl;
import project.database.element.DataElement;
import project.database.element.TagElement;
import project.gui.ChangeEventControl;
import project.gui.ChangeEventListener;
import project.gui.GUIStage;
import project.gui.component.part.ColoredText;
import project.gui.stage.TagEditor;

import java.util.ArrayList;
import java.util.Comparator;

//todo check
public abstract class TagElementDatabase {
    /* change */
    private static final ArrayList<ChangeEventListener> changeListeners = new ArrayList<>();
    public static ArrayList<ChangeEventListener> getChangeListeners() {
        return changeListeners;
    }

    /* vars */
    private static final ArrayList<TagElement> tagElements = new ArrayList<>();

    /* public */
    public static void add(TagElement tagElement) {
        if (tagElement != null && !TagElementDatabase.contains(tagElement)) {
            TagElementDatabase.getTagElements().add(tagElement);
            TagElementDatabase.sort();
            ChangeEventControl.requestReload(GUIStage.getPaneLeft());
        }
    }
    public static void remove(TagElement tagElement) {
        if (tagElement != null) {
            ArrayList<DataElement> dataElements = DataElementDatabase.getDataElements();
            for (DataElement dataElement : dataElements) {
                dataElement.getTagElements().remove(tagElement);
            }
            FilterControl.unlistTagElement(tagElement);
            TagElementDatabase.getTagElements().remove(tagElement);
            ChangeEventControl.requestReloadGlobal();
        }
    }
    public static void edit(TagElement tagElement) {
        if (tagElement != null) {
            TagElement editTagElement = new TagEditor(tagElement).getResult();
            if (editTagElement != null) {
                String editGroup = editTagElement.getGroup();
                String editName = editTagElement.getName();
                TagElementDatabase.getTagElement(tagElement).setGroup(editGroup);
                TagElementDatabase.getTagElement(tagElement).setName(editName);

                ChangeEventControl.requestReload(GUIStage.getPaneLeft(), GUIStage.getPaneRight());
            }
        }
    }
    public static TagElement create() {
        TagElement newTagElement = new TagEditor().getResult();
        if (newTagElement.isEmpty()) return null;
        return newTagElement;
    }

    public static void sort() {
        Comparator comparator = Comparator.comparing(TagElement::getGroupAndName);
        TagElementDatabase.getTagElements().sort(comparator);
        FilterControl.getTagElementWhitelist().sort(comparator);
        FilterControl.getTagElementBlacklist().sort(comparator);
    }
    public static void initialize() {
        ArrayList<DataElement> dataElements = DataElementDatabase.getDataElements();
        for (DataElement dataElement : dataElements) {
            ArrayList<TagElement> tagElementsOfDataElement = dataElement.getTagElements();
            for (TagElement tagElementOfDataElement : tagElementsOfDataElement) {
                if (tagElementOfDataElement == null) continue;
                if (!TagElementDatabase.contains(tagElementOfDataElement)) {
                    TagElementDatabase.getTagElements().add(tagElementOfDataElement);
                } else {
                    int tagElementIndex = tagElementsOfDataElement.indexOf(tagElementOfDataElement);
                    TagElement tagElementInDatabase = TagElementDatabase.getTagElement(tagElementOfDataElement);
                    tagElementsOfDataElement.set(tagElementIndex, tagElementInDatabase);
                }
            }
        }

        FilterControl.getValidDataElements().addAll(dataElements);
    }

    /* boolean */
    public static boolean contains(TagElement tagElement) {
        String tagElementGroup = tagElement.getGroup();
        String tagElementName = tagElement.getName();
        for (TagElement tagElementFromDatabase : TagElementDatabase.getTagElements()) {
            String databaseTagElementGroup = tagElementFromDatabase.getGroup();
            String databaseTagElementName = tagElementFromDatabase.getName();
            if (tagElementGroup.equals(databaseTagElementGroup) && tagElementName.equals(databaseTagElementName)) {
                return true;
            }
        }
        return false;
    }

    /* get */
    public static TagElement getTagElement(String tagElementGroup, String tagElementName) {
        for (TagElement tagElementFromDatabase : TagElementDatabase.getTagElements()) {
            String databaseTagElementGroup = tagElementFromDatabase.getGroup();
            String databaseTagElementName = tagElementFromDatabase.getName();
            if (tagElementGroup.equals(databaseTagElementGroup) && tagElementName.equals(databaseTagElementName)) {
                return tagElementFromDatabase;
            }
        }
        return null;
    }
    public static TagElement getTagElement(TagElement tagElement) {
        String tagElementGroup = tagElement.getGroup();
        String tagElementName = tagElement.getName();
        return TagElementDatabase.getTagElement(tagElementGroup, tagElementName);
    }
    public static TagElement getTagElement(String groupAndName) {
        String tagElementGroup = groupAndName.split("-")[0].trim();
        String tagElementName = groupAndName.split("-")[1].trim();
        return TagElementDatabase.getTagElement(tagElementGroup, tagElementName);
    }
    public static TagElement getTagElement(TreeCell<ColoredText> treeCell) {
        try {
            ColoredText parentValue = treeCell.getTreeItem().getParent().getValue();
            String tagElementGroup = parentValue.getText();
            String tagElementName = treeCell.getText();
            return TagElementDatabase.getTagElement(tagElementGroup, tagElementName);
        } catch (NullPointerException e) {
            return null;
        }
    }

    public static ArrayList<String> getGroups() {
        ArrayList<String> groups = new ArrayList<>();
        for (TagElement tagElement : TagElementDatabase.getTagElements()) {
            if (!groups.contains(tagElement.getGroup())) {
                groups.add(tagElement.getGroup());
            }
        }
        groups.sort(Comparator.naturalOrder());
        return groups;
    }
    public static ArrayList<String> getNamesInGroup(String databaseTagElementGroup) {
        ArrayList<String> namesInGroup = new ArrayList<>();
        for (TagElement tagElement : TagElementDatabase.getTagElements()) {
            String tagElementGroup = tagElement.getGroup();
            String tagElementName = tagElement.getName();
            if (tagElementGroup.equals(databaseTagElementGroup) && !namesInGroup.contains(tagElementName)) {
                namesInGroup.add(tagElementName);
            }
        }
        return namesInGroup;
    }

    public static ArrayList<TagElement> getTagElements() {
        return tagElements;
    }
}
