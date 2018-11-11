package project.control.filter;

import project.database.object.DataCollection;
import project.database.object.DataObject;
import project.database.object.TagCollection;
import project.database.object.TagObject;
import project.gui.component.GUINode;
import project.utils.MainUtil;

import java.util.Random;

public class Filter extends DataCollection implements MainUtil {
    private final TagCollection whitelist;
    private final TagCollection blacklist;

    private FilterMode whitelistMode;
    private FilterMode blacklistMode;
    private FilterData currentFilterData;

    public Filter() {
        whitelist = new TagCollection();
        blacklist = new TagCollection();

        whitelistMode = FilterMode.All;
        blacklistMode = FilterMode.Any;
        currentFilterData = FilterData.SHOW_EVERYTHING;
    }
    public void apply() {
        currentFilterData.apply();
        reload.queue(GUINode.GALLERYPANE);
    }

    public void whitelistTagObject(TagObject tagObject) {
        if (!isTagObjectWhitelisted(tagObject)) {
            whitelist.add(tagObject);
            blacklist.remove(tagObject);
            currentFilterData = FilterData.CUSTOM;
        }
    }
    public void blacklistTagObject(TagObject tagObject) {
        if (!isTagObjectBlacklisted(tagObject)) {
            whitelist.remove(tagObject);
            blacklist.add(tagObject);
            currentFilterData = FilterData.CUSTOM;
        }
    }
    public void unlistTagObject(TagObject tagObject) {
        whitelist.remove(tagObject);
        blacklist.remove(tagObject);
        currentFilterData = FilterData.CUSTOM;
    }

    public void whitelistGroup(String group) {
        for (String name : mainTags.getNames(group)) {
            whitelistTagObject(mainTags.getTagObject(group, name));
        }
    }
    public void blacklistGroup(String group) {
        for (String name : mainTags.getNames(group)) {
            blacklistTagObject(mainTags.getTagObject(group, name));
        }
    }
    public void unlistGroup(String group) {
        for (String name : mainTags.getNames(group)) {
            unlistTagObject(mainTags.getTagObject(group, name));
        }
    }

    public boolean isGroupWhitelisted(String group) {
        boolean value = true;
        for (String name : mainTags.getNames(group)) {
            if (!isTagObjectWhitelisted(group, name)) {
                value = false;
                break;
            }
        }
        return value;
    }
    public boolean isGroupBlacklisted(String group) {
        boolean value = true;
        for (String name : mainTags.getNames(group)) {
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
        return whitelist.contains(mainTags.getTagObject(group, name));
    }
    public boolean isTagObjectBlacklisted(TagObject tagObject) {
        return blacklist.contains(tagObject);
    }
    public boolean isTagObjectBlacklisted(String group, String name) {
        return blacklist.contains(mainTags.getTagObject(group, name));
    }

    public DataObject getRandomObject() {
        return this.get(new Random().nextInt(this.size()));
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

    public void setFilter(FilterData filterData) {
        this.currentFilterData = filterData;
        this.apply();

        switch (this.currentFilterData) {
            case CUSTOM:
                topPane.getMenuUntaggedOnly().setSelected(false);
                topPane.getMenuMaxXTags().setSelected(false);
                break;
            case SHOW_EVERYTHING:
                topPane.getMenuUntaggedOnly().setSelected(false);
                topPane.getMenuMaxXTags().setSelected(false);
                break;
            case SHOW_UNTAGGED:
                topPane.getMenuUntaggedOnly().setSelected(true);
                topPane.getMenuMaxXTags().setSelected(false);
                break;
            case SHOW_MAX_X_TAGS:
                topPane.getMenuUntaggedOnly().setSelected(false);
                topPane.getMenuMaxXTags().setSelected(true);
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
