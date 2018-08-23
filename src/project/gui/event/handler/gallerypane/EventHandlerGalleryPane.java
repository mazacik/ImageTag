package project.gui.event.handler.gallerypane;

import project.gui.component.gallerypane.GalleryPane;

public abstract class EventHandlerGalleryPane {
    public static void onWidthChange() {
        GalleryPane.calculateTilePaneHGap();
    }
}
