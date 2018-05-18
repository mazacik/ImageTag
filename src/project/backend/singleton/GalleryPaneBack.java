package project.backend.singleton;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import project.backend.database.Database;
import project.backend.database.DatabaseItem;
import project.frontend.Frontend;
import project.frontend.singleton.GalleryPaneFront;

public class GalleryPaneBack {
    private static final GalleryPaneBack instance = new GalleryPaneBack();

    private final ObservableList<Node> galleryTiles = GalleryPaneFront.getInstance().getTilePane().getChildren();


    public void reloadContent() {
        if (Frontend.isPreviewFullscreen()) return;
        galleryTiles.clear();
        for (DatabaseItem databaseItem : Database.getDatabaseItemsFiltered())
            galleryTiles.add(databaseItem.getGalleryTile());
    }

    public static GalleryPaneBack getInstance() {
        return instance;
    }
}
