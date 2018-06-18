package project.gui.component.gallery;

import javafx.scene.control.ScrollPane;
import javafx.scene.layout.TilePane;
import project.backend.Settings;
import project.database.DatabaseItem;
import project.gui.component.gallery.part.GalleryTile;
import project.gui.component.preview.PreviewPaneBack;
import project.gui.component.right.RightPaneBack;
import project.gui.component.top.TopPaneFront;

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
        GalleryTile.generateEffect(currentFocusedItem);

        /* remove old marker */
        if (previousFocusedItem != null)
            GalleryTile.generateEffect(previousFocusedItem);

        TopPaneFront.getInstance().getInfoLabelMenu().setText(databaseItem.getName());
        RightPaneBack.getInstance().reloadContent();
        GalleryPaneBack.getInstance().reloadContent();
        PreviewPaneBack.getInstance().reloadContent();
    }

    /* getters */
    public TilePane getTilePane() {
        return tilePane;
    }

    public DatabaseItem getCurrentFocusedItem() {
        return currentFocusedItem;
    }
}
