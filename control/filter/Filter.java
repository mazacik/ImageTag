package control.filter;

import control.reload.Reload;
import database.list.DataObjectList;
import database.object.TagObject;
import system.InstanceRepo;

public class Filter extends DataObjectList implements InstanceRepo {
    private FilterMode whitelistMode;
    private FilterMode blacklistMode;
    private FilterTemplate currentFilterTemplate;

    public Filter() {
        whitelistMode = FilterMode.All;
        blacklistMode = FilterMode.Any;
        currentFilterTemplate = FilterTemplate.NONE;
    }
    public void apply() {
        currentFilterTemplate.apply();
        reload.notifyChangeIn(Reload.Control.FILTER);
    }

    public void whitelistTagObject(TagObject tagObject) {
        if (!isTagObjectWhitelisted(tagObject)) {
            infoListWhite.add(tagObject);
            infoListBlack.remove(tagObject);
            currentFilterTemplate = FilterTemplate.CUSTOM;
        }
    }
    public void blacklistTagObject(TagObject tagObject) {
        if (!isTagObjectBlacklisted(tagObject)) {
            infoListWhite.remove(tagObject);
            infoListBlack.add(tagObject);
            currentFilterTemplate = FilterTemplate.CUSTOM;
        }
    }
    public void unlistTagObject(TagObject tagObject) {
        infoListWhite.remove(tagObject);
        infoListBlack.remove(tagObject);
        currentFilterTemplate = FilterTemplate.CUSTOM;
    }

    public void whitelistGroup(String group) {
        for (String name : mainInfoList.getNames(group)) {
            whitelistTagObject(mainInfoList.getTagObject(group, name));
        }
    }
    public void blacklistGroup(String group) {
        for (String name : mainInfoList.getNames(group)) {
            blacklistTagObject(mainInfoList.getTagObject(group, name));
        }
    }
    public void unlistGroup(String group) {
        for (String name : mainInfoList.getNames(group)) {
            unlistTagObject(mainInfoList.getTagObject(group, name));
        }
    }

    public boolean isGroupWhitelisted(String group) {
        boolean value = true;
        for (String name : mainInfoList.getNames(group)) {
            if (!isTagObjectWhitelisted(group, name)) {
                value = false;
                break;
            }
        }
        return value;
    }
    public boolean isGroupBlacklisted(String group) {
        boolean value = true;
        for (String name : mainInfoList.getNames(group)) {
            if (!isTagObjectBlacklisted(group, name)) {
                value = false;
                break;
            }
        }
        return value;
    }

    public boolean isTagObjectWhitelisted(TagObject tagObject) {
        return infoListWhite.contains(tagObject);
    }
    public boolean isTagObjectWhitelisted(String group, String name) {
        return infoListWhite.contains(mainInfoList.getTagObject(group, name));
    }
    public boolean isTagObjectBlacklisted(TagObject tagObject) {
        return infoListBlack.contains(tagObject);
    }
    public boolean isTagObjectBlacklisted(String group, String name) {
        return infoListBlack.contains(mainInfoList.getTagObject(group, name));
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
    }

    public enum FilterMode {
        Any,
        All
    }
}
