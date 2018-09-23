package project.control;

import project.MainUtils;
import project.database.object.DataCollection;
import project.database.object.DataObject;
import project.database.object.TagCollection;
import project.database.object.TagObject;

public enum Filter implements MainUtils {
    SHOW_EVERYTHING {
        public void apply() {
            filterControl.getWhitelist().clear();
            filterControl.getBlacklist().clear();
            filterControl.getCollection().setAll(dataControl.getCollection());
        }
    },
    SHOW_UNTAGGED {
        public void apply() {
            filterControl.getWhitelist().clear();
            filterControl.getBlacklist().setAll(tagControl.getCollection());
            DataCollection dataCollectionFiltered = filterControl.getCollection();
            dataCollectionFiltered.clear();
            for (DataObject dataObject : dataControl.getCollection()) {
                if (dataObject.getTagCollection().size() == 0)
                    dataCollectionFiltered.add(dataObject);
            }
        }
    },
    SHOW_MAX_X_TAGS {
        public void apply() {
            filterControl.getWhitelist().clear();
            filterControl.getBlacklist().clear();
            DataCollection dataCollectionFiltered = filterControl.getCollection();
            dataCollectionFiltered.clear();
            for (DataObject dataObject : dataControl.getCollection()) {
                if (dataObject.getTagCollection().size() <= maxTagsValue) {
                    dataCollectionFiltered.add(dataObject);
                }
            }
        }
    },
    CUSTOM {
        public void apply() {
            TagCollection whitelist = filterControl.getWhitelist();
            TagCollection blacklist = filterControl.getBlacklist();
            DataCollection dataCollection = dataControl.getCollection();
            DataCollection dataCollectionFiltered = filterControl.getCollection();

            if (whitelist.isEmpty() && blacklist.isEmpty()) {
                dataCollectionFiltered.setAll(dataCollection);
            } else {
                dataCollectionFiltered.clear();
                for (DataObject dataObject : dataCollection) {
                    TagCollection dataObjectTagCollection = dataObject.getTagCollection();
                    if (isWhitelistOk(whitelist, dataObjectTagCollection)) {
                        if (isBlacklistOk(blacklist, dataObjectTagCollection)) {
                            dataCollectionFiltered.add(dataObject);
                        }
                    }
                }
            }
        }
    };

    private static int maxTagsValue = 0;

    public void apply() {
        throw new RuntimeException();
    }

    private static boolean isWhitelistOk(TagCollection whitelist, TagCollection tagCollection) {
        FilterControl.FilterMode whitelistMode = filterControl.getWhitelistMode();
        if (whitelist.isEmpty()) {
            return true;
        } else if (whitelistMode.equals(FilterControl.FilterMode.All) && tagCollection.containsAll(whitelist)) {
            return true;
        } else if (whitelistMode.equals(FilterControl.FilterMode.Any)) {
            for (TagObject tagObject : whitelist) {
                if (tagCollection.contains(tagObject)) {
                    return true;
                }
            }
        }

        return false;
    }
    private static boolean isBlacklistOk(TagCollection blacklist, TagCollection tagCollection) {
        FilterControl.FilterMode blacklistMode = filterControl.getBlacklistMode();
        if (blacklist.isEmpty()) {
            return true;
        } else if (blacklistMode.equals(FilterControl.FilterMode.All) && tagCollection.containsAll(blacklist)) {
            return false;
        } else if (blacklistMode.equals(FilterControl.FilterMode.Any)) {
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
