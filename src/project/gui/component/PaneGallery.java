package project.gui.component;

import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.TilePane;
import project.common.Settings;
import project.control.FilterControl;
import project.control.FocusControl;
import project.database.DataElementDatabase;
import project.database.element.DataElement;
import project.gui.ChangeEventControl;
import project.gui.ChangeEventEnum;
import project.gui.ChangeEventListener;
import project.gui.GUIControl;

public class PaneGallery extends ScrollPane implements ChangeEventListener {
    /* components */
    private final TilePane tilePane = new TilePane();
    private final ObservableList<Node> tilePaneItems = tilePane.getChildren();

    private static final int galleryIconSizePref = Settings.getGalleryIconSizePref();

    /* constructors */
    public PaneGallery() {
        initializeComponents();
        initializeProperties();
    }

    /* initialize */
    private void initializeComponents() {
        tilePane.setVgap(3);
        tilePane.setPrefTileWidth(galleryIconSizePref);
        tilePane.setPrefTileHeight(galleryIconSizePref);
    }
    private void initializeProperties() {
        setHbarPolicy(ScrollBarPolicy.NEVER);
        setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
        setMinViewportWidth(galleryIconSizePref);
        setFitToWidth(true);

        setContent(tilePane);

        setOnScrollListener();
        setWidthPropertyListener();

        ChangeEventControl.subscribe(this, ChangeEventEnum.FILTER, ChangeEventEnum.FOCUS);
    }

    /* public */
    public void adjustViewportToFocus() {
        DataElement currentFocusedItem = FocusControl.getCurrentFocus();
        if (currentFocusedItem == null) return;

        int columnCount = getColumnCount();
        int focusIndex = FilterControl.getValidDataElements().indexOf(currentFocusedItem);
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
    public void refreshComponent() {
        if (GUIControl.isPreviewFullscreen()) return;

        tilePaneItems.clear();
        for (DataElement dataElement : FilterControl.getValidDataElements()) {
            tilePaneItems.add(dataElement.getGalleryTile());
        }
    }

    /* private */
    private void recalculateHgap() {
        int tilePaneWidth = (int) tilePane.getWidth();
        int prefTileWidth = (int) tilePane.getPrefTileWidth();
        int columnCount = tilePaneWidth / prefTileWidth;
        if (columnCount != 0) {
            tilePane.setHgap(tilePaneWidth % prefTileWidth / columnCount);
        }
    }

    /* event */
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

                for (DataElement dataElement : DataElementDatabase.getDataElements()) {
                    dataElement.getGalleryTile().setFitWidth(galleryIconSizePref);
                    dataElement.getGalleryTile().setFitHeight(galleryIconSizePref);
                }
                recalculateHgap();
            }
        });
    }
    private void setWidthPropertyListener() {
        tilePane.widthProperty().addListener((observable, oldValue, newValue) -> recalculateHgap());
    }

    /* get */
    public int getColumnCount() {
        int tilePaneWidth = (int) tilePane.getWidth();
        int prefTileWidth = (int) tilePane.getPrefTileWidth();
        return tilePaneWidth / prefTileWidth;
    }
}
