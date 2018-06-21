package project.gui.component.gallery;

import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.TilePane;
import project.backend.Settings;
import project.database.ItemDatabase;
import project.database.part.DatabaseItem;
import project.gui.GUIComponent;
import project.gui.GUIController;
import project.gui.component.gallery.part.GalleryTile;

import java.util.ArrayList;

public class GalleryPane extends ScrollPane implements GUIComponent {
    private final ArrayList<GUIComponent> changeListeners = new ArrayList<>();

    private final TilePane tilePane = new TilePane();
    private final ObservableList<Node> tilePaneItems = tilePane.getChildren();

    private DatabaseItem currentFocusedItem = null;
    private DatabaseItem previousFocusedItem = null;

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
    }

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

        GUIController.notifyOfChange(this);

        //GUIStage.getTopPane().getInfoLabelMenu().setText(databaseItem.getName());
        //RightPaneBack.getInstance().reloadContent();
        //GalleryPaneBack.getInstance().reloadContent();
        //PreviewPaneBack.getInstance().reloadContent();
    }

    public void adjustViewportPositionToFocus() {
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

    public void addToSubscribers(GUIComponent subscriber) {
        if (!changeListeners.contains(subscriber))
            changeListeners.add(subscriber);
    }

    public int getColumnCount() {
        int tilePaneWidth = (int) tilePane.getWidth();
        int prefTileWidth = (int) tilePane.getPrefTileWidth();
        return tilePaneWidth / prefTileWidth;
    }

    public ArrayList<GUIComponent> getChangeListeners() {
        return changeListeners;
    }

    public TilePane getTilePane() {
        return tilePane;
    }

    public DatabaseItem getCurrentFocusedItem() {
        return currentFocusedItem;
    }
}
