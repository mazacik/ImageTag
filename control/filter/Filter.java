package control.filter;

import control.reload.Reload;
import database.list.MainListData;
import database.object.DataObject;
import database.object.InfoObject;
import utils.MainUtil;

import java.util.Random;

public class Filter extends MainListData implements MainUtil {
    private FilterMode whitelistMode;
    private FilterMode blacklistMode;
    private FilterTemplate currentFilterTemplate;

    public Filter() {
        whitelistMode = FilterMode.All;
        blacklistMode = FilterMode.Any;
        currentFilterTemplate = FilterTemplate.SHOW_EVERYTHING;
    }
    public void apply() {
        currentFilterTemplate.apply();
        reload.notifyChangeIn(Reload.Control.FILTER);
    }

    public void whitelistTagObject(InfoObject infoObject) {
        if (!isTagObjectWhitelisted(infoObject)) {
            infoListWhite.add(infoObject);
            infoListBlack.remove(infoObject);
            currentFilterTemplate = FilterTemplate.CUSTOM;
        }
    }
    public void blacklistTagObject(InfoObject infoObject) {
        if (!isTagObjectBlacklisted(infoObject)) {
            infoListWhite.remove(infoObject);
            infoListBlack.add(infoObject);
            currentFilterTemplate = FilterTemplate.CUSTOM;
        }
    }
    public void unlistTagObject(InfoObject infoObject) {
        infoListWhite.remove(infoObject);
        infoListBlack.remove(infoObject);
        currentFilterTemplate = FilterTemplate.CUSTOM;
    }

    public void whitelistGroup(String group) {
        for (String name : mainListInfo.getNames(group)) {
            whitelistTagObject(mainListInfo.getInfoObject(group, name));
        }
    }
    public void blacklistGroup(String group) {
        for (String name : mainListInfo.getNames(group)) {
            blacklistTagObject(mainListInfo.getInfoObject(group, name));
        }
    }
    public void unlistGroup(String group) {
        for (String name : mainListInfo.getNames(group)) {
            unlistTagObject(mainListInfo.getInfoObject(group, name));
        }
    }

    public boolean isGroupWhitelisted(String group) {
        boolean value = true;
        for (String name : mainListInfo.getNames(group)) {
            if (!isTagObjectWhitelisted(group, name)) {
                value = false;
                break;
            }
        }
        return value;
    }
    public boolean isGroupBlacklisted(String group) {
        boolean value = true;
        for (String name : mainListInfo.getNames(group)) {
            if (!isTagObjectBlacklisted(group, name)) {
                value = false;
                break;
            }
        }
        return value;
    }

    public boolean isTagObjectWhitelisted(InfoObject infoObject) {
        return infoListWhite.contains(infoObject);
    }
    public boolean isTagObjectWhitelisted(String group, String name) {
        return infoListWhite.contains(mainListInfo.getInfoObject(group, name));
    }
    public boolean isTagObjectBlacklisted(InfoObject infoObject) {
        return infoListBlack.contains(infoObject);
    }
    public boolean isTagObjectBlacklisted(String group, String name) {
        return infoListBlack.contains(mainListInfo.getInfoObject(group, name));
    }

    public DataObject getRandomObject() {
        return this.get(new Random().nextInt(this.size()));
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
                toolbar.getMenuUntaggedOnly().setSelected(false);
                toolbar.getMenuMaxXTags().setSelected(false);
                break;
            case SHOW_EVERYTHING:
                toolbar.getMenuUntaggedOnly().setSelected(false);
                toolbar.getMenuMaxXTags().setSelected(false);
                break;
            case SHOW_UNTAGGED:
                toolbar.getMenuUntaggedOnly().setSelected(true);
                toolbar.getMenuMaxXTags().setSelected(false);
                break;
            case SHOW_MAX_X_TAGS:
                toolbar.getMenuUntaggedOnly().setSelected(false);
                toolbar.getMenuMaxXTags().setSelected(true);
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
