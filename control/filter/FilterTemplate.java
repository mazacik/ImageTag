package control.filter;

import database.object.DataObject;
import database.list.InfoList;
import database.object.InfoObject;
import utils.MainUtil;

public enum FilterTemplate implements MainUtil {
    SHOW_EVERYTHING {
        public void apply() {
            infoListWhite.clear();
            infoListBlack.clear();
            filter.setAll(dataListMain);
        }
    },
    SHOW_UNTAGGED {
        public void apply() {
            infoListWhite.clear();
            infoListBlack.setAll(infoListMain);
            filter.clear();
            for (DataObject dataObject : dataListMain) {
                if (dataObject.getInfoList().size() == 0)
                    filter.add(dataObject);
            }
        }
    },
    SHOW_MAX_X_TAGS {
        public void apply() {
            infoListWhite.clear();
            infoListBlack.clear();
            filter.clear();
            for (DataObject dataObject : dataListMain) {
                if (dataObject.getInfoList().size() <= maxTagsValue) {
                    filter.add(dataObject);
                }
            }
        }
    },
    CUSTOM {
        public void apply() {
            if (infoListWhite.isEmpty() && infoListBlack.isEmpty()) {
                filter.setAll(dataListMain);
            } else {
                filter.clear();
                for (DataObject dataObject : dataListMain) {
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
        throw new RuntimeException();
    }
}
