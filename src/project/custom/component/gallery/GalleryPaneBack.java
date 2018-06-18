package project.custom.component.gallery;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.layout.TilePane;
import project.common.Database;
import project.common.Utility;
import project.database.DatabaseItem;

public class GalleryPaneBack {
    /* lazy singleton */
    private static GalleryPaneBack instance;
    public static GalleryPaneBack getInstance() {
        if (instance == null) instance = new GalleryPaneBack();
        return instance;
    }

    /* imports */
    private final TilePane tilePane = GalleryPaneFront.getInstance().getTilePane();
    private final ObservableList<Node> galleryTiles = tilePane.getChildren();

    /* constructors */
    private GalleryPaneBack() {
        GalleryPaneListener.getInstance();
    }

    /* public methods */
    public void reloadContent() {
        if (Utility.isPreviewFullscreen()) return;
        galleryTiles.clear();
        for (DatabaseItem databaseItem : Database.getDatabaseItemsFiltered())
            galleryTiles.add(databaseItem.getGalleryTile());
    }

    public int getColumnCount() {
        int tilePaneWidth = (int) tilePane.getWidth();
        int prefTileWidth = (int) tilePane.getPrefTileWidth();
        return tilePaneWidth / prefTileWidth;
    }
}
