package control.filter;

import database.list.DataObjectList;
import database.object.TagObject;
import system.Instances;

public class Filter extends DataObjectList implements Instances {
    private FilterMode whitelistMode;
    private FilterMode blacklistMode;

    public Filter() {
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
        for (String name : mainInfoList.getNames(group)) {
            whitelist(mainInfoList.getTagObject(group, name));
        }
    }
    public void blacklist(String group) {
        for (String name : mainInfoList.getNames(group)) {
            blacklist(mainInfoList.getTagObject(group, name));
        }
    }
    public void unlist(String group) {
        for (String name : mainInfoList.getNames(group)) {
            unlist(mainInfoList.getTagObject(group, name));
        }
    }

    public boolean isWhitelisted(String group) {
        boolean value = true;
        for (String name : mainInfoList.getNames(group)) {
            if (!isWhitelisted(group, name)) {
                value = false;
                break;
            }
        }
        return value;
    }
    public boolean isBlacklisted(String group) {
        boolean value = true;
        for (String name : mainInfoList.getNames(group)) {
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
        return infoListWhite.contains(mainInfoList.getTagObject(group, name));
    }
    public boolean isBlacklisted(TagObject tagObject) {
        return infoListBlack.contains(tagObject);
    }
    public boolean isBlacklisted(String group, String name) {
        return infoListBlack.contains(mainInfoList.getTagObject(group, name));
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
