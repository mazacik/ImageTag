package user_interface.singleton.center;

import database.object.DataObject;
import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.TilePane;
import settings.SettingsEnum;
import system.CommonUtil;
import system.InstanceRepo;
import user_interface.factory.NodeFactory;
import user_interface.factory.util.enums.ColorType;
import user_interface.scene.SceneUtil;
import user_interface.singleton.BaseNode;

import java.util.ArrayList;

public class TileView extends ScrollPane implements BaseNode, InstanceRepo {
    private final TilePane tilePane = new TilePane(1, 1);
    private final ArrayList<Integer> expandedGroups = new ArrayList<>();
    private Bounds customBounds;

    public TileView() {
        final int galleryIconSize = settings.intValueOf(SettingsEnum.TILEVIEW_ICONSIZE);

        tilePane.setPrefTileWidth(galleryIconSize);
        tilePane.setPrefTileHeight(galleryIconSize);
        tilePane.setPrefHeight(CommonUtil.getUsableScreenHeight() - topMenu.getPrefHeight() - topMenu.getPadding().getBottom() - topMenu.getBorder().getInsets().getBottom());

        tilePane.setPrefColumns(1);

        this.setContent(tilePane);
        this.setFitToWidth(true);
        this.setFitToHeight(true);
        this.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        this.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        this.setBorder(NodeFactory.getBorder(0, 1, 0, 1));
        NodeFactory.addNodeToManager(this, ColorType.DEF);
        NodeFactory.addNodeToManager(tilePane, ColorType.DEF);
    }

    public void adjustPrefColumns() {
        //int prefWidth = (int) CommonUtil.getUsableScreenWidth() * 4 / 5;
        int prefWidth = (int) (CommonUtil.getUsableScreenWidth() - 2 * SceneUtil.getSidePanelMinWidth());
        int prefColumns = prefWidth / settings.intValueOf(SettingsEnum.TILEVIEW_ICONSIZE) - 1;
        tilePane.setPrefColumns(prefColumns);
        this.setMinViewportWidth(tilePane.getPrefColumns() * tilePane.getPrefTileWidth() + (tilePane.getPrefColumns() - 1) * tilePane.getHgap() + 1);
    }

    public void reload() {
        double scrollbarValue = this.getVvalue();
        ObservableList<Node> tilePaneItems = tilePane.getChildren();
        tilePaneItems.clear();
        ArrayList<Integer> mergeIDs = new ArrayList<>();
        for (DataObject dataObject : filter) {
            if (dataObject.getMergeID() == 0) {
                tilePaneItems.add(dataObject.getBaseTile());
            } else if (!mergeIDs.contains(dataObject.getMergeID())) {
                if (!expandedGroups.contains(dataObject.getMergeID())) {
                    tilePaneItems.add(dataObject.getBaseTile());
                    dataObject.generateTileEffect();
                } else {
                    dataObject.getMergeGroup().forEach(dataObject1 -> {
                        tilePaneItems.add(dataObject1.getBaseTile());
                        dataObject1.generateTileEffect();
                    });
                }
                mergeIDs.add(dataObject.getMergeID());
            }
        }
        this.setVvalue(scrollbarValue);
    }
    public void adjustViewportToCurrentTarget() {
        DataObject currentTarget = target.getCurrentTarget();
        if (CommonUtil.isFullView() || currentTarget == null) return;
        int targetIndex = this.getVisibleDataObjects().indexOf(currentTarget);
        if (targetIndex < 0) return;

        ObservableList<Node> tilePaneItems = tilePane.getChildren();
        int columnCount = this.getColumnCount();
        int targetRow = targetIndex / columnCount;

        Bounds viewportBoundsTransform = tilePane.localToScene(customBounds);
        Bounds currentTargetTileBounds = tilePaneItems.get(targetIndex).getBoundsInParent();

        double viewportHeight = viewportBoundsTransform.getHeight();
        double contentHeight = tilePane.getHeight() - viewportHeight;
        double rowHeight = tilePane.getPrefTileHeight() + tilePane.getVgap();

        double rowToContentRatio = rowHeight / contentHeight;
        double viewportToContentRatio = viewportHeight / contentHeight;

        double viewportTop = viewportBoundsTransform.getMinY() * -1;
        double viewportBottom = viewportBoundsTransform.getMinY() * -1 + viewportBoundsTransform.getHeight();
        double tileTop = currentTargetTileBounds.getMinY();
        double tileBottom = currentTargetTileBounds.getMaxY();

        if (tileBottom > viewportBottom) {
            this.setVvalue((targetRow + 1) * rowToContentRatio - viewportToContentRatio);
        } else if (tileTop < viewportTop) {
            this.setVvalue(targetRow * rowToContentRatio);
        }
    }

    public void onShown() {
        this.setCustomBounds(this.getViewportBounds());
        this.adjustPrefColumns();
        this.lookup(".scroll-bar").setStyle("-fx-background-color: transparent;");
        this.lookup(".increment-button").setStyle("-fx-background-color: transparent;");
        this.lookup(".decrement-button").setStyle("-fx-background-color: transparent;");
        this.lookup(".thumb").setStyle("-fx-background-color: gray; -fx-background-insets: 0 4 0 4;");
    }

    public int getColumnCount() {
        return (int) this.getViewportBounds().getWidth() / (int) tilePane.getPrefTileWidth();
    }
    public int getRowCount() {
        double itemCountFilter = tilePane.getChildren().size();
        double columnCount = this.getColumnCount();
        return (int) Math.ceil(itemCountFilter / columnCount);
    }

    public ArrayList<BaseTile> getVisibleTiles() {
        ArrayList<BaseTile> visibleTiles = new ArrayList<>();
        tilePane.getChildren().forEach(tile -> visibleTiles.add((BaseTile) tile));
        return visibleTiles;
    }
    public ArrayList<DataObject> getVisibleDataObjects() {
        ArrayList<DataObject> visibleDataObjects = new ArrayList<>();
        tilePane.getChildren().forEach(tile -> visibleDataObjects.add(((BaseTile) tile).getParentDataObject()));
        return visibleDataObjects;
    }

    public TilePane getTilePane() {
        return tilePane;
    }
    public ArrayList<Integer> getExpandedGroups() {
        return expandedGroups;
    }

    public void setCustomBounds(Bounds customBounds) {
        this.customBounds = customBounds;
    }
}
