package control.filter;

import database.list.BaseListInfo;
import database.object.DataObject;
import database.object.InfoObject;
import utils.InstanceRepo;

public enum FilterTemplate implements InstanceRepo {
    SHOW_EVERYTHING {
        public void apply() {
            infoListWhite.clear();
            infoListBlack.clear();
            filter.setAll(mainListData);
        }
    },
    SHOW_UNTAGGED {
        public void apply() {
            infoListWhite.clear();
            infoListBlack.setAll(mainListInfo);
            filter.clear();
            for (DataObject dataObject : mainListData) {
                if (dataObject.getBaseListInfo().size() == 0)
                    filter.add(dataObject);
            }
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
        throw new RuntimeException();
    }
}
