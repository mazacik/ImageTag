package project.gui.component.gallery;

import javafx.scene.layout.TilePane;
import project.backend.Settings;
import project.database.ItemDatabase;
import project.database.part.DatabaseItem;

public class GalleryPaneListener {
    /* lazy singleton */
    private static GalleryPaneListener instance;
    public static GalleryPaneListener getInstance() {
        if (instance == null) instance = new GalleryPaneListener();
        return instance;
    }

    /* imports */
    private final GalleryPane galleryPane = GalleryPane.getInstance();
    private final TilePane tilePane = galleryPane.getTilePane();

    /* constructors */
    private GalleryPaneListener() {
        setOnScrollListener();
        setWidthPropertyListener();
    }

    /* private methods */
    private void recalculateHgap() {
        int tilePaneWidth = (int) tilePane.getWidth();
        int prefTileWidth = (int) tilePane.getPrefTileWidth();
        int columnCount = tilePaneWidth / prefTileWidth;
        if (columnCount != 0)
            tilePane.setHgap(tilePaneWidth % prefTileWidth / columnCount);
    }

    /* event methods */
    private void setOnScrollListener() {
        int galleryIconSizeMax = Settings.getGalleryIconSizeMax();
        int galleryIconSizeMin = Settings.getGalleryIconSizeMin();
        int galleryIconSizePref = Settings.getGalleryIconSizePref();

        tilePane.setOnScroll(event -> {
            if (event.isControlDown()) {
                event.consume();

                if (event.getDeltaY() < 0) {
                    Settings.setGalleryIconSizePref(Settings.getGalleryIconSizePref() - 10);
                    if (galleryIconSizePref < galleryIconSizeMin)
                        Settings.setGalleryIconSizePref(galleryIconSizeMin);
                } else {
                    Settings.setGalleryIconSizePref(Settings.getGalleryIconSizePref() + 10);
                    if (galleryIconSizePref > galleryIconSizeMax)
                        Settings.setGalleryIconSizePref(galleryIconSizeMax);
                }

                tilePane.setPrefTileWidth(galleryIconSizePref);
                tilePane.setPrefTileHeight(galleryIconSizePref);

                for (DatabaseItem databaseItem : ItemDatabase.getDatabaseItems()) {
                    databaseItem.getGalleryTile().setFitWidth(galleryIconSizePref);
                    databaseItem.getGalleryTile().setFitHeight(galleryIconSizePref);
                }
                recalculateHgap();
            }
        });
    }

    private void setWidthPropertyListener() {
        tilePane.widthProperty().addListener((observable, oldValue, newValue) -> recalculateHgap());
    }
}
