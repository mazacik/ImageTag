package project.gui.component;

import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.TilePane;
import project.control.FilterControl;
import project.control.FocusControl;
import project.control.change.ChangeEventControl;
import project.control.change.ChangeEventEnum;
import project.database.control.DataElementControl;
import project.database.element.DataElement;
import project.gui.control.GUIControl;
import project.helper.Settings;

public abstract class GalleryPane extends ScrollPane {
    /* components */
    private static final ScrollPane _this = new ScrollPane();
    private static final TilePane tilePane = new TilePane();

    /* const */
    private static final int GALLERY_ICON_SIZE_PREF = Settings.getGalleryIconSizePref();

    /* initialize */
    public static void initialize() {
        initializeComponents();
        initializeProperties();
    }
    private static void initializeComponents() {
        tilePane.setVgap(3);
        tilePane.setPrefTileWidth(GALLERY_ICON_SIZE_PREF);
        tilePane.setPrefTileHeight(GALLERY_ICON_SIZE_PREF);
    }
    private static void initializeProperties() {
        _this.setHbarPolicy(ScrollBarPolicy.NEVER);
        _this.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
        _this.setMinViewportWidth(GALLERY_ICON_SIZE_PREF);
        _this.setFitToWidth(true);

        _this.setContent(tilePane);

        GalleryPane.setOnScrollListener();
        GalleryPane.setWidthPropertyListener();

        ChangeEventControl.subscribe(GalleryPane.class, ChangeEventEnum.FILTER, ChangeEventEnum.SELECTION);
    }

    /* public */
    public static void refreshComponent() {
        if (GUIControl.isPreviewFullscreen()) return;

        ObservableList<Node> tilePaneItems = tilePane.getChildren();
        tilePaneItems.clear();
        for (DataElement dataElement : FilterControl.getValidDataElements()) {
            tilePaneItems.add(dataElement.getGalleryTile());
        }
    }
    public static void adjustViewportToFocus() {
        DataElement currentFocusedItem = FocusControl.getCurrentFocus();
        if (currentFocusedItem == null) return;
        ObservableList<Node> tilePaneItems = tilePane.getChildren();

        int columnCount = GalleryPane.getColumnCount();
        int focusIndex = FilterControl.getValidDataElements().indexOf(currentFocusedItem);
        int focusRow = focusIndex / columnCount;

        Bounds viewportBounds = _this.getViewportBounds();
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
            _this.setVvalue(focusRow * rowToContentRatio);
        } else if (viewportBottom - rowHeight < tileBottom) {
            _this.setVvalue((focusRow + 1) * rowToContentRatio - viewportToContentRatio);
        }
    }

    /* private */
    private static void recalculateHgap() {
        int tilePaneWidth = (int) tilePane.getWidth();
        int prefTileWidth = (int) tilePane.getPrefTileWidth();
        int columnCount = tilePaneWidth / prefTileWidth;
        if (columnCount != 0) {
            tilePane.setHgap(tilePaneWidth % prefTileWidth / columnCount);
        }
    }

    /* event */
    private static void setOnScrollListener() {
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

                for (DataElement dataElement : DataElementControl.getDataElementsLive()) {
                    dataElement.getGalleryTile().setFitWidth(galleryIconSizePref);
                    dataElement.getGalleryTile().setFitHeight(galleryIconSizePref);
                }
                recalculateHgap();
            }
        });
    }
    private static void setWidthPropertyListener() {
        tilePane.widthProperty().addListener((observable, oldValue, newValue) -> recalculateHgap());
    }

    /* get */
    public static int getColumnCount() {
        int tilePaneWidth = (int) tilePane.getWidth();
        int prefTileWidth = (int) tilePane.getPrefTileWidth();
        return tilePaneWidth / prefTileWidth;
    }
    public static Region getInstance() {
        return _this;
    }
}
