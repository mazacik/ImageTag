package project.control.filter;

import project.database.object.DataObject;
import project.database.object.TagCollection;
import project.database.object.TagObject;
import project.utils.MainUtil;

public enum FilterTemplate implements MainUtil {
    SHOW_EVERYTHING {
        public void apply() {
            whitelist.clear();
            blacklist.clear();
            filter.setAll(mainData);
        }
    },
    SHOW_UNTAGGED {
        public void apply() {
            whitelist.clear();
            blacklist.setAll(mainTags);
            filter.clear();
            for (DataObject dataObject : mainData) {
                if (dataObject.getTagCollection().size() == 0)
                    filter.add(dataObject);
            }
        }
    },
    SHOW_MAX_X_TAGS {
        public void apply() {
            whitelist.clear();
            blacklist.clear();
            filter.clear();
            for (DataObject dataObject : mainData) {
                if (dataObject.getTagCollection().size() <= maxTagsValue) {
                    filter.add(dataObject);
                }
            }
        }
    },
    CUSTOM {
        public void apply() {
            if (whitelist.isEmpty() && blacklist.isEmpty()) {
                filter.setAll(mainData);
            } else {
                filter.clear();
                for (DataObject dataObject : mainData) {
                    TagCollection dataObjectTagCollection = dataObject.getTagCollection();
                    if (isWhitelistOk(dataObjectTagCollection) && isBlacklistOk(dataObjectTagCollection)) {
                        filter.add(dataObject);
                    }
                }
            }
        }
    };

    private static int maxTagsValue = 0;

    public void apply() {
        throw new RuntimeException();
    }

    private static boolean isWhitelistOk(TagCollection tagCollection) {
        Filter.FilterMode whitelistMode = filter.getWhitelistMode();
        if (whitelist.isEmpty()) {
            return true;
        } else if (whitelistMode.equals(Filter.FilterMode.All) && tagCollection.containsAll(whitelist)) {
            return true;
        } else if (whitelistMode.equals(Filter.FilterMode.Any)) {
            for (TagObject tagObject : whitelist) {
                if (tagCollection.contains(tagObject)) {
                    return true;
                }
            }
        }

        return false;
    }
    private static boolean isBlacklistOk(TagCollection tagCollection) {
        Filter.FilterMode blacklistMode = filter.getBlacklistMode();
        if (blacklist.isEmpty()) {
            return true;
        } else if (blacklistMode.equals(Filter.FilterMode.All) && tagCollection.containsAll(blacklist)) {
            return false;
        } else if (blacklistMode.equals(Filter.FilterMode.Any)) {
            for (TagObject tagObject : blacklist) {
                if (tagCollection.contains(tagObject)) {
                    return false;
                }
            }
        }

        return true;
    }

    public static void setMaxTagsValue(int value) {
        maxTagsValue = value;
    }
}
