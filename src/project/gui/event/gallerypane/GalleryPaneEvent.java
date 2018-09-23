package project.gui.event.gallerypane;

import project.MainUtils;

public class GalleryPaneEvent implements MainUtils {
    public GalleryPaneEvent() {
        onWidthChange();
    }

    private void onWidthChange() {
        galleryPane.getTilePane().widthProperty().addListener((observable, oldValue, newValue) -> galleryPane.calculateTilePaneHGap());
    }
}
