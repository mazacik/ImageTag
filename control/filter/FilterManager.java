package control.filter;

import database.list.DataObjectList;
import database.list.TagList;
import database.object.DataObject;
import database.object.TagObject;
import system.Instances;

import java.util.ArrayList;

public abstract class FilterManager implements Instances {
    private static final DataObjectList newDataObjects = new DataObjectList();
    private static final double FACTOR = 0.5;
    private static boolean showImages = true;
    private static boolean showGifs = true;
    private static boolean showVideos = true;
    private static boolean sessionOnly = false;
    private static boolean enableLEQ = false;
    private static int tagLimit = 0;
    public static void addNewDataObject(DataObject dataObject) {
        newDataObjects.add(dataObject);
    }
    private static boolean isWhitelistOk(TagList tagList) {
        Filter.FilterMode whitelistMode = filter.getWhitelistMode();
        if (infoListWhite.isEmpty()) {
            return true;
        } else if (whitelistMode.equals(Filter.FilterMode.All) && tagList.containsAll(infoListWhite)) {
            return true;
        } else if (whitelistMode.equals(Filter.FilterMode.Any)) {
            for (TagObject tagObject : infoListWhite) {
                if (tagList.contains(tagObject)) {
                    return true;
                }
            }
        }

        return false;
    }
    private static boolean isBlacklistOk(TagList tagList) {
        Filter.FilterMode blacklistMode = filter.getBlacklistMode();
        if (infoListBlack.isEmpty()) {
            return true;
        } else if (blacklistMode.equals(Filter.FilterMode.All) && tagList.containsAll(infoListBlack)) {
            return false;
        } else if (blacklistMode.equals(Filter.FilterMode.Any)) {
            for (TagObject tagObject : infoListBlack) {
                if (tagList.contains(tagObject)) {
                    return false;
                }
            }
        }

        return true;
    }
    public static void apply() {
        filter.clear();
        for (DataObject dataObject : mainDataList) {
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

            if (sessionOnly && !newDataObjects.contains(dataObject)) continue;

            TagList tagList = dataObject.getTagList();
            if (enableLEQ && tagList.size() > tagLimit) continue;

            if (isWhitelistOk(tagList) && isBlacklistOk(tagList)) {
                filter.add(dataObject);
            }
        }
    }
    public static void reset() {
        infoListWhite.clear();
        infoListBlack.clear();
        filter.setAll(mainDataList);
    }
    public static void showSimilar(DataObject dataObject) {
        infoListWhite.clear();
        infoListBlack.clear();
        filter.clear();

        TagList query = dataObject.getTagList();

        for (DataObject iterator : mainDataList) {
            if (iterator.getTagList().size() != 0) {
                ArrayList<TagObject> sameTags = new ArrayList<>(query);
                sameTags.retainAll(iterator.getTagList());

                if (sameTags.size() != 0) {
                    double similarity = (double) sameTags.size() / (double) query.size();
                    if (similarity >= FACTOR) {
                        filter.add(iterator);
                    }
                }
            }
        }
    }

    public static boolean isShowImages() {
        return showImages;
    }
    public static void setShowImages(boolean showImages) {
        FilterManager.showImages = showImages;
    }
    public static boolean isShowGifs() {
        return showGifs;
    }
    public static void setShowGifs(boolean showGifs) {
        FilterManager.showGifs = showGifs;
    }
    public static boolean isShowVideos() {
        return showVideos;
    }
    public static void setShowVideos(boolean showVideos) {
        FilterManager.showVideos = showVideos;
    }
    public static boolean isSessionOnly() {
        return sessionOnly;
    }
    public static void setSessionOnly(boolean showSessionOnly) {
        FilterManager.sessionOnly = showSessionOnly;
    }
    public static boolean isEnableLEQ() {
        return enableLEQ;
    }
    public static void setEnableLEQ(boolean enableLEQ) {
        FilterManager.enableLEQ = enableLEQ;
    }
    public static int getTagLimit() {
        return tagLimit;
    }
    public static void setTagLimit(int tagLimit) {
        FilterManager.tagLimit = tagLimit;
    }
}
