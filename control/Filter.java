package control;

import database.list.ObjectList;
import database.list.TagList;
import database.object.DataObject;
import database.object.TagObject;
import lifecycle.InstanceManager;

import java.util.ArrayList;

public class Filter extends ObjectList {
    private final TagList infoListWhite;
    private final TagList infoListBlack;

    private FilterMode whitelistMode;
    private FilterMode blacklistMode;

    private boolean showImages = true;
    private boolean showGifs = true;
    private boolean showVideos = true;
    private boolean sessionOnly = false;
    private boolean enableLimit = false;
    private int limit = 0;

    public Filter() {
        infoListWhite = new TagList();
        infoListBlack = new TagList();

        whitelistMode = FilterMode.All;
        blacklistMode = FilterMode.Any;
    }

    public void reset() {
        InstanceManager.getFilter().getInfoListWhite().clear();
        InstanceManager.getFilter().getInfoListBlack().clear();
        InstanceManager.getFilter().setAll(InstanceManager.getObjectListMain());
    }
    public void refresh() {
        InstanceManager.getFilter().clear();
        for (DataObject dataObject : InstanceManager.getObjectListMain()) {
            switch (dataObject.getFileType()) {
                case IMAGE:
                    if (!showImages) continue;
                    break;
                case VIDEO:
                    if (!showVideos) continue;
                    break;
                case GIF:
                    if (!showGifs) continue;
                    break;
            }

            if (sessionOnly && !currentSessionObjects.contains(dataObject)) continue;

            TagList tagList = dataObject.getTagList();
            if (enableLimit && tagList.size() > limit) continue;

            if (isWhitelistOk(tagList) && isBlacklistOk(tagList)) {
                InstanceManager.getFilter().add(dataObject);
            }
        }
    }
    public boolean refreshObject(DataObject dataObject) {
        switch (dataObject.getFileType()) {
            case IMAGE:
                if (!showImages) {
                    this.remove(dataObject);
                    return false;
                }

                break;
            case VIDEO:
                if (!showVideos) {
                    this.remove(dataObject);
                    return false;
                }
                break;
            case GIF:
                if (!showGifs) {
                    this.remove(dataObject);
                    return false;
                }
                break;
        }

        if (sessionOnly && !currentSessionObjects.contains(dataObject)) {
            this.remove(dataObject);
            return false;
        }

        TagList tagList = dataObject.getTagList();
        if (enableLimit && tagList.size() > limit) {
            this.remove(dataObject);
            return false;
        }

        if (isWhitelistOk(tagList) && isBlacklistOk(tagList)) {
            this.add(dataObject);
            return true;
        }

        return false;
    }

    @SuppressWarnings("FieldCanBeLocal")
    private final double similarityFactor = 0.5;
    public void showSimilar(DataObject dataObject) {
        InstanceManager.getFilter().getInfoListWhite().clear();
        InstanceManager.getFilter().getInfoListBlack().clear();
        InstanceManager.getFilter().clear();

        TagList query = dataObject.getTagList();

        for (DataObject iterator : InstanceManager.getObjectListMain()) {
            if (iterator.getTagList().size() != 0) {
                ArrayList<TagObject> sameTags = new ArrayList<>(query);
                sameTags.retainAll(iterator.getTagList());

                if (sameTags.size() != 0) {
                    double similarity = (double) sameTags.size() / (double) query.size();
                    if (similarity >= similarityFactor) {
                        InstanceManager.getFilter().add(iterator);
                    }
                }
            }
        }
    }

    private final ObjectList currentSessionObjects = new ObjectList();
    public ObjectList getCurrentSessionObjects() {
        return currentSessionObjects;
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
        for (String name : InstanceManager.getTagListMain().getNames(group)) {
            whitelist(InstanceManager.getTagListMain().getTagObject(group, name));
        }
    }
    public void blacklist(String group) {
        for (String name : InstanceManager.getTagListMain().getNames(group)) {
            blacklist(InstanceManager.getTagListMain().getTagObject(group, name));
        }
    }
    public void unlist(String group) {
        for (String name : InstanceManager.getTagListMain().getNames(group)) {
            unlist(InstanceManager.getTagListMain().getTagObject(group, name));
        }
    }

    private boolean isWhitelistOk(TagList tagList) {
        FilterMode whitelistMode = InstanceManager.getFilter().getWhitelistMode();
        if (InstanceManager.getFilter().getInfoListWhite().isEmpty()) {
            return true;
        } else if (whitelistMode.equals(FilterMode.All) && tagList.containsAll(InstanceManager.getFilter().getInfoListWhite())) {
            return true;
        } else if (whitelistMode.equals(FilterMode.Any)) {
            for (TagObject tagObject : InstanceManager.getFilter().getInfoListWhite()) {
                if (tagList.contains(tagObject)) {
                    return true;
                }
            }
        }
        return false;
    }
    private boolean isBlacklistOk(TagList tagList) {
        FilterMode blacklistMode = InstanceManager.getFilter().getBlacklistMode();
        if (InstanceManager.getFilter().getInfoListBlack().isEmpty()) {
            return true;
        } else if (blacklistMode.equals(FilterMode.All) && tagList.containsAll(InstanceManager.getFilter().getInfoListBlack())) {
            return false;
        } else if (blacklistMode.equals(FilterMode.Any)) {
            for (TagObject tagObject : InstanceManager.getFilter().getInfoListBlack()) {
                if (tagList.contains(tagObject)) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean isWhitelisted(String group) {
        boolean value = true;
        for (String name : InstanceManager.getTagListMain().getNames(group)) {
            if (!isWhitelisted(group, name)) {
                value = false;
                break;
            }
        }
        return value;
    }
    public boolean isBlacklisted(String group) {
        boolean value = true;
        for (String name : InstanceManager.getTagListMain().getNames(group)) {
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
        return infoListWhite.contains(InstanceManager.getTagListMain().getTagObject(group, name));
    }
    public boolean isBlacklisted(TagObject tagObject) {
        return infoListBlack.contains(tagObject);
    }
    public boolean isBlacklisted(String group, String name) {
        return infoListBlack.contains(InstanceManager.getTagListMain().getTagObject(group, name));
    }

    public TagList getInfoListWhite() {
        return infoListWhite;
    }
    public TagList getInfoListBlack() {
        return infoListBlack;
    }

    public boolean isShowImages() {
        return showImages;
    }
    public boolean isShowGifs() {
        return showGifs;
    }
    public boolean isShowVideos() {
        return showVideos;
    }
    public boolean isSessionOnly() {
        return sessionOnly;
    }
    public boolean isEnableLimit() {
        return enableLimit;
    }
    public int getLimit() {
        return limit;
    }

    public void setShowImages(boolean showImages) {
        this.showImages = showImages;
    }
    public void setShowGifs(boolean showGifs) {
        this.showGifs = showGifs;
    }
    public void setShowVideos(boolean showVideos) {
        this.showVideos = showVideos;
    }
    public void setSessionOnly(boolean sessionOnly) {
        this.sessionOnly = sessionOnly;
    }
    public void setEnableLimit(boolean enableLimit) {
        this.enableLimit = enableLimit;
    }
    public void setLimit(int limit) {
        this.limit = limit;
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
