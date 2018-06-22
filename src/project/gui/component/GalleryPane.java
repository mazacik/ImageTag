package project.gui.component;

import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.TilePane;
import project.backend.Settings;
import project.database.ItemDatabase;
import project.database.part.DatabaseItem;
import project.gui.ChangeEvent;
import project.gui.ChangeNotificationHelper;
import project.gui.GUIController;
import project.gui.component.part.GalleryTile;

import java.util.ArrayList;

public class GalleryPane extends ScrollPane implements ChangeNotificationHelper {
    /* change listeners */
    private final ArrayList<ChangeNotificationHelper> changeListeners = new ArrayList<>();

    /* components */
    private final TilePane tilePane = new TilePane();
    private final ObservableList<Node> tilePaneItems = tilePane.getChildren();

    /* variables */
    private DatabaseItem currentFocusedItem = null;
    private DatabaseItem previousFocusedItem = null;

    /* constructors */
    public GalleryPane() {
        int galleryIconSizePref = Settings.getGalleryIconSizePref();

        setHbarPolicy(ScrollBarPolicy.NEVER);
        setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
        setMinViewportWidth(galleryIconSizePref);
        setFitToWidth(true);

        tilePane.setVgap(3);
        tilePane.setPrefTileWidth(galleryIconSizePref);
        tilePane.setPrefTileHeight(galleryIconSizePref);

        setContent(tilePane);

        setOnScrollListener();
        setWidthPropertyListener();

        GUIController.subscribe(this, ChangeEvent.FILTER, ChangeEvent.FOCUS);
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

        GUIController.notifyOfChange(ChangeEvent.FOCUS);
    }

    public void adjustViewportToFocus() {
        if (currentFocusedItem == null) return;

        int columnCount = getColumnCount();
        int focusIndex = ItemDatabase.getDatabaseItemsFiltered().indexOf(currentFocusedItem);
        int focusRow = focusIndex / columnCount;

        Bounds viewportBounds = getViewportBounds();
        Bounds tileBounds = tilePaneItems.get(focusIndex).getBoundsInParent();

        double viewportHeight = viewportBounds.getHeight();
        double contentHeight = tilePane.getHeight() - viewportHeight;
        double rowHeight = tilePane.getPrefTileHeight() + tilePane.getVgap();

        double rowToContentRatio = rowHeight / contentHeight;
        double viewportToContentRatio = viewportHeight / contentHeight;

        double viewportTop = viewportBounds.getMaxY() * -1 + viewportBounds.getHeight();
        double viewportBottom = viewportBounds.getMinY() * -1 + viewportBounds.getHeight();
        double tileTop = tileBounds.getMaxY();
        double tileBottom = tileBounds.getMinY();

        if (viewportTop + rowHeight > tileTop) {
            setVvalue(focusRow * rowToContentRatio);
        } else if (viewportBottom - rowHeight < tileBottom) {
            setVvalue((focusRow + 1) * rowToContentRatio - viewportToContentRatio);
        }
    }

    public void refresh() {
        if (GUIController.isPreviewFullscreen()) return;

        tilePaneItems.clear();

        for (DatabaseItem databaseItem : ItemDatabase.getDatabaseItemsFiltered()) {
            tilePaneItems.add(databaseItem.getGalleryTile());
        }
    }

    /* private methods */
    private void recalculateHgap() {
        int tilePaneWidth = (int) tilePane.getWidth();
        int prefTileWidth = (int) tilePane.getPrefTileWidth();
        int columnCount = tilePaneWidth / prefTileWidth;
        if (columnCount != 0) {
            tilePane.setHgap(tilePaneWidth % prefTileWidth / columnCount);
        }
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

    /* getters */
    public int getColumnCount() {
        int tilePaneWidth = (int) tilePane.getWidth();
        int prefTileWidth = (int) tilePane.getPrefTileWidth();
        return tilePaneWidth / prefTileWidth;
    }
    public DatabaseItem getCurrentFocusedItem() {
        return currentFocusedItem;
    }
    public ArrayList<ChangeNotificationHelper> getChangeListeners() {
        return changeListeners;
    }
}
