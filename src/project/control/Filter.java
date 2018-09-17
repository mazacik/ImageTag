package project.control;

import project.database.control.DataControl;
import project.database.control.TagControl;
import project.database.object.DataCollection;
import project.database.object.DataObject;
import project.database.object.TagCollection;
import project.database.object.TagObject;

public enum Filter {
    SHOW_EVERYTHING {
        public void apply() {
            FilterControl.getWhitelist().clear();
            FilterControl.getBlacklist().clear();
            FilterControl.getCollection().setAll(DataControl.getCollection());
        }
    },
    SHOW_UNTAGGED {
        public void apply() {
            FilterControl.getWhitelist().clear();
            FilterControl.getBlacklist().setAll(TagControl.getCollection());
            DataCollection dataCollectionFiltered = FilterControl.getCollection();
            dataCollectionFiltered.clear();
            for (DataObject dataObject : DataControl.getCollection()) {
                if (dataObject.getTagCollection().size() == 0)
                    dataCollectionFiltered.add(dataObject);
            }
        }
    },
    SHOW_MAX_X_TAGS {
        public void apply() {
            FilterControl.getWhitelist().clear();
            FilterControl.getBlacklist().clear();
            DataCollection dataCollectionFiltered = FilterControl.getCollection();
            dataCollectionFiltered.clear();
            for (DataObject dataObject : DataControl.getCollection()) {
                if (dataObject.getTagCollection().size() <= maxTagsValue) {
                    dataCollectionFiltered.add(dataObject);
                }
            }
        }
    },
    CUSTOM {
        public void apply() {
            TagCollection whitelist = FilterControl.getWhitelist();
            TagCollection blacklist = FilterControl.getBlacklist();
            DataCollection dataCollection = DataControl.getCollection();
            DataCollection dataCollectionFiltered = FilterControl.getCollection();

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

    /* vars */
    private static int maxTagsValue = 0;

    /* public */
    public void apply() {
        throw new RuntimeException();
    }

    /* boolean */
    private static boolean isWhitelistOk(TagCollection whitelist, TagCollection tagCollection) {
        FilterMode whitelistMode = FilterControl.getWhitelistMode();
        if (whitelist.isEmpty()) {
            return true;
        } else if (whitelistMode.equals(FilterMode.All) && tagCollection.containsAll(whitelist)) {
            return true;
        } else if (whitelistMode.equals(FilterMode.Any)) {
            for (TagObject tagObject : whitelist) {
                if (tagCollection.contains(tagObject)) {
                    return true;
                }
            }
        }

        return false;
    }
    private static boolean isBlacklistOk(TagCollection blacklist, TagCollection tagCollection) {
        FilterMode blacklistMode = FilterControl.getBlacklistMode();
        if (blacklist.isEmpty()) {
            return true;
        } else if (blacklistMode.equals(FilterMode.All) && tagCollection.containsAll(blacklist)) {
            return false;
        } else if (blacklistMode.equals(FilterMode.Any)) {
            for (TagObject tagObject : blacklist) {
                if (tagCollection.contains(tagObject)) {
                    return false;
                }
            }
        }

        return true;
    }

    /* set */
    public static void setMaxTagsValue(int value) {
        maxTagsValue = value;
    }
}
