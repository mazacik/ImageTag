package control.filter;

import database.list.DataObjectList;
import database.list.TagList;
import database.object.DataObject;
import database.object.TagObject;
import lifecycle.InstanceManager;

import java.util.ArrayList;

public abstract class FilterManager {
    private static boolean showImages = true;
    private static boolean showGifs = true;
    private static boolean showVideos = true;
    private static boolean sessionOnly = false;
    private static final DataObjectList newDataObjects = new DataObjectList();
    private static final double SIMILARITY_FACTOR = 0.5;
    private static boolean enableLimit = false;
    public static void addNewDataObject(DataObject dataObject) {
        newDataObjects.add(dataObject);
    }

    private static boolean isWhitelistOk(TagList tagList) {
        Filter.FilterMode whitelistMode = InstanceManager.getFilter().getWhitelistMode();
        if (InstanceManager.getFilter().getInfoListWhite().isEmpty()) {
            return true;
        } else if (whitelistMode.equals(Filter.FilterMode.All) && tagList.containsAll(InstanceManager.getFilter().getInfoListWhite())) {
            return true;
        } else if (whitelistMode.equals(Filter.FilterMode.Any)) {
            for (TagObject tagObject : InstanceManager.getFilter().getInfoListWhite()) {
                if (tagList.contains(tagObject)) {
                    return true;
                }
            }
        }
        return false;
    }
    private static boolean isBlacklistOk(TagList tagList) {
        Filter.FilterMode blacklistMode = InstanceManager.getFilter().getBlacklistMode();
        if (InstanceManager.getFilter().getInfoListBlack().isEmpty()) {
            return true;
        } else if (blacklistMode.equals(Filter.FilterMode.All) && tagList.containsAll(InstanceManager.getFilter().getInfoListBlack())) {
            return false;
        } else if (blacklistMode.equals(Filter.FilterMode.Any)) {
            for (TagObject tagObject : InstanceManager.getFilter().getInfoListBlack()) {
                if (tagList.contains(tagObject)) {
                    return false;
                }
            }
        }
        return true;
    }
    private static int limit = 0;
    public static void reset() {
        InstanceManager.getFilter().getInfoListWhite().clear();
        InstanceManager.getFilter().getInfoListBlack().clear();
        InstanceManager.getFilter().setAll(InstanceManager.getMainDataList());
    }
    public static void refresh() {
        InstanceManager.getFilter().clear();
        for (DataObject dataObject : InstanceManager.getMainDataList()) {
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
            if (enableLimit && tagList.size() > limit) continue;

            if (isWhitelistOk(tagList) && isBlacklistOk(tagList)) {
                InstanceManager.getFilter().add(dataObject);
            }
        }
    }
    public static void showSimilar(DataObject dataObject) {
        InstanceManager.getFilter().getInfoListWhite().clear();
        InstanceManager.getFilter().getInfoListBlack().clear();
        InstanceManager.getFilter().clear();

        TagList query = dataObject.getTagList();

        for (DataObject iterator : InstanceManager.getMainDataList()) {
            if (iterator.getTagList().size() != 0) {
                ArrayList<TagObject> sameTags = new ArrayList<>(query);
                sameTags.retainAll(iterator.getTagList());

                if (sameTags.size() != 0) {
                    double similarity = (double) sameTags.size() / (double) query.size();
                    if (similarity >= SIMILARITY_FACTOR) {
                        InstanceManager.getFilter().add(iterator);
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
    public static boolean isEnableLimit() {
        return enableLimit;
    }
    public static void setEnableLimit(boolean enableLimit) {
        FilterManager.enableLimit = enableLimit;
    }
    public static int getLimit() {
        return limit;
    }
    public static void setLimit(int limit) {
        FilterManager.limit = limit;
    }
}
