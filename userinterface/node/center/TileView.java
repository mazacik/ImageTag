package userinterface.node.center;

import database.object.DataObject;
import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.TilePane;
import settings.SettingsNamespace;
import userinterface.BackgroundEnum;
import userinterface.node.BaseNode;
import utils.MainUtil;

public class TileView extends ScrollPane implements MainUtil, BaseNode {
    private final TilePane tilePane;

    public TileView() {
        final int galleryIconSize = settings.valueOf(SettingsNamespace.TILEVIEW_ICONSIZE);

        tilePane = new TilePane();
        tilePane.setVgap(3);
        tilePane.setPrefTileWidth(galleryIconSize);
        tilePane.setPrefTileHeight(galleryIconSize);
        tilePane.setBackground(BackgroundEnum.NIGHT_1.getValue());

        this.setMinViewportWidth(galleryIconSize);
        this.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        this.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        tilePane.setPrefWidth(settings.valueOf(SettingsNamespace.MAINSCENE_WIDTH));
        tilePane.setPrefHeight(settings.valueOf(SettingsNamespace.MAINSCENE_HEIGHT));
        this.setFitToWidth(true);
        this.setFitToHeight(true);
        this.setContent(tilePane);
        this.setPadding(new Insets(settings.valueOf(SettingsNamespace.GLOBAL_SPACING)));
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
        int currentGap = (int) tilePane.getVgap();
        int hgap = currentGap;

        int tilePaneWidth = (int) tilePane.getWidth() + currentGap;
        int prefTileWidth = (int) tilePane.getPrefTileWidth();
        int columnCount = tilePaneWidth / prefTileWidth - 1;

        if (columnCount > 0) {
            hgap = (tilePaneWidth + currentGap * columnCount) % (prefTileWidth + currentGap) / columnCount;
        }
        if (hgap < 3) hgap = currentGap;

        tilePane.setHgap(hgap);
    }
    public void adjustViewportToCurrentFocus() {
        DataObject currentFocusedItem = target.getCurrentFocus();
        if (isFullView() || currentFocusedItem == null) return;
        int focusIndex = filter.indexOf(currentFocusedItem);
        if (focusIndex < 0) return;

        ObservableList<Node> tilePaneItems = tilePane.getChildren();
        int columnCount = this.getColumnCount();
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
    public int getRowCount() {
        double itemCountFilter = filter.size();
        double columnCount = this.getColumnCount();
        return (int) Math.ceil(itemCountFilter / columnCount);
    }

    public TilePane getTilePane() {
        return tilePane;
    }
}
