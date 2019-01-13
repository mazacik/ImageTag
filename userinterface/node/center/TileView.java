package userinterface.node.center;

import database.object.DataObject;
import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import settings.SettingsNamespace;
import userinterface.node.BaseNode;
import utils.CommonUtil;
import utils.InstanceRepo;

public class TileView extends ScrollPane implements BaseNode, InstanceRepo {
    private final TilePane tilePane = new TilePane(3, 3);

    public TileView() {
        final int galleryIconSize = settings.valueOf(SettingsNamespace.TILEVIEW_ICONSIZE);

        tilePane.setPrefTileWidth(galleryIconSize);
        tilePane.setPrefTileHeight(galleryIconSize);
        tilePane.setPrefWidth(settings.valueOf(SettingsNamespace.MAINSCENE_WIDTH));
        tilePane.setPrefHeight(settings.valueOf(SettingsNamespace.MAINSCENE_HEIGHT));
        tilePane.setBackground(CommonUtil.getBackgroundDefault());
        tilePane.setPadding(new Insets(0, 0, 0, 1));

        this.setContent(tilePane);
        this.setFitToWidth(true);
        this.setMinViewportWidth(galleryIconSize);
        this.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        this.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        this.setBackground(CommonUtil.getBackgroundDefault());
        this.setBorder(new Border(new BorderStroke(CommonUtil.getNodeBorderColor(), BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(0, 1, 0, 1))));
    }

    public void reload() {
        if (CommonUtil.isFullView()) return;
        double scrollbarValue = this.getVvalue();
        ObservableList<Node> tilePaneItems = tilePane.getChildren();
        tilePaneItems.clear();
        filter.forEach(dataObject -> tilePaneItems.add(dataObject.getBaseTile()));
        this.setVvalue(scrollbarValue);
        this.calculateTilePaneHGap();
    }
    public void calculateTilePaneHGap() {
        double Hgap = tilePane.getVgap();

        double tilePaneWidth = tilePane.getWidth() - 1; // left + right insets
        double prefTileWidth = tilePane.getPrefTileWidth();
        double columnCount = tilePaneWidth / prefTileWidth;

        if (columnCount > 0) Hgap = tilePaneWidth % prefTileWidth / columnCount;

        tilePane.setHgap(Hgap);
    }
    public void adjustViewportToCurrentTarget() {
        DataObject currentTargetedItem = target.getCurrentTarget();
        if (CommonUtil.isFullView() || currentTargetedItem == null) return;
        int targetIndex = filter.indexOf(currentTargetedItem);
        if (targetIndex < 0) return;

        ObservableList<Node> tilePaneItems = tilePane.getChildren();
        int columnCount = this.getColumnCount();
        int targetRow = targetIndex / columnCount;

        Bounds viewportBounds = tilePane.localToScene(this.getViewportBounds());
        Bounds currentTargetTileBounds = tilePaneItems.get(targetIndex).getBoundsInParent();

        double viewportHeight = viewportBounds.getHeight();
        double contentHeight = tilePane.getHeight() - viewportHeight;
        double rowHeight = tilePane.getPrefTileHeight() + tilePane.getVgap();

        double rowToContentRatio = rowHeight / contentHeight;
        double viewportToContentRatio = viewportHeight / contentHeight;

        double viewportTop = viewportBounds.getMaxY() * -1 + viewportBounds.getHeight();
        double viewportBottom = viewportBounds.getMinY() * -1 + viewportBounds.getHeight();
        double tileTop = currentTargetTileBounds.getMinY();
        double tileBottom = currentTargetTileBounds.getMaxY();

        if (tileTop > viewportBottom - rowHeight) {
            this.setVvalue((targetRow + 1) * rowToContentRatio - viewportToContentRatio);
        } else if (tileBottom - rowHeight < viewportTop) {
            this.setVvalue(targetRow * rowToContentRatio);
        }
    }

    public int getColumnCount() {
        return (int) tilePane.getWidth() / (int) tilePane.getPrefTileWidth();
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
