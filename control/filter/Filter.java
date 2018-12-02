package control.filter;

import database.object.DataCollection;
import database.object.DataObject;
import database.object.TagCollection;
import database.object.TagObject;
import gui.component.NodeEnum;
import utils.MainUtil;

import java.util.Random;

public class Filter extends DataCollection implements MainUtil {
    private final TagCollection whitelist;
    private final TagCollection blacklist;

    private FilterMode whitelistMode;
    private FilterMode blacklistMode;
    private FilterTemplate currentFilterTemplate;

    public Filter() {
        whitelist = new TagCollection();
        blacklist = new TagCollection();

        whitelistMode = FilterMode.All;
        blacklistMode = FilterMode.Any;
        currentFilterTemplate = FilterTemplate.SHOW_EVERYTHING;
    }
    public void apply() {
        currentFilterTemplate.apply();
        reload.queue(NodeEnum.GALLERYPANE);
    }

    public void whitelistTagObject(TagObject tagObject) {
        if (!isTagObjectWhitelisted(tagObject)) {
            whitelist.add(tagObject);
            blacklist.remove(tagObject);
            currentFilterTemplate = FilterTemplate.CUSTOM;
        }
    }
    public void blacklistTagObject(TagObject tagObject) {
        if (!isTagObjectBlacklisted(tagObject)) {
            whitelist.remove(tagObject);
            blacklist.add(tagObject);
            currentFilterTemplate = FilterTemplate.CUSTOM;
        }
    }
    public void unlistTagObject(TagObject tagObject) {
        whitelist.remove(tagObject);
        blacklist.remove(tagObject);
        currentFilterTemplate = FilterTemplate.CUSTOM;
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
    public void setWhitelistMode(FilterMode whitelistMode) {
        this.whitelistMode = whitelistMode;
    }
    public FilterMode getBlacklistMode() {
        return blacklistMode;
    }
    public void setBlacklistMode(FilterMode blacklistMode) {
        this.blacklistMode = blacklistMode;
    }
    public void setFilter(FilterTemplate filterTemplate) {
        this.currentFilterTemplate = filterTemplate;
        this.apply();

        switch (this.currentFilterTemplate) {
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

    public enum FilterMode {
        Any,
        All
    }
}
