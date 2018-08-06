package project.control;

import javafx.collections.ObservableList;
import project.database.control.DataObjectControl;
import project.database.control.TagElementControl;
import project.database.element.DataObject;
import project.database.element.TagElement;
import project.enums.FilterCollection;
import project.gui.component.gallerypane.GalleryPane;
import project.gui.component.leftpane.LeftPane;
import project.gui.component.rightpane.RightPane;
import project.gui.component.toppane.TopPane;

import java.util.ArrayList;

public abstract class FilterControl {
    /* vars */
    private static final ArrayList<DataObject> dataObjectsValid = new ArrayList<>();

    private static final ArrayList<TagElement> tagElementWhitelist = new ArrayList<>();
    private static final ArrayList<TagElement> tagElementBlacklist = new ArrayList<>();

    private static FilterCollection currentFilter = FilterCollection.SHOW_EVERYTHING;

    /* public */
    public static void validDataElementsRefresh() {
        ArrayList<DataObject> dataObjects = DataObjectControl.getDataElementsCopy();

        currentFilter.activate();
        dataObjectsValid.clear();

        if (tagElementWhitelist.isEmpty() && tagElementBlacklist.isEmpty()) dataObjectsValid.addAll(dataObjects);
        else {
            for (DataObject dataObject : dataObjects) {
                ArrayList<TagElement> dataElementTagElements = dataObject.getTagElements();
                if (tagElementWhitelist.isEmpty() || dataElementTagElements.containsAll(tagElementWhitelist)) {
                    boolean isDataElementValid = true;
                    for (TagElement tagElement : tagElementBlacklist) {
                        if (dataElementTagElements.contains(tagElement)) {
                            isDataElementValid = false;
                            break;
                        }
                    }
                    if (isDataElementValid) dataObjectsValid.add(dataObject);
                }
            }
        }

        ReloadControl.requestComponentReload(GalleryPane.class);
    }
    public static void addTagElementToDataElementSelection(TagElement tagElement) {
        if (tagElement != null && !tagElement.isEmpty()) {
            if (!TagElementControl.contains(tagElement)) {
                TagElementControl.add(tagElement);
            }

            ArrayList<DataObject> dataObjectSelection = SelectionControl.getDataObjects();
            for (DataObject dataObject : dataObjectSelection)
                if (!dataObject.getTagElements().contains(tagElement)) {
                    dataObject.getTagElements().add(tagElement);
                }
        }
    }
    public static void removeTagElementSelectionFromDataElementSelection() {
        ArrayList<TagElement> tagElementsToRemove = new ArrayList<>();
        ObservableList<String> tagElementSelection = RightPane.getListView().getSelectionModel().getSelectedItems();
        for (String tagElement : tagElementSelection) {
            tagElementsToRemove.add(TagElementControl.getTagElement(tagElement));
        }

        ArrayList<DataObject> dataElementsSelected = SelectionControl.getDataObjects();
        for (TagElement tagElement : tagElementsToRemove) {
            for (DataObject dataObject : dataElementsSelected) {
                dataObject.getTagElements().remove(tagElement);
            }

            boolean tagExists = false;
            ArrayList<DataObject> dataObjects = DataObjectControl.getDataElementsCopy();
            for (DataObject dataObject : dataObjects) {
                if (dataObject.getTagElements().contains(tagElement)) {
                    tagExists = true;
                    break;
                }
            }
            if (!tagExists) {
                FilterControl.unlistTagElement(tagElement);
                TagElementControl.remove(tagElement);
                ReloadControl.requestComponentReload(LeftPane.class);
            }
        }

        ReloadControl.requestComponentReload(RightPane.class);
    }

    public static void whitelistGroup(String group) {
        ArrayList<String> namesInCategory = TagElementControl.getNamesInGroup(group);
        for (String name : namesInCategory) {
            TagElement tagElement = TagElementControl.getTagElement(group, name);
            FilterControl.whitelistTagElement(tagElement);
        }
    }
    public static void blacklistGroup(String group) {
        ArrayList<String> namesInCategory = TagElementControl.getNamesInGroup(group);
        for (String name : namesInCategory) {
            TagElement tagElement = TagElementControl.getTagElement(group, name);
            FilterControl.blacklistTagElement(tagElement);
        }
    }
    public static void unlistGroup(String group) {
        ArrayList<String> namesInCategory = TagElementControl.getNamesInGroup(group);
        for (String name : namesInCategory) {
            TagElement tagElement = TagElementControl.getTagElement(group, name);
            FilterControl.unlistTagElement(tagElement);
        }
    }

    public static void whitelistTagElement(TagElement tagElement) {
        if (tagElement != null && !FilterControl.isTagElementWhitelisted(tagElement)) {
            tagElementWhitelist.add(tagElement);
            tagElementBlacklist.remove(tagElement);
            currentFilter = FilterCollection.CUSTOM;
        }
    }
    public static void blacklistTagElement(TagElement tagElement) {
        if (tagElement != null && !FilterControl.isTagElementBlacklisted(tagElement)) {
            tagElementWhitelist.remove(tagElement);
            tagElementBlacklist.add(tagElement);
            currentFilter = FilterCollection.CUSTOM;
        }
    }
    public static void unlistTagElement(TagElement tagElement) {
        if (tagElement != null) {
            tagElementWhitelist.remove(tagElement);
            tagElementBlacklist.remove(tagElement);
            currentFilter = FilterCollection.CUSTOM;
        }
    }

    /* boolean */
    public static boolean isGroupWhitelisted(String group) {
        boolean value = true;
        for (String name : TagElementControl.getNamesInGroup(group)) {
            if (!FilterControl.isTagElementWhitelisted(group, name)) {
                value = false;
                break;
            }
        }
        return value;
    }
    public static boolean isGroupBlacklisted(String group) {
        boolean value = true;
        for (String name : TagElementControl.getNamesInGroup(group)) {
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
    public static boolean isTagElementWhitelisted(String group, String name) {
        return tagElementWhitelist.contains(TagElementControl.getTagElement(group, name));
    }
    public static boolean isTagElementBlacklisted(TagElement tagElement) {
        if (tagElement == null) return false;
        return tagElementBlacklist.contains(tagElement);
    }
    public static boolean isTagElementBlacklisted(String group, String name) {
        return tagElementBlacklist.contains(TagElementControl.getTagElement(group, name));
    }

    /* get */
    public static ArrayList<DataObject> getValidObjects() {
        return dataObjectsValid;
    }

    public static ArrayList<TagElement> getTagElementWhitelist() {
        return tagElementWhitelist;
    }
    public static ArrayList<TagElement> getTagElementBlacklist() {
        return tagElementBlacklist;
    }

    /* set */
    public static void setFilter(FilterCollection filter) {
        currentFilter = filter;

        validDataElementsRefresh();

        switch (currentFilter) {
            case SHOW_EVERYTHING:
                TopPane.getMenuUntaggedOnly().setSelected(false);
                TopPane.getMenuMaxXTags().setSelected(false);
                break;
            case SHOW_UNTAGGED:
                TopPane.getMenuUntaggedOnly().setSelected(true);
                TopPane.getMenuMaxXTags().setSelected(false);
                break;
            case SHOW_MAX_X_TAGS:
                TopPane.getMenuUntaggedOnly().setSelected(false);
                TopPane.getMenuMaxXTags().setSelected(true);
                break;
            case CUSTOM:
                TopPane.getMenuUntaggedOnly().setSelected(false);
                TopPane.getMenuMaxXTags().setSelected(false);
                break;
            default:
                break;
        }
    }
}
