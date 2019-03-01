package control.filter;

import database.list.InfoList;
import database.object.DataObject;
import database.object.InfoObject;
import system.InstanceRepo;

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
                if (dataObject.getInfoList().size() <= maxTagsValue) {
                    filter.add(dataObject);
                }
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
                    InfoList dataObjectInfoList = dataObject.getInfoList();
                    if (isWhitelistOk(dataObjectInfoList) && isBlacklistOk(dataObjectInfoList)) {
                        filter.add(dataObject);
                    }
                }
            }
        }
    };

    private static int maxTagsValue = 0;
    private static boolean isWhitelistOk(InfoList infoList) {
        Filter.FilterMode whitelistMode = filter.getWhitelistMode();
        if (infoListWhite.isEmpty()) {
            return true;
        } else if (whitelistMode.equals(Filter.FilterMode.All) && infoList.containsAll(infoListWhite)) {
            return true;
        } else if (whitelistMode.equals(Filter.FilterMode.Any)) {
            for (InfoObject infoObject : infoListWhite) {
                if (infoList.contains(infoObject)) {
                    return true;
                }
            }
        }

        return false;
    }
    private static boolean isBlacklistOk(InfoList infoList) {
        Filter.FilterMode blacklistMode = filter.getBlacklistMode();
        if (infoListBlack.isEmpty()) {
            return true;
        } else if (blacklistMode.equals(Filter.FilterMode.All) && infoList.containsAll(infoListBlack)) {
            return false;
        } else if (blacklistMode.equals(Filter.FilterMode.Any)) {
            for (InfoObject infoObject : infoListBlack) {
                if (infoList.contains(infoObject)) {
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
}
