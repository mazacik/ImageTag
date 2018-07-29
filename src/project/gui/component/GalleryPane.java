package project.gui.component;

import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.TilePane;
import project.control.FilterControl;
import project.control.FocusControl;
import project.database.element.DataElement;
import project.gui.GUIControl;
import project.settings.Settings;
import project.userinput.gui.UserInputGalleryPane;

public abstract class GalleryPane {
    /* const */
    private static final int GALLERY_ICON_SIZE_PREF = Settings.getGalleryIconSizePref();

    /* components */
    private static final ScrollPane _this = new ScrollPane();
    private static final TilePane tilePane = new TilePane();

    /* initialize */
    public static void initialize() {
        initializeComponents();
        initializeInstance();
        UserInputGalleryPane.initialize();
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

    /* public */
    public static void reload() {
        if (GUIControl.isPreviewFullscreen()) return;

        double scrollbarValue = _this.getVvalue();
        ObservableList<Node> tilePaneItems = tilePane.getChildren();
        tilePaneItems.clear();
        for (DataElement dataElement : FilterControl.getValidDataElements()) {
            tilePaneItems.add(dataElement.getGalleryTile());
        }
        _this.setVvalue(scrollbarValue);
    }
    public static void calculateTilePaneHGap() {
        int tilePaneWidth = (int) tilePane.getWidth();
        int prefTileWidth = (int) tilePane.getPrefTileWidth();
        int columnCount = tilePaneWidth / prefTileWidth - 1;
        if (columnCount != 0) {
            tilePane.setHgap(tilePaneWidth % prefTileWidth / columnCount);
        }
    }
    public static void adjustViewportToCurrentFocus() {
        DataElement currentFocusedItem = FocusControl.getCurrentFocus();
        if (currentFocusedItem == null) return;
        ObservableList<Node> tilePaneItems = tilePane.getChildren();

        int columnCount = GalleryPane.getColumnCount();
        int focusIndex = FilterControl.getValidDataElements().indexOf(currentFocusedItem);
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

    /* get */
    public static int getColumnCount() {
        int tilePaneWidth = (int) tilePane.getWidth();
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
