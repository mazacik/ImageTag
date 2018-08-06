package project.gui.event.listener.gallerypane;

import project.gui.component.gallerypane.GalleryTile;
import project.gui.event.handler.gallerypane.EventHandlerGalleryTile;

public abstract class EventListenerGalleryTile {
    public static void onMouseClick(GalleryTile galleryTile) {
        galleryTile.setOnMouseClicked(event -> {
            switch (event.getButton()) {
                case PRIMARY:
                    EventHandlerGalleryTile.onLeftClick(galleryTile);
                    break;
                case SECONDARY:
                    EventHandlerGalleryTile.onRightClick(galleryTile, event);
                    break;
                default:
                    break;
            }
        });
    }
}
