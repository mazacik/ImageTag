package control.filter;

import database.list.BaseListInfo;
import database.object.DataObject;
import database.object.InfoObject;
import utils.InstanceRepo;

public enum FilterTemplate implements InstanceRepo {
    NONE {
        public void apply() {
        }
    },
    SHOW_EVERYTHING {
        public void apply() {
            infoListWhite.clear();
            infoListBlack.clear();
            filter.setAll(mainListData);
        }
    },
    SHOW_MAX_X_TAGS {
        public void apply() {
            infoListWhite.clear();
            infoListBlack.clear();
            filter.clear();
            for (DataObject dataObject : mainListData) {
                if (dataObject.getBaseListInfo().size() <= maxTagsValue) {
                    filter.add(dataObject);
                }
            }
        }
        public void resolveObject(DataObject dataObject) {
            if (dataObject.getBaseListInfo().size() > maxTagsValue) {
                select.remove(dataObject);
                filter.remove(dataObject);
            }
        }
    },
    CUSTOM {
        public void apply() {
            if (infoListWhite.isEmpty() && infoListBlack.isEmpty()) {
                filter.setAll(mainListData);
            } else {
                filter.clear();
                for (DataObject dataObject : mainListData) {
                    BaseListInfo dataObjectInfoList = dataObject.getBaseListInfo();
                    if (isWhitelistOk(dataObjectInfoList) && isBlacklistOk(dataObjectInfoList)) {
                        filter.add(dataObject);
                    }
                }
            }
        }
        public void resolveObject(DataObject dataObject) {
            BaseListInfo dataObjectInfoList = dataObject.getBaseListInfo();
            if (!isWhitelistOk(dataObjectInfoList) || !isBlacklistOk(dataObjectInfoList)) {
                select.remove(dataObject);
                filter.remove(dataObject);
            }
        }
    };

    private static int maxTagsValue = 0;
    private static boolean isWhitelistOk(BaseListInfo baseListInfo) {
        Filter.FilterMode whitelistMode = filter.getWhitelistMode();
        if (infoListWhite.isEmpty()) {
            return true;
        } else if (whitelistMode.equals(Filter.FilterMode.All) && baseListInfo.containsAll(infoListWhite)) {
            return true;
        } else if (whitelistMode.equals(Filter.FilterMode.Any)) {
            for (InfoObject infoObject : infoListWhite) {
                if (baseListInfo.contains(infoObject)) {
                    return true;
                }
            }
        }

        return false;
    }
    private static boolean isBlacklistOk(BaseListInfo baseListInfo) {
        Filter.FilterMode blacklistMode = filter.getBlacklistMode();
        if (infoListBlack.isEmpty()) {
            return true;
        } else if (blacklistMode.equals(Filter.FilterMode.All) && baseListInfo.containsAll(infoListBlack)) {
            return false;
        } else if (blacklistMode.equals(Filter.FilterMode.Any)) {
            for (InfoObject infoObject : infoListBlack) {
                if (baseListInfo.contains(infoObject)) {
                    return false;
                }
            }
        }

        return true;
    }
    public static void setMaxTagsValue(int value) {
        maxTagsValue = value;
    }
    public void apply() {
        logger.error(this, "default apply() reached");
    }
    public void resolveObject(DataObject dataObject) {
        logger.debug(this, "default resolveObject() reached");
    }
}
