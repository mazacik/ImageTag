package project.control;

import javafx.collections.ObservableList;
import project.database.control.DataElementControl;
import project.database.control.TagElementControl;
import project.database.element.DataElement;
import project.database.element.TagElement;
import project.gui.component.GalleryPane;
import project.gui.component.LeftPane;
import project.gui.component.RightPane;
import project.gui.custom.generic.NumberInputWindow;

import java.util.ArrayList;

public abstract class FilterControl {
    /* vars */
    private static final ArrayList<DataElement> validDataElements = new ArrayList<>();

    private static final ArrayList<TagElement> tagElementWhitelist = new ArrayList<>();
    private static final ArrayList<TagElement> tagElementBlacklist = new ArrayList<>();

    private static boolean customFilterUntaggedOnly = false;
    private static boolean customFilterLessThanXTags = false;
    private static int customFilterLessThanXTagsMax = 0;

    /* public */
    public static void revalidateDataElements() {
        if (customFilterUntaggedOnly) {
            customFilterUntaggedOnly();
        } else if (customFilterLessThanXTags) {
            customFilterLessThanXTags(customFilterLessThanXTagsMax);
        }

        ArrayList<DataElement> dataElements = DataElementControl.getDataElementsCopy();
        validDataElements.clear();
        if (tagElementWhitelist.isEmpty() && tagElementBlacklist.isEmpty()) {
            validDataElements.addAll(dataElements);
        } else if (tagElementWhitelist.isEmpty() && !tagElementBlacklist.isEmpty()) {
            for (DataElement dataElement : dataElements) {
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
            ReloadControl.requestReloadOf(LeftPane.class, GalleryPane.class);
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
                ReloadControl.requestReloadOf(LeftPane.class);
            }
        }

        ReloadControl.requestReloadOf(RightPane.class);
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

    public static void customFilterUntaggedOnly() {
        FilterControl.getTagElementWhitelist().clear();
        FilterControl.getTagElementBlacklist().clear();
        FilterControl.getTagElementBlacklist().addAll(TagElementControl.getTagElements());
    }
    public static void customFilterLessThanXTagsGetValue() {
        int maxTags = new NumberInputWindow("Filter Settings", "Maximum number of tags:").getResultValue();
        if (maxTags == 0) return;
        customFilterLessThanXTagsMax = maxTags;
        FilterControl.revalidateDataElements();
    }
    public static void customFilterLessThanXTags(int maxTags) {
        if (maxTags == 0) return;
        FilterControl.getValidDataElements().clear();
        FilterControl.getTagElementWhitelist().clear();
        FilterControl.getTagElementBlacklist().clear();
        for (DataElement dataElement : DataElementControl.getDataElementsCopy()) {
            if (dataElement.getTagElements().size() <= maxTags) {
                FilterControl.getValidDataElements().add(dataElement);
            }
        }
        ReloadControl.requestReloadOf(GalleryPane.class);
    }
    public static void customFilterResetFiltering() {
        customFilterUntaggedOnly = false;
        customFilterLessThanXTags = false;
        FilterControl.getTagElementWhitelist().clear();
        FilterControl.getTagElementBlacklist().clear();
        FilterControl.revalidateDataElements();
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
    public static void setCustomFilterUntaggedOnly(boolean value) {
        customFilterUntaggedOnly = value;
    }
    public static void setCustomFilterLessThanXTags(boolean value) {
        customFilterLessThanXTags = value;
    }
}
