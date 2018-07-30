package project.enums;

import project.control.FilterControl;
import project.control.ReloadControl;
import project.database.control.DataElementControl;
import project.database.control.TagElementControl;
import project.database.element.DataElement;
import project.gui.component.GalleryPane.GalleryPane;

public enum FilterCollection {
    SHOW_EVERYTHING {
        public void activate() {
            FilterControl.getTagElementWhitelist().clear();
            FilterControl.getTagElementBlacklist().clear();
        }
    },
    SHOW_UNTAGGED {
        public void activate() {
            FilterControl.getTagElementWhitelist().clear();
            FilterControl.getTagElementBlacklist().clear();
            FilterControl.getTagElementBlacklist().addAll(TagElementControl.getTagElements());
        }
    },
    SHOW_MAX_X_TAGS {
        public void activate() {
            if (maxTagsValue == 0) return;
            FilterControl.getValidDataElements().clear();
            FilterControl.getTagElementWhitelist().clear();
            FilterControl.getTagElementBlacklist().clear();
            for (DataElement dataElement : DataElementControl.getDataElementsCopy()) {
                if (dataElement.getTagElements().size() <= maxTagsValue) {
                    FilterControl.getValidDataElements().add(dataElement);
                }
            }
            ReloadControl.requestComponentReload(GalleryPane.class);
        }
    },
    CUSTOM;

    /* vars */
    private static int maxTagsValue = 0;

    /* public */
    public void activate() {}

    /* set */
    public static void setMaxTagsValue(int value) {
        maxTagsValue = value;
    }
}
