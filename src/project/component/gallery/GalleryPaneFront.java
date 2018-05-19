package project.component.gallery;

import javafx.scene.control.ScrollPane;
import javafx.scene.layout.TilePane;
import project.common.Settings;
import project.database.DatabaseItem;

public class GalleryPaneFront extends ScrollPane {
    /* lazy singleton */
    private static GalleryPaneFront instance;
    public static GalleryPaneFront getInstance() {
        if (instance == null) instance = new GalleryPaneFront();
        return instance;
    }

    /* imports */
    private final int galleryIconSizePref = Settings.getGalleryIconSizePref();

    /* components */
    private final TilePane tilePane = new TilePane();

    /* variables */
    private DatabaseItem currentFocusedItem = null;
    private DatabaseItem previousFocusedItem = null;

    /* constructors */
    private GalleryPaneFront() {
        setHbarPolicy(ScrollBarPolicy.NEVER);
        setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
        setMinViewportWidth(galleryIconSizePref);
        setFitToWidth(true);

        tilePane.setVgap(3);
        tilePane.setPrefTileWidth(galleryIconSizePref);
        tilePane.setPrefTileHeight(galleryIconSizePref);

        setContent(tilePane);
    }

    /* public methods */
    public void focusTile(DatabaseItem databaseItem) {
        /* store old marker position */
        if (currentFocusedItem != null)
            previousFocusedItem = currentFocusedItem;

        /* apply new marker */
        currentFocusedItem = databaseItem;
        currentFocusedItem.getGalleryTile().generateEffect(currentFocusedItem);

        /* remove old marker */
        if (previousFocusedItem != null)
            previousFocusedItem.getGalleryTile().generateEffect(previousFocusedItem);
    }

    /* getters */
    public TilePane getTilePane() {
        return tilePane;
    }

    public DatabaseItem getCurrentFocusedItem() {
        return currentFocusedItem;
    }

    /* setters */
    public void setCurrentFocusedItem(DatabaseItem databaseItem) {
        currentFocusedItem = databaseItem;
    }
}
