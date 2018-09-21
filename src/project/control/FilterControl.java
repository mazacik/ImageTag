package project.control;

import project.database.object.DataCollection;
import project.database.object.TagCollection;
import project.database.object.TagObject;
import project.gui.component.GUINode;
import project.gui.component.toppane.TopPane;

public class FilterControl extends ControlCollection {
    private final ReloadControl reloadControl;
    private final TagControl tagControl;

    private final DataCollection collection;
    private final TagCollection whitelist;
    private final TagCollection blacklist;

    private FilterMode whitelistMode;
    private FilterMode blacklistMode;
    private Filter currentFilter;

    FilterControl() {
        reloadControl = MainControl.getReloadControl();
        tagControl = MainControl.getTagControl();

        collection = new DataCollection();
        whitelist = new TagCollection();
        blacklist = new TagCollection();

        whitelistMode = FilterMode.All;
        blacklistMode = FilterMode.Any;
        currentFilter = Filter.SHOW_EVERYTHING;
    }

    public void applyFilter() {
        currentFilter.apply();
        reloadControl.reload(GUINode.GALLERYPANE);
    }

    public void whitelistTagObject(TagObject tagObject) {
        if (!isTagObjectWhitelisted(tagObject)) {
            whitelist.add(tagObject);
            blacklist.remove(tagObject);
            currentFilter = Filter.CUSTOM;
        }
    }
    public void blacklistTagObject(TagObject tagObject) {
        if (!isTagObjectBlacklisted(tagObject)) {
            whitelist.remove(tagObject);
            blacklist.add(tagObject);
            currentFilter = Filter.CUSTOM;
        }
    }
    public void unlistTagObject(TagObject tagObject) {
        whitelist.remove(tagObject);
        blacklist.remove(tagObject);
        currentFilter = Filter.CUSTOM;
    }

    public void whitelistGroup(String group) {
        for (String name : tagControl.getNames(group)) {
            whitelistTagObject(tagControl.getTagObject(group, name));
        }
    }
    public void blacklistGroup(String group) {
        for (String name : tagControl.getNames(group)) {
            blacklistTagObject(tagControl.getTagObject(group, name));
        }
    }
    public void unlistGroup(String group) {
        for (String name : tagControl.getNames(group)) {
            unlistTagObject(tagControl.getTagObject(group, name));
        }
    }

    public boolean isGroupWhitelisted(String group) {
        boolean value = true;
        for (String name : tagControl.getNames(group)) {
            if (!isTagObjectWhitelisted(group, name)) {
                value = false;
                break;
            }
        }
        return value;
    }
    public boolean isGroupBlacklisted(String group) {
        boolean value = true;
        for (String name : tagControl.getNames(group)) {
            if (!isTagObjectBlacklisted(group, name)) {
                value = false;
                break;
            }
        }
        return value;
    }

    public boolean isTagObjectWhitelisted(TagObject tagObject) {
        return whitelist.contains(tagObject);
    }
    public boolean isTagObjectWhitelisted(String group, String name) {
        return whitelist.contains(tagControl.getTagObject(group, name));
    }
    public boolean isTagObjectBlacklisted(TagObject tagObject) {
        return blacklist.contains(tagObject);
    }
    public boolean isTagObjectBlacklisted(String group, String name) {
        return blacklist.contains(tagControl.getTagObject(group, name));
    }

    public DataCollection getCollection() {
        return collection;
    }
    public TagCollection getWhitelist() {
        return whitelist;
    }
    public TagCollection getBlacklist() {
        return blacklist;
    }

    public FilterMode getWhitelistMode() {
        return whitelistMode;
    }
    public FilterMode getBlacklistMode() {
        return blacklistMode;
    }

    public void setFilter(Filter filter) {
        currentFilter = filter;
        applyFilter();

        switch (currentFilter) {
            case CUSTOM:
                TopPane.getMenuUntaggedOnly().setSelected(false);
                TopPane.getMenuMaxXTags().setSelected(false);
                break;
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
            default:
                break;
        }
    }

    public void setWhitelistMode(FilterMode whitelistMode) {
        this.whitelistMode = whitelistMode;
    }
    public void setBlacklistMode(FilterMode blacklistMode) {
        this.blacklistMode = blacklistMode;
    }

    public enum FilterMode {
        Any,
        All
    }
}
