package control.filter;

import database.list.InfoObjectList;
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
            filter.setAll(mainDataList);
        }
    },
    SHOW_MAX_X_TAGS {
        public void apply() {
            infoListWhite.clear();
            infoListBlack.clear();
            filter.clear();
            for (DataObject dataObject : mainDataList) {
                if (dataObject.getInfoObjectList().size() <= maxTagsValue) {
                    filter.add(dataObject);
                }
            }
        }
    },
    CUSTOM {
        public void apply() {
            if (infoListWhite.isEmpty() && infoListBlack.isEmpty()) {
                filter.setAll(mainDataList);
            } else {
                filter.clear();
                for (DataObject dataObject : mainDataList) {
                    InfoObjectList infoObjectList = dataObject.getInfoObjectList();
                    if (isWhitelistOk(infoObjectList) && isBlacklistOk(infoObjectList)) {
                        filter.add(dataObject);
                    }
                }
            }
        }
    };

    private static int maxTagsValue = 0;
    private static boolean isWhitelistOk(InfoObjectList infoObjectList) {
        Filter.FilterMode whitelistMode = filter.getWhitelistMode();
        if (infoListWhite.isEmpty()) {
            return true;
        } else if (whitelistMode.equals(Filter.FilterMode.All) && infoObjectList.containsAll(infoListWhite)) {
            return true;
        } else if (whitelistMode.equals(Filter.FilterMode.Any)) {
            for (InfoObject infoObject : infoListWhite) {
                if (infoObjectList.contains(infoObject)) {
                    return true;
                }
            }
        }

        return false;
    }
    private static boolean isBlacklistOk(InfoObjectList infoObjectList) {
        Filter.FilterMode blacklistMode = filter.getBlacklistMode();
        if (infoListBlack.isEmpty()) {
            return true;
        } else if (blacklistMode.equals(Filter.FilterMode.All) && infoObjectList.containsAll(infoListBlack)) {
            return false;
        } else if (blacklistMode.equals(Filter.FilterMode.Any)) {
            for (InfoObject infoObject : infoListBlack) {
                if (infoObjectList.contains(infoObject)) {
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
