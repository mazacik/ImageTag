package project.component.gallery;

import javafx.scene.control.ScrollPane;
import javafx.scene.effect.Blend;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.ColorInput;
import javafx.scene.layout.TilePane;
import javafx.scene.paint.Color;
import project.common.Settings;
import project.database.Database;
import project.database.DatabaseItem;

import java.util.ArrayList;

public class GalleryPaneFront extends ScrollPane {
    /* lazy singleton */
    private static GalleryPaneFront instance;
    public static GalleryPaneFront getInstance() {
        if (instance == null) instance = new GalleryPaneFront();
        return instance;
    }

    /* imports */
    private final int galleryIconSizePref = Settings.getGalleryIconSizePref();
    private final ArrayList<DatabaseItem> databaseItemsSelected = Database.getDatabaseItemsSelected();

    /* components */
    private final TilePane tilePane = new TilePane();

    /* variables */
    private DatabaseItem focusedItem = null;
    private final Blend focusMarker = buildFocusPositionMarker();

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
    public void markGalleryTile(DatabaseItem databaseItem) {
        /* remove old marker */
        if (focusedItem != null) {
            if (databaseItemsSelected.contains(focusedItem))
                focusedItem.getGalleryTile().setHighlight(true);
            else
                focusedItem.getGalleryTile().setHighlight(false);
        }

        /* apply new marker */
        focusedItem = databaseItem;
        focusedItem.getGalleryTile().setEffect(focusMarker);
    }

    /* builder methods */
    private Blend buildFocusPositionMarker() {
        int markSize = 6;
        int markPosition = (galleryIconSizePref - markSize) / 2;

        Blend focusMarker = new Blend();
        focusMarker.setTopInput(new ColorInput(markPosition, markPosition, markSize, markSize, Color.RED));
        if (focusedItem != null)
            focusMarker.setBottomInput(focusedItem.getGalleryTile().getEffect());
        focusMarker.setMode(BlendMode.SRC_OVER);
        return focusMarker;
    }

    /* getters */
    public TilePane getTilePane() {
        return tilePane;
    }

    public DatabaseItem getFocusedItem() {
        return focusedItem;
    }

    /* setters */
    public void setFocusedItem(DatabaseItem databaseItem) {
        focusedItem = databaseItem;
        markGalleryTile(focusedItem);
    }
}
