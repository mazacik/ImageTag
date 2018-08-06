package project.gui.event.handler.gallerypane;

import javafx.scene.input.ScrollEvent;
import project.gui.component.gallerypane.GalleryPane;

public abstract class EventHandlerGalleryPane {
    public static void onScroll(ScrollEvent scrollEvent) {
        if (scrollEvent.isControlDown()) {
            scrollEvent.consume();
            if (scrollEvent.getDeltaY() < 0) {
                GalleryPane.makeTilesLarger();
            } else {
                GalleryPane.makeTilesSmaller();
            }
        }
    }
    public static void onWidthChange() {
        GalleryPane.calculateTilePaneHGap();
    }
}
