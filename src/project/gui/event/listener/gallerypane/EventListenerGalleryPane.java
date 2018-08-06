package project.gui.event.listener.gallerypane;

import javafx.scene.layout.TilePane;
import project.gui.component.gallerypane.GalleryPane;
import project.gui.event.handler.gallerypane.EventHandlerGalleryPane;

public abstract class EventListenerGalleryPane {
    private static final TilePane tilePane = GalleryPane.getTilePane();

    public static void initialize() {
        onScroll();
        onWidthChange();
    }

    private static void onScroll() {
        tilePane.setOnScroll(EventHandlerGalleryPane::onScroll);
    }
    private static void onWidthChange() {
        tilePane.widthProperty().addListener((observable, oldValue, newValue) -> EventHandlerGalleryPane.onWidthChange());
    }
}
