package project.backend.listener;

import javafx.scene.layout.TilePane;
import project.backend.common.Settings;
import project.backend.database.Database;
import project.backend.database.DatabaseItem;
import project.frontend.singleton.GalleryPaneFront;

public class GalleryPaneListener {
    private static final GalleryPaneListener instance = new GalleryPaneListener();

    private final GalleryPaneFront galleryPaneFront = GalleryPaneFront.getInstance();
    private final TilePane tilePane = galleryPaneFront.getTilePane();


    private GalleryPaneListener() {
        setOnScrollListener();
        setWidthPropertyListener();
    }

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

                for (DatabaseItem databaseItem : Database.getDatabaseItems()) {
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

    private void recalculateHgap() {
        int tilePaneWidth = (int) tilePane.getWidth();
        int prefTileWidth = (int) tilePane.getPrefTileWidth();
        int columnCount = tilePaneWidth / prefTileWidth;
        if (columnCount != 0)
            tilePane.setHgap(tilePaneWidth % prefTileWidth / columnCount);
    }

    public static GalleryPaneListener getInstance() {
        return instance;
    }
}
