package project.userinput.gui;

import javafx.scene.layout.TilePane;
import project.database.control.DataElementControl;
import project.database.element.DataElement;
import project.gui.component.GalleryPane;
import project.helper.Settings;

public abstract class UserInputGalleryPane {
    public static void initialize() {
        setOnScrollListener_tilePane();
        setWidthPropertyListener_tilePane();
    }

    private static void setOnScrollListener_tilePane() {
        int galleryIconSizeMax = Settings.getGalleryIconSizeMax();
        int galleryIconSizeMin = Settings.getGalleryIconSizeMin();
        int galleryIconSizePref = Settings.getGalleryIconSizePref();

        TilePane tilePane = GalleryPane.getTilePane();

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

                for (DataElement dataElement : DataElementControl.getDataElementsLive()) {
                    dataElement.getGalleryTile().setFitWidth(galleryIconSizePref);
                    dataElement.getGalleryTile().setFitHeight(galleryIconSizePref);
                }
                GalleryPane.recalculateHgap();
            }
        });
    }
    private static void setWidthPropertyListener_tilePane() {
        GalleryPane.getTilePane().widthProperty().addListener((observable, oldValue, newValue) -> GalleryPane.recalculateHgap());
    }
}
