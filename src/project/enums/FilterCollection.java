package project.enums;

import project.control.FilterControl;
import project.control.ReloadControl;
import project.database.control.DataControl;
import project.database.control.TagControl;
import project.database.element.DataObject;
import project.gui.component.gallerypane.GalleryPane;

public enum FilterCollection {
    SHOW_EVERYTHING {
        public void activate() {
            FilterControl.getWhitelist().clear();
            FilterControl.getBlacklist().clear();
        }
    },
    SHOW_UNTAGGED {
        public void activate() {
            FilterControl.getWhitelist().clear();
            FilterControl.getBlacklist().clear();
            FilterControl.getBlacklist().addAll(TagControl.getCollection());
        }
    },
    SHOW_MAX_X_TAGS {
        public void activate() {
            if (maxTagsValue == 0) return;
            FilterControl.getCollection().clear();
            FilterControl.getWhitelist().clear();
            FilterControl.getBlacklist().clear();
            for (DataObject dataObject : DataControl.getDataCollectionCopy()) {
                if (dataObject.getTagCollection().size() <= maxTagsValue) {
                    FilterControl.getCollection().add(dataObject);
                }
            }
            ReloadControl.request(GalleryPane.class);
        }
    },
    CUSTOM {
        public void activate() {

        }
    };

    /* vars */
    private static int maxTagsValue = 0;

    /* public */
    public void activate() {}

    /* set */
    public static void setMaxTagsValue(int value) {
        maxTagsValue = value;
    }
}
