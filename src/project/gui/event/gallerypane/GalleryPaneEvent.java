package project.gui.event.gallerypane;

import project.gui.component.gallerypane.GalleryPane;

public abstract class GalleryPaneEvent {
    public static void initialize() {
        onWidthChange();
    }

    private static void onWidthChange() {
        GalleryPane.getTilePane().widthProperty().addListener((observable, oldValue, newValue) -> GalleryPane.calculateTilePaneHGap());
    }
}
