package project.control;

import javafx.collections.ObservableList;
import project.database.control.DataControl;
import project.database.control.TagControl;
import project.database.element.DataCollection;
import project.database.element.DataObject;
import project.database.element.TagCollection;
import project.database.element.TagObject;
import project.enums.FilterCollection;
import project.gui.component.gallerypane.GalleryPane;
import project.gui.component.leftpane.LeftPane;
import project.gui.component.rightpane.RightPane;
import project.gui.component.toppane.TopPane;

import java.util.ArrayList;

public abstract class FilterControl {
    /* vars */
    private static final DataCollection dataCollectionValid = new DataCollection();

    private static final TagCollection whitelist = new TagCollection();
    private static final TagCollection blacklist = new TagCollection();

    private static FilterCollection currentFilter = FilterCollection.SHOW_EVERYTHING;

    /* public */
    public static void refresh() {
        DataCollection dataCollection = DataControl.getDataCollectionCopy();
        currentFilter.activate();

        if (whitelist.isEmpty() && blacklist.isEmpty()) {
            dataCollectionValid.setAll(dataCollection);
        } else {
            dataCollectionValid.clear();
            for (DataObject dataIterator : dataCollection) {
                TagCollection dataIteratorTagCollection = dataIterator.getTagCollection();
                if (whitelist.isEmpty() || dataIteratorTagCollection.containsAll(whitelist)) {
                    if (blacklist.isEmpty()) {
                        dataCollectionValid.add(dataIterator);
                    } else {
                        boolean isValid = true;
                        for (TagObject tagObject : blacklist) {
                            if (dataIteratorTagCollection.contains(tagObject)) {
                                isValid = false;
                                break;
                            }
                        }
                        if (isValid) dataCollectionValid.add(dataIterator);
                    }
                }
            }
        }
        ReloadControl.request(GalleryPane.class);
    }
    public static void addTagElementToDataElementSelection(TagObject tagObject) {
        if (tagObject != null && !tagObject.isEmpty()) {
            if (!TagControl.getCollection().contains(tagObject)) {
                TagControl.add(tagObject);
            }

            ArrayList<DataObject> dataObjectSelection = SelectionControl.getCollection();
            for (DataObject dataObject : dataObjectSelection)
                if (!dataObject.getTagCollection().contains(tagObject)) {
                    dataObject.getTagCollection().add(tagObject);
                }
        }
    }
    public static void removeTagElementSelectionFromDataElementSelection() {
        ArrayList<TagObject> tagElementsToRemove = new ArrayList<>();
        ObservableList<String> tagElementSelection = RightPane.getListView().getSelectionModel().getSelectedItems();
        for (String tagElement : tagElementSelection) {
            tagElementsToRemove.add(TagControl.getTagObject(tagElement));
        }

        ArrayList<DataObject> dataElementsSelected = SelectionControl.getCollection();
        for (TagObject tagObject : tagElementsToRemove) {
            for (DataObject dataObject : dataElementsSelected) {
                dataObject.getTagCollection().remove(tagObject);
            }

            boolean tagExists = false;
            ArrayList<DataObject> dataObjects = DataControl.getDataCollectionCopy();
            for (DataObject dataObject : dataObjects) {
                if (dataObject.getTagCollection().contains(tagObject)) {
                    tagExists = true;
                    break;
                }
            }
            if (!tagExists) {
                FilterControl.removeTagObject(tagObject);
                TagControl.remove(tagObject);
                ReloadControl.request(LeftPane.class);
            }
        }

        ReloadControl.request(RightPane.class);
    }

    public static void whitelistGroup(String group) {
        ArrayList<String> namesInCategory = TagControl.getNames(group);
        for (String name : namesInCategory) {
            TagObject tagObject = TagControl.getTagObject(group, name);
            FilterControl.whitelistTagElement(tagObject);
        }
    }
    public static void blacklistGroup(String group) {
        ArrayList<String> namesInCategory = TagControl.getNames(group);
        for (String name : namesInCategory) {
            TagObject tagObject = TagControl.getTagObject(group, name);
            FilterControl.blacklistTagElement(tagObject);
        }
    }
    public static void unlistGroup(String group) {
        ArrayList<String> namesInCategory = TagControl.getNames(group);
        for (String name : namesInCategory) {
            TagObject tagObject = TagControl.getTagObject(group, name);
            FilterControl.removeTagObject(tagObject);
        }
    }

    public static void whitelistTagElement(TagObject tagObject) {
        if (tagObject != null && !FilterControl.isTagElementWhitelisted(tagObject)) {
            whitelist.add(tagObject);
            blacklist.remove(tagObject);
            currentFilter = FilterCollection.CUSTOM;
        }
    }
    public static void blacklistTagElement(TagObject tagObject) {
        if (tagObject != null && !FilterControl.isTagElementBlacklisted(tagObject)) {
            whitelist.remove(tagObject);
            blacklist.add(tagObject);
            currentFilter = FilterCollection.CUSTOM;
        }
    }
    public static void removeTagObject(TagObject tagObject) {
        if (tagObject != null) {
            whitelist.remove(tagObject);
            blacklist.remove(tagObject);
            currentFilter = FilterCollection.CUSTOM;
        }
    }

    /* boolean */
    public static boolean isGroupWhitelisted(String group) {
        boolean value = true;
        for (String name : TagControl.getNames(group)) {
            if (!FilterControl.isTagElementWhitelisted(group, name)) {
                value = false;
                break;
            }
        }
        return value;
    }
    public static boolean isGroupBlacklisted(String group) {
        boolean value = true;
        for (String name : TagControl.getNames(group)) {
            if (!FilterControl.isTagElementBlacklisted(group, name)) {
                value = false;
                break;
            }
        }
        return value;
    }

    public static boolean isTagElementWhitelisted(TagObject tagObject) {
        if (tagObject == null) return false;
        return whitelist.contains(tagObject);
    }
    public static boolean isTagElementWhitelisted(String group, String name) {
        return whitelist.contains(TagControl.getTagObject(group, name));
    }
    public static boolean isTagElementBlacklisted(TagObject tagObject) {
        if (tagObject == null) return false;
        return blacklist.contains(tagObject);
    }
    public static boolean isTagElementBlacklisted(String group, String name) {
        return blacklist.contains(TagControl.getTagObject(group, name));
    }

    /* get */
    public static DataCollection getCollection() {
        return dataCollectionValid;
    }

    public static TagCollection getWhitelist() {
        return whitelist;
    }
    public static TagCollection getBlacklist() {
        return blacklist;
    }

    /* set */
    public static void setFilter(FilterCollection filter) {
        currentFilter = filter;

        refresh();

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
