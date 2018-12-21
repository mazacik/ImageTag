package gui.singleton.center;

import control.reload.Reload;
import database.object.DataObject;
import gui.singleton.BaseNode;
import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.TilePane;
import settings.SettingsEnum;
import utils.MainUtil;

public class TileView extends ScrollPane implements MainUtil, BaseNode {
    private final TilePane tilePane;

    public TileView() {
        final int galleryIconSize = settings.getValueOf(SettingsEnum.TILEVIEW_ICONSIZE);

        tilePane = new TilePane();
        tilePane.setVgap(3);
        tilePane.setPrefTileWidth(galleryIconSize);
        tilePane.setPrefTileHeight(galleryIconSize);

        reload.subscribe(this, Reload.Control.DATA, Reload.Control.FILTER);

        this.setMinViewportWidth(galleryIconSize);
        this.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        this.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        this.setFitToWidth(true);
        this.setContent(tilePane);
    }

    public void reload() {
        if (isFullView()) return;
        double scrollbarValue = this.getVvalue();
        ObservableList<Node> tilePaneItems = tilePane.getChildren();
        tilePaneItems.clear();
        filter.forEach(dataObject -> tilePaneItems.add(dataObject.getBaseTile()));
        this.setVvalue(scrollbarValue);
        this.calculateTilePaneHGap();
    }
    public void calculateTilePaneHGap() {
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
    public void adjustViewportToCurrentFocus() {
        DataObject currentFocusedItem = target.getCurrentFocus();
        if (isFullView() || currentFocusedItem == null) return;
        int focusIndex = filter.indexOf(currentFocusedItem);
        if (focusIndex < 0) return;

        ObservableList<Node> tilePaneItems = tilePane.getChildren();
        int columnCount = tileView.getColumnCount();
        int focusRow = focusIndex / columnCount;

        Bounds viewportBounds = tilePane.localToScene(this.getViewportBounds());
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
            this.setVvalue((focusRow + 1) * rowToContentRatio - viewportToContentRatio);
        } else if (tileBottom - rowHeight < viewportTop) {
            this.setVvalue(focusRow * rowToContentRatio);
        }
    }

    public int getColumnCount() {
        int tilePaneWidth = (int) tilePane.getWidth() + (int) tilePane.getVgap();
        int prefTileWidth = (int) tilePane.getPrefTileWidth();
        return tilePaneWidth / prefTileWidth;
    }
    public TilePane getTilePane() {
        return tilePane;
    }
}
