package project.control;

import project.database.control.TagControl;
import project.database.element.DataCollection;
import project.database.element.TagCollection;
import project.database.element.TagObject;
import project.enums.Filter;
import project.gui.component.gallerypane.GalleryPane;
import project.gui.component.toppane.TopPane;

public abstract class FilterControl {
    /* vars */
    private static final DataCollection dataCollectionFiltered = new DataCollection();

    private static final TagCollection whitelist = new TagCollection();
    private static final TagCollection blacklist = new TagCollection();

    private static Filter currentFilter = Filter.SHOW_EVERYTHING;

    /* public */
    public static void doWork() {
        currentFilter.apply();
        ReloadControl.reload(GalleryPane.class);
    }

    public static void whitelistTagObject(TagObject tagObject) {
        if (tagObject != null && !FilterControl.isTagElementWhitelisted(tagObject)) {
            whitelist.add(tagObject);
            blacklist.remove(tagObject);
            currentFilter = Filter.CUSTOM;
        }
    }
    public static void blacklistTagObject(TagObject tagObject) {
        if (tagObject != null && !FilterControl.isTagElementBlacklisted(tagObject)) {
            whitelist.remove(tagObject);
            blacklist.add(tagObject);
            currentFilter = Filter.CUSTOM;
        }
    }
    public static void removeTagObject(TagObject tagObject) {
        if (tagObject != null) {
            whitelist.remove(tagObject);
            blacklist.remove(tagObject);
            currentFilter = Filter.CUSTOM;
        }
    }

    public static void whitelistGroup(String group) {
        for (String name : TagControl.getNames(group)) {
            FilterControl.whitelistTagObject(TagControl.getTagObject(group, name));
        }
    }
    public static void blacklistGroup(String group) {
        for (String name : TagControl.getNames(group)) {
            FilterControl.blacklistTagObject(TagControl.getTagObject(group, name));
        }
    }
    public static void unlistGroup(String group) {
        for (String name : TagControl.getNames(group)) {
            FilterControl.removeTagObject(TagControl.getTagObject(group, name));
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
        return whitelist.contains(tagObject);
    }
    public static boolean isTagElementWhitelisted(String group, String name) {
        return whitelist.contains(TagControl.getTagObject(group, name));
    }
    public static boolean isTagElementBlacklisted(TagObject tagObject) {
        return blacklist.contains(tagObject);
    }
    public static boolean isTagElementBlacklisted(String group, String name) {
        return blacklist.contains(TagControl.getTagObject(group, name));
    }

    /* get */
    public static DataCollection getCollection() {
        return dataCollectionFiltered;
    }

    public static TagCollection getWhitelist() {
        return whitelist;
    }
    public static TagCollection getBlacklist() {
        return blacklist;
    }

    /* set */
    public static void setFilter(Filter filter) {
        currentFilter = filter;
        doWork();

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
