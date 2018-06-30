package project.database.control;

import javafx.scene.control.TreeCell;
import project.control.FilterControl;
import project.control.change.ChangeEventControl;
import project.database.element.DataElement;
import project.database.element.TagElement;
import project.gui.component.LeftPane;
import project.gui.component.RightPane;
import project.gui.component.part.ColoredText;
import project.gui.custom.TagEditor;

import java.util.ArrayList;
import java.util.Comparator;

public abstract class TagElementControl {
    /* vars */
    private static final ArrayList<TagElement> tagElements = new ArrayList<>();

    /* public */
    public static void add(TagElement tagElement) {
        if (tagElement != null && !TagElementControl.contains(tagElement)) {
            TagElementControl.getTagElements().add(tagElement);
            TagElementControl.sortSimple();
            ChangeEventControl.requestReload(LeftPane.class);
        }
    }
    public static void remove(TagElement tagElement) {
        if (tagElement != null) {
            ArrayList<DataElement> dataElements = DataElementControl.getDataElementsLive();
            for (DataElement dataElement : dataElements) {
                dataElement.getTagElements().remove(tagElement);
            }
            FilterControl.unlistTagElement(tagElement);
            TagElementControl.getTagElements().remove(tagElement);
            ChangeEventControl.requestReloadGlobal();
        }
    }
    public static void edit(TagElement tagElement) {
        if (tagElement != null) {
            TagElement editTagElement = new TagEditor(tagElement).getResult();
            if (editTagElement != null) {
                String editGroup = editTagElement.getGroup();
                String editName = editTagElement.getName();
                TagElementControl.getTagElement(tagElement).setGroup(editGroup);
                TagElementControl.getTagElement(tagElement).setName(editName);
                TagElementControl.sortSimple();

                ChangeEventControl.requestReload(LeftPane.class, RightPane.class);
            }
        }
    }
    public static TagElement create() {
        TagElement newTagElement = new TagEditor().getResult();
        if (newTagElement.isEmpty()) return null;
        return newTagElement;
    }

    public static void sortSimple() {
        Comparator tagElementComparator = Comparator.comparing(TagElement::getGroupAndName);
        TagElementControl.getTagElements().sort(tagElementComparator);
    }
    public static void sortAll() {
        Comparator tagElementComparator = Comparator.comparing(TagElement::getGroupAndName);
        TagElementControl.getTagElements().sort(tagElementComparator);
        FilterControl.getTagElementWhitelist().sort(tagElementComparator);
        FilterControl.getTagElementBlacklist().sort(tagElementComparator);
    }
    public static void initialize() {
        ArrayList<DataElement> dataElements = DataElementControl.getDataElementsLive();
        for (DataElement dataElement : dataElements) {
            ArrayList<TagElement> tagElementsOfDataElement = dataElement.getTagElements();
            for (TagElement tagElementOfDataElement : tagElementsOfDataElement) {
                if (tagElementOfDataElement == null) continue;
                if (!TagElementControl.contains(tagElementOfDataElement)) {
                    TagElementControl.getTagElements().add(tagElementOfDataElement);
                } else {
                    int tagElementIndex = tagElementsOfDataElement.indexOf(tagElementOfDataElement);
                    TagElement tagElementInDatabase = TagElementControl.getTagElement(tagElementOfDataElement);
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
        for (TagElement tagElementFromDatabase : TagElementControl.getTagElements()) {
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
        for (TagElement tagElementFromDatabase : TagElementControl.getTagElements()) {
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
        return TagElementControl.getTagElement(tagElementGroup, tagElementName);
    }
    public static TagElement getTagElement(String groupAndName) {
        String tagElementGroup = groupAndName.split("-")[0].trim();
        String tagElementName = groupAndName.split("-")[1].trim();
        return TagElementControl.getTagElement(tagElementGroup, tagElementName);
    }
    public static TagElement getTagElement(TreeCell<ColoredText> treeCell) {
        try {
            ColoredText parentValue = treeCell.getTreeItem().getParent().getValue();
            String tagElementGroup = parentValue.getText();
            String tagElementName = treeCell.getText();
            return TagElementControl.getTagElement(tagElementGroup, tagElementName);
        } catch (NullPointerException e) {
            return null;
        }
    }

    public static ArrayList<String> getGroups() {
        ArrayList<String> groups = new ArrayList<>();
        for (TagElement tagElement : TagElementControl.getTagElements()) {
            if (!groups.contains(tagElement.getGroup())) {
                groups.add(tagElement.getGroup());
            }
        }
        groups.sort(Comparator.naturalOrder());
        return groups;
    }
    public static ArrayList<String> getNamesInGroup(String databaseTagElementGroup) {
        ArrayList<String> namesInGroup = new ArrayList<>();
        for (TagElement tagElement : TagElementControl.getTagElements()) {
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
