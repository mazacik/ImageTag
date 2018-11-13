package gui.event.gallerypane;

import utils.MainUtil;

public class GalleryPaneEvent implements MainUtil {
    public GalleryPaneEvent() {
        onWidthChange();
    }

    private void onWidthChange() {
        galleryPane.getTilePane().widthProperty().addListener((observable, oldValue, newValue) -> galleryPane.calculateTilePaneHGap());
    }
}
