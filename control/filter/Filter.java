package control.filter;

import database.list.DataObjectList;
import database.list.TagList;
import database.object.TagObject;
import lifecycle.InstanceManager;

public class Filter extends DataObjectList {
    private TagList infoListWhite;
    private TagList infoListBlack;

    private FilterMode whitelistMode;
    private FilterMode blacklistMode;

    public Filter() {
        infoListWhite = new TagList();
        infoListBlack = new TagList();

        whitelistMode = FilterMode.All;
        blacklistMode = FilterMode.Any;
    }

    public void whitelist(TagObject tagObject) {
        if (!isWhitelisted(tagObject)) {
            infoListWhite.add(tagObject);
            infoListBlack.remove(tagObject);
        }
    }
    public void blacklist(TagObject tagObject) {
        if (!isBlacklisted(tagObject)) {
            infoListWhite.remove(tagObject);
            infoListBlack.add(tagObject);
        }
    }
    public void unlist(TagObject tagObject) {
        infoListWhite.remove(tagObject);
        infoListBlack.remove(tagObject);
    }

    public void whitelist(String group) {
        for (String name : InstanceManager.getMainInfoList().getNames(group)) {
            whitelist(InstanceManager.getMainInfoList().getTagObject(group, name));
        }
    }
    public void blacklist(String group) {
        for (String name : InstanceManager.getMainInfoList().getNames(group)) {
            blacklist(InstanceManager.getMainInfoList().getTagObject(group, name));
        }
    }
    public void unlist(String group) {
        for (String name : InstanceManager.getMainInfoList().getNames(group)) {
            unlist(InstanceManager.getMainInfoList().getTagObject(group, name));
        }
    }

    public boolean isWhitelisted(String group) {
        boolean value = true;
        for (String name : InstanceManager.getMainInfoList().getNames(group)) {
            if (!isWhitelisted(group, name)) {
                value = false;
                break;
            }
        }
        return value;
    }
    public boolean isBlacklisted(String group) {
        boolean value = true;
        for (String name : InstanceManager.getMainInfoList().getNames(group)) {
            if (!isBlacklisted(group, name)) {
                value = false;
                break;
            }
        }
        return value;
    }

    public boolean isWhitelisted(TagObject tagObject) {
        return infoListWhite.contains(tagObject);
    }
    public boolean isWhitelisted(String group, String name) {
        return infoListWhite.contains(InstanceManager.getMainInfoList().getTagObject(group, name));
    }
    public boolean isBlacklisted(TagObject tagObject) {
        return infoListBlack.contains(tagObject);
    }
    public boolean isBlacklisted(String group, String name) {
        return infoListBlack.contains(InstanceManager.getMainInfoList().getTagObject(group, name));
    }

    public TagList getInfoListWhite() {
        return infoListWhite;
    }
    public TagList getInfoListBlack() {
        return infoListBlack;
    }

    //todo re-implement this
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

    public enum FilterMode {
        Any,
        All
    }
}
