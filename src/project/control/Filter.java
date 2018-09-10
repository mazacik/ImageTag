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
            if (maxTagsValue == 0) return; // todo exception
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
            DataCollection dataCollectionFiltered = FilterControl.getCollection();
            DataCollection dataCollection = DataControl.getCollection();

            if (whitelist.isEmpty() && blacklist.isEmpty()) {
                dataCollectionFiltered.setAll(dataCollection);
            } else {
                dataCollectionFiltered.clear();
                for (DataObject dataIterator : dataCollection) {
                    TagCollection dataIteratorTagCollection = dataIterator.getTagCollection();
                    if (whitelist.isEmpty() || dataIteratorTagCollection.containsAll(whitelist)) {
                        if (blacklist.isEmpty()) {
                            dataCollectionFiltered.add(dataIterator);
                        } else {
                            boolean isValid = true;
                            for (TagObject tagObject : blacklist) {
                                if (dataIteratorTagCollection.contains(tagObject)) {
                                    isValid = false;
                                    break;
                                }
                            }
                            if (isValid) dataCollectionFiltered.add(dataIterator);
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
        // todo exception
    }

    /* set */
    public static void setMaxTagsValue(int value) {
        maxTagsValue = value;
    }
}
