package project.frontend.singleton;

import javafx.scene.control.ScrollPane;
import javafx.scene.effect.InnerShadow;
import javafx.scene.layout.TilePane;
import javafx.scene.paint.Color;
import project.backend.common.Settings;
import project.backend.database.DatabaseItem;

public class GalleryPaneFront extends ScrollPane {
    private static final GalleryPaneFront instance = new GalleryPaneFront();

    private final TilePane tilePane = new TilePane();
    private final InnerShadow highlightEffect = new InnerShadow();

    private int galleryIconSizePref = Settings.getGalleryIconSizePref();


    private GalleryPaneFront() {
        setHbarPolicy(ScrollBarPolicy.NEVER);
        setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
        setMinViewportWidth(galleryIconSizePref);
        setFitToWidth(true);

        tilePane.setVgap(3);
        tilePane.setPrefTileWidth(galleryIconSizePref);
        tilePane.setPrefTileHeight(galleryIconSizePref);

        highlightEffect.setColor(Color.RED);
        highlightEffect.setOffsetX(0);
        highlightEffect.setOffsetY(0);
        highlightEffect.setWidth(5);
        highlightEffect.setHeight(5);
        highlightEffect.setChoke(1);

        setContent(tilePane);
    }

    public void setGalleryTileHighlight(DatabaseItem databaseItem, boolean visible) {
        if (visible)
            databaseItem.getGalleryTile().setEffect(highlightEffect);
        else
            databaseItem.getGalleryTile().setEffect(null);
    }

    public TilePane getTilePane() {
        return tilePane;
    }

    public static GalleryPaneFront getInstance() {
        return instance;
    }
}
