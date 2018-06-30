package project.control;

import javafx.collections.ObservableList;
import project.database.DataElementDatabase;
import project.database.TagElementDatabase;
import project.database.element.DataElement;
import project.database.element.TagElement;
import project.gui.ChangeEventControl;
import project.gui.GUIStage;

import java.util.ArrayList;

public abstract class FilterControl {
    /* vars */
    private static final ArrayList<DataElement> validDataElements = new ArrayList<>();

    private static final ArrayList<TagElement> tagElementWhitelist = new ArrayList<>();
    private static final ArrayList<TagElement> tagElementBlacklist = new ArrayList<>();

    /* public */
    public static void refreshValidDataElements() {
        ArrayList<DataElement> dataElements = DataElementDatabase.getDataElements();
        validDataElements.clear();
        if (tagElementWhitelist.isEmpty() && tagElementBlacklist.isEmpty()) {
            validDataElements.addAll(dataElements);
        } else {
            for (DataElement dataElement : dataElements) {
                if (dataElement.getTagElements().containsAll(tagElementWhitelist)) {
                    boolean isDataElementValid = true;
                    for (TagElement tagElement : tagElementBlacklist) {
                        if (dataElement.getTagElements().contains(tagElement)) {
                            isDataElementValid = false;
                            break;
                        }
                    }
                    if (isDataElementValid) {
                        validDataElements.add(dataElement);
                    }
                }
            }
        }

        ChangeEventControl.requestReload(GUIStage.getPaneGallery());
    }
    public static void addTagElementToDataElementSelection(TagElement tagElement) {
        if (tagElement != null && !tagElement.isEmpty()) {
            if (!TagElementDatabase.contains(tagElement)) {
                TagElementDatabase.add(tagElement);
            }

            ArrayList<DataElement> dataElementSelection = SelectionControl.getDataElements();
            for (DataElement dataElement : dataElementSelection)
                if (!dataElement.getTagElements().contains(tagElement)) {
                    dataElement.getTagElements().add(tagElement);
                }

            ChangeEventControl.requestReload(GUIStage.getPaneRight());
        }
    }
    public static void removeTagElementSelectionFromDataElementSelection() {
        ArrayList<TagElement> tagElementsToRemove = new ArrayList<>();
        ObservableList<String> tagElementSelection = GUIStage.getPaneRight().getListView().getSelectionModel().getSelectedItems();
        for (String tagElement : tagElementSelection) {
            tagElementsToRemove.add(TagElementDatabase.getTagElement(tagElement));
        }

        ArrayList<DataElement> dataElementsSelected = SelectionControl.getDataElements();
        for (TagElement tagElement : tagElementsToRemove) {
            for (DataElement dataElement : dataElementsSelected) {
                dataElement.getTagElements().remove(tagElement);
            }

            boolean tagExists = false;
            ArrayList<DataElement> dataElements = DataElementDatabase.getDataElements();
            for (DataElement dataElement : dataElements) {
                if (dataElement.getTagElements().contains(tagElement)) {
                    tagExists = true;
                    break;
                }
            }
            if (!tagExists) {
                FilterControl.unlistTagElement(tagElement);
                TagElementDatabase.remove(tagElement);
            }
        }

        ChangeEventControl.requestReloadGlobal();
    }

    public static void whitelistGroup(String group) {
        ArrayList<String> namesInCategory = TagElementDatabase.getNamesInGroup(group);
        for (String name : namesInCategory) {
            TagElement tagElement = TagElementDatabase.getTagElement(group, name);
            FilterControl.whitelistTagElement(tagElement);
        }
    }
    public static void blacklistGroup(String group) {
        ArrayList<String> namesInCategory = TagElementDatabase.getNamesInGroup(group);
        for (String name : namesInCategory) {
            TagElement tagElement = TagElementDatabase.getTagElement(group, name);
            FilterControl.blacklistTagElement(tagElement);
        }
    }
    public static void unlistGroup(String group) {
        ArrayList<String> namesInCategory = TagElementDatabase.getNamesInGroup(group);
        for (String name : namesInCategory) {
            TagElement tagElement = TagElementDatabase.getTagElement(group, name);
            FilterControl.unlistTagElement(tagElement);
        }
    }

    public static void whitelistTagElement(TagElement tagElement) {
        if (tagElement != null && !FilterControl.isTagElementWhitelisted(tagElement)) {
            tagElementWhitelist.add(tagElement);
            tagElementBlacklist.remove(tagElement);
        }
    }
    public static void blacklistTagElement(TagElement tagElement) {
        if (tagElement != null && !FilterControl.isTagElementBlacklisted(tagElement)) {
            tagElementWhitelist.remove(tagElement);
            tagElementBlacklist.add(tagElement);
        }
    }
    public static void unlistTagElement(TagElement tagElement) {
        if (tagElement != null) {
            tagElementWhitelist.remove(tagElement);
            tagElementBlacklist.remove(tagElement);
        }
    }

    /* boolean */
    public static boolean isGroupWhitelisted(String group) {
        boolean value = true;
        for (String name : TagElementDatabase.getNamesInGroup(group)) {
            if (!FilterControl.isTagElementWhitelisted(group, name)) {
                value = false;
                break;
            }
        }
        return value;
    }
    public static boolean isGroupBlacklisted(String group) {
        boolean value = true;
        for (String name : TagElementDatabase.getNamesInGroup(group)) {
            if (!FilterControl.isTagElementBlacklisted(group, name)) {
                value = false;
                break;
            }
        }
        return value;
    }

    public static boolean isTagElementWhitelisted(TagElement tagElement) {
        if (tagElement == null) return false;
        return tagElementWhitelist.contains(tagElement);
    }
    public static boolean isTagElementBlacklisted(TagElement tagElement) {
        if (tagElement == null) return false;
        return tagElementBlacklist.contains(tagElement);
    }
    public static boolean isTagElementWhitelisted(String group, String name) {
        return tagElementWhitelist.contains(TagElementDatabase.getTagElement(group, name));
    }
    public static boolean isTagElementBlacklisted(String group, String name) {
        return tagElementBlacklist.contains(TagElementDatabase.getTagElement(group, name));
    }

    /* get */
    public static ArrayList<DataElement> getValidDataElements() {
        return validDataElements;
    }
    public static ArrayList<TagElement> getTagElementWhitelist() {
        return tagElementWhitelist;
    }
    public static ArrayList<TagElement> getTagElementBlacklist() {
        return tagElementBlacklist;
    }
}
