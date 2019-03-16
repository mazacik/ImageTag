package user_interface.singleton.center;

import database.object.DataObject;
import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import settings.SettingsEnum;
import system.CommonUtil;
import system.InstanceRepo;
import user_interface.factory.NodeFactory;
import user_interface.factory.util.ColorUtil;
import user_interface.factory.util.enums.ColorType;
import user_interface.singleton.BaseNode;

import java.util.ArrayList;

public class TileView extends ScrollPane implements BaseNode, InstanceRepo {
    private final TilePane tilePane = new TilePane(1, 1);
    private final ArrayList<Integer> expandedGroups = new ArrayList<>();
    private Bounds customBounds;

    public TileView() {
        final int galleryIconSize = coreSettings.valueOf(SettingsEnum.TILEVIEW_ICONSIZE);

        tilePane.setPrefTileWidth(galleryIconSize);
        tilePane.setPrefTileHeight(galleryIconSize);
        tilePane.setPrefHeight(coreSettings.valueOf(SettingsEnum.MAINSCENE_HEIGHT));
        tilePane.setPrefColumns(10);

        this.setContent(tilePane);
        this.setFitToWidth(true);
        this.setMinViewportWidth(tilePane.getPrefColumns() * tilePane.getPrefTileWidth() + (tilePane.getPrefColumns() - 1) * tilePane.getHgap());
        this.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        this.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        this.setBorder(new Border(new BorderStroke(ColorUtil.getBorderColor(), BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(0, 1, 0, 1))));
        NodeFactory.addNodeToBackgroundManager(this, ColorType.DEF);
        NodeFactory.addNodeToBackgroundManager(tilePane, ColorType.DEF);
    }

    public void reload() {
        if (CommonUtil.isFullView()) return;

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
        int targetIndex = this.getDataObjects().indexOf(currentTarget);
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

    public void postInit() {
        this.setCustomBounds(tileView.getViewportBounds());
        this.lookupAll(".scroll-bar").forEach(sb -> sb.setStyle("-fx-background-color: transparent;"));
        this.lookupAll(".increment-button").forEach(sb -> sb.setStyle("-fx-background-color: transparent;"));
        this.lookupAll(".decrement-button").forEach(sb -> sb.setStyle("-fx-background-color: transparent;"));
        this.lookupAll(".thumb").forEach(sb -> sb.setStyle("-fx-background-color: gray; -fx-background-insets: 0 4 0 4;"));
    }

    public int getColumnCount() {
        return (int) this.getViewportBounds().getWidth() / (int) tilePane.getPrefTileWidth();
    }
    public int getRowCount() {
        double itemCountFilter = tilePane.getChildren().size();
        double columnCount = this.getColumnCount();
        return (int) Math.ceil(itemCountFilter / columnCount);
    }

    public ArrayList<BaseTile> getTiles() {
        ArrayList<BaseTile> arrayList = new ArrayList<>();
        tilePane.getChildren().forEach(tile -> arrayList.add((BaseTile) tile));
        return arrayList;
    }
    public ArrayList<DataObject> getDataObjects() {
        ArrayList<DataObject> dataObjects = new ArrayList<>();
        tilePane.getChildren().forEach(tile -> dataObjects.add(((BaseTile) tile).getParentDataObject()));
        return dataObjects;
    }

    public ArrayList<Integer> getExpandedGroups() {
        return expandedGroups;
    }

    public void setCustomBounds(Bounds customBounds) {
        this.customBounds = customBounds;
    }
}
