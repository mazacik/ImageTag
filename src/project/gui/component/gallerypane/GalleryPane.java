package project.gui.component.gallerypane;

import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.TilePane;
import project.control.MainControl;
import project.database.object.DataObject;
import project.gui.GUIInstance;
import project.gui.event.gallerypane.GalleryPaneEvent;
import project.settings.Settings;

public abstract class GalleryPane {
    private static final int GALLERY_ICON_SIZE_PREF = Settings.getGalleryIconSizePref();

    private static final ScrollPane _this = new ScrollPane();
    private static final TilePane tilePane = new TilePane();

    public static void initialize() {
        initializeComponents();
        initializeInstance();
        GalleryPaneEvent.initialize();
    }
    private static void initializeComponents() {
        tilePane.setVgap(3);
        tilePane.setPrefTileWidth(GALLERY_ICON_SIZE_PREF);
        tilePane.setPrefTileHeight(GALLERY_ICON_SIZE_PREF);
    }
    private static void initializeInstance() {
        _this.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        _this.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        _this.setMinViewportWidth(GALLERY_ICON_SIZE_PREF);
        _this.setFitToWidth(true);

        _this.setContent(tilePane);
    }

    public static void reload() {
        if (GUIInstance.isPreviewFullscreen()) return;
        double scrollbarValue = _this.getVvalue();
        ObservableList<Node> tilePaneItems = tilePane.getChildren();
        tilePaneItems.clear();
        for (DataObject dataObject : MainControl.getFilterControl().getCollection()) {
            tilePaneItems.add(dataObject.getGalleryTile());
        }
        _this.setVvalue(scrollbarValue);
        calculateTilePaneHGap();
        adjustViewportToCurrentFocus();
    }
    public static void calculateTilePaneHGap() {
        //todo fix poslednych par pixelov
        int minGap = (int) tilePane.getVgap();
        int hgap = minGap;

        int tilePaneWidth = (int) tilePane.getWidth() + minGap;
        int prefTileWidth = (int) tilePane.getPrefTileWidth();
        int columnCount = tilePaneWidth / prefTileWidth - 1;

        if (columnCount > 0) {
            hgap = (tilePaneWidth + minGap * columnCount) % (prefTileWidth + minGap) / columnCount;
        }

        tilePane.setHgap(hgap);
    }
    public static void adjustViewportToCurrentFocus() {
        DataObject currentFocusedItem = MainControl.getFocusControl().getCurrentFocus();
        if (currentFocusedItem == null) return;
        if (GUIInstance.isPreviewFullscreen()) return;
        int focusIndex = MainControl.getFilterControl().getCollection().indexOf(currentFocusedItem);
        if (focusIndex < 0) return;

        ObservableList<Node> tilePaneItems = tilePane.getChildren();
        int columnCount = GalleryPane.getColumnCount();
        int focusRow = focusIndex / columnCount;

        Bounds viewportBounds = tilePane.localToScene(_this.getViewportBounds());
        Bounds currentFocusTileBounds = tilePaneItems.get(focusIndex).getBoundsInParent();

        double viewportHeight = viewportBounds.getHeight();
        double contentHeight = tilePane.getHeight() - viewportHeight;
        double rowHeight = tilePane.getPrefTileHeight() + tilePane.getVgap();

        double rowToContentRatio = rowHeight / contentHeight;
        double viewportToContentRatio = viewportHeight / contentHeight;

        double viewportTop = viewportBounds.getMaxY() * -1 + viewportBounds.getHeight();
        double viewportBottom = viewportBounds.getMinY() * -1 + viewportBounds.getHeight();
        double tileTop = currentFocusTileBounds.getMinY();
        double tileBottom = currentFocusTileBounds.getMaxY();

        if (tileTop > viewportBottom - rowHeight) {
            _this.setVvalue((focusRow + 1) * rowToContentRatio - viewportToContentRatio);
        } else if (tileBottom - rowHeight < viewportTop) {
            _this.setVvalue(focusRow * rowToContentRatio);
        }
    }

    public static int getColumnCount() {
        int tilePaneWidth = (int) tilePane.getWidth() + (int) tilePane.getVgap();
        int prefTileWidth = (int) tilePane.getPrefTileWidth();
        return tilePaneWidth / prefTileWidth;
    }
    public static TilePane getTilePane() {
        return tilePane;
    }
    public static Region getInstance() {
        return _this;
    }
}
