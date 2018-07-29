package project.control;

import javafx.collections.ObservableList;
import project.database.control.DataElementControl;
import project.database.control.TagElementControl;
import project.database.element.DataElement;
import project.database.element.TagElement;
import project.gui.component.GalleryPane;
import project.gui.component.LeftPane;
import project.gui.component.RightPane;
import project.gui.component.TopPane;

import java.util.ArrayList;

public abstract class FilterControl {
    /* vars */
    private static final ArrayList<DataElement> validDataElements = new ArrayList<>();

    private static final ArrayList<TagElement> tagElementWhitelist = new ArrayList<>();
    private static final ArrayList<TagElement> tagElementBlacklist = new ArrayList<>();

    private static FilterCollection currentFilter = FilterCollection.SHOW_EVERYTHING;

    /* public */
    public static void validDataElementsRefresh() {
        ArrayList<DataElement> dataElements = DataElementControl.getDataElementsCopy();

        currentFilter.activate();
        validDataElements.clear();

        if (tagElementWhitelist.isEmpty() && tagElementBlacklist.isEmpty()) validDataElements.addAll(dataElements);
        else {
            for (DataElement dataElement : dataElements) {
                ArrayList<TagElement> dataElementTagElements = dataElement.getTagElements();
                if (tagElementWhitelist.isEmpty() || dataElementTagElements.containsAll(tagElementWhitelist)) {
                    boolean isDataElementValid = true;
                    for (TagElement tagElement : tagElementBlacklist) {
                        if (dataElementTagElements.contains(tagElement)) {
                            isDataElementValid = false;
                            break;
                        }
                    }
                    if (isDataElementValid) validDataElements.add(dataElement);
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

            ArrayList<DataElement> dataElementSelection = SelectionControl.getDataElements();
            for (DataElement dataElement : dataElementSelection)
                if (!dataElement.getTagElements().contains(tagElement)) {
                    dataElement.getTagElements().add(tagElement);
                }
        }
    }
    public static void removeTagElementSelectionFromDataElementSelection() {
        ArrayList<TagElement> tagElementsToRemove = new ArrayList<>();
        ObservableList<String> tagElementSelection = RightPane.getListView().getSelectionModel().getSelectedItems();
        for (String tagElement : tagElementSelection) {
            tagElementsToRemove.add(TagElementControl.getTagElement(tagElement));
        }

        ArrayList<DataElement> dataElementsSelected = SelectionControl.getDataElements();
        for (TagElement tagElement : tagElementsToRemove) {
            for (DataElement dataElement : dataElementsSelected) {
                dataElement.getTagElements().remove(tagElement);
            }

            boolean tagExists = false;
            ArrayList<DataElement> dataElements = DataElementControl.getDataElementsCopy();
            for (DataElement dataElement : dataElements) {
                if (dataElement.getTagElements().contains(tagElement)) {
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
    public static boolean isTagElementBlacklisted(TagElement tagElement) {
        if (tagElement == null) return false;
        return tagElementBlacklist.contains(tagElement);
    }
    public static boolean isTagElementWhitelisted(String group, String name) {
        return tagElementWhitelist.contains(TagElementControl.getTagElement(group, name));
    }
    public static boolean isTagElementBlacklisted(String group, String name) {
        return tagElementBlacklist.contains(TagElementControl.getTagElement(group, name));
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
