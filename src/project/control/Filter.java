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
            FilterControl filterControl = Control.getFilterControl();
            filterControl.getWhitelist().clear();
            filterControl.getBlacklist().clear();
            filterControl.getCollection().setAll(DataControl.getCollection());
        }
    },
    SHOW_UNTAGGED {
        public void apply() {
            FilterControl filterControl = Control.getFilterControl();
            filterControl.getWhitelist().clear();
            filterControl.getBlacklist().setAll(TagControl.getCollection());
            DataCollection dataCollectionFiltered = filterControl.getCollection();
            dataCollectionFiltered.clear();
            for (DataObject dataObject : DataControl.getCollection()) {
                if (dataObject.getTagCollection().size() == 0)
                    dataCollectionFiltered.add(dataObject);
            }
        }
    },
    SHOW_MAX_X_TAGS {
        public void apply() {
            FilterControl filterControl = Control.getFilterControl();
            filterControl.getWhitelist().clear();
            filterControl.getBlacklist().clear();
            DataCollection dataCollectionFiltered = filterControl.getCollection();
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
            FilterControl filterControl = Control.getFilterControl();
            TagCollection whitelist = filterControl.getWhitelist();
            TagCollection blacklist = filterControl.getBlacklist();
            DataCollection dataCollection = DataControl.getCollection();
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
        FilterControl.FilterMode whitelistMode = Control.getFilterControl().getWhitelistMode();
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
        FilterControl.FilterMode blacklistMode = Control.getFilterControl().getBlacklistMode();
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
