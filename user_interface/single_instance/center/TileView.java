package user_interface.single_instance.center;

import database.object.DataObject;
import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import settings.SettingsNamespace;
import system.CommonUtil;
import system.InstanceRepo;
import user_interface.node_factory.NodeFactory;
import user_interface.node_factory.utils.ColorType;
import user_interface.node_factory.utils.ColorUtil;
import user_interface.single_instance.BaseNode;

public class TileView extends ScrollPane implements BaseNode, InstanceRepo {
    private final TilePane tilePane = new TilePane(1, 1);

    public TileView() {
        final int galleryIconSize = userSettings.valueOf(SettingsNamespace.TILEVIEW_ICONSIZE);

        tilePane.setPrefTileWidth(galleryIconSize);
        tilePane.setPrefTileHeight(galleryIconSize);
        tilePane.setPrefHeight(coreSettings.valueOf(SettingsNamespace.MAINSCENE_HEIGHT));
        tilePane.setPrefColumns(10);

        this.setContent(tilePane);
        this.setFitToWidth(true);
        this.setMinViewportWidth(tilePane.getPrefColumns() * tilePane.getPrefTileWidth() + (tilePane.getPrefColumns() - 1) * tilePane.getHgap());
        this.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        this.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        this.setBorder(new Border(new BorderStroke(ColorUtil.getBorderColor(), BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(0, 1, 0, 1))));
        NodeFactory.addNodeToBackgroundManager(this, ColorType.DEF);
    }

    public void reload() {
        if (CommonUtil.isFullView()) return;
        double scrollbarValue = this.getVvalue();
        ObservableList<Node> tilePaneItems = tilePane.getChildren();
        tilePaneItems.clear();
        filter.forEach(dataObject -> tilePaneItems.add(dataObject.getBaseTile()));
        this.setVvalue(scrollbarValue);
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
        return (int) this.getViewportBounds().getWidth() / (int) tilePane.getPrefTileWidth();
    }
    public int getRowCount() {
        double itemCountFilter = filter.size();
        double columnCount = this.getColumnCount();
        return (int) Math.ceil(itemCountFilter / columnCount);
    }
}
