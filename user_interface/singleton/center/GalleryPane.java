package user_interface.singleton.center;

import database.object.DataObject;
import javafx.collections.ObservableList;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.TilePane;
import lifecycle.InstanceManager;
import user_interface.singleton.NodeBase;
import user_interface.utils.NodeUtil;
import user_interface.utils.SceneUtil;
import user_interface.utils.SizeUtil;
import user_interface.utils.enums.ColorType;

import java.util.ArrayList;

public class GalleryPane extends ScrollPane implements NodeBase {
    private final TilePane tilePane;
    private final ArrayList<Integer> expandedGroups;

    public GalleryPane() {
        tilePane = new TilePane(1, 1);
        expandedGroups = new ArrayList<>();

        tilePane.setPrefTileWidth(SizeUtil.getGalleryIconSize());
        tilePane.setPrefTileHeight(SizeUtil.getGalleryIconSize());
        tilePane.setOnScroll(event -> {
            event.consume();

            double viewportHeight = this.getViewportBounds().getHeight();
            double contentHeight = tilePane.getHeight() - viewportHeight;
            double rowHeight = tilePane.getPrefTileHeight() + tilePane.getVgap();
            double rowToContentRatio = rowHeight / contentHeight;

            if (event.getDeltaY() > 0) {
                this.setVvalue(this.getVvalue() - rowToContentRatio);
            } else {
                this.setVvalue(this.getVvalue() + rowToContentRatio);
            }
        });

        this.setMaxWidth(1);
        this.setContent(tilePane);
        this.setHbarPolicy(ScrollBarPolicy.NEVER);
        this.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
        this.setPrefViewportHeight(SizeUtil.getUsableScreenHeight());
        this.setBorder(NodeUtil.getBorder(0, 1, 0, 1));

        NodeUtil.addToManager(this, ColorType.DEF);
        NodeUtil.addToManager(tilePane, ColorType.DEF);
    }

    public boolean reload() {
        double scrollbarValue = this.getVvalue();
        ObservableList<Node> tilePaneItems = tilePane.getChildren();
        tilePaneItems.clear();
        ArrayList<Integer> mergeIDs = new ArrayList<>();
        for (DataObject dataObject : InstanceManager.getFilter()) {
            if (dataObject.getBaseTile() != null) {
                if (dataObject.getMergeID() == 0 && !tilePaneItems.contains(dataObject.getBaseTile())) {
                    tilePaneItems.add(dataObject.getBaseTile());
                } else if (!mergeIDs.contains(dataObject.getMergeID())) {
                    if (!expandedGroups.contains(dataObject.getMergeID()) && !tilePaneItems.contains(dataObject.getBaseTile())) {
                        tilePaneItems.add(dataObject.getBaseTile());
                        dataObject.generateTileEffect();
                    } else {
                        dataObject.getMergeGroup().forEach(dataObjectInMergeGroup -> {
                            if (!tilePaneItems.contains(dataObject.getBaseTile())) {
                                tilePaneItems.add(dataObjectInMergeGroup.getBaseTile());
                                dataObjectInMergeGroup.generateTileEffect();
                            }
                        });
                    }
                    mergeIDs.add(dataObject.getMergeID());
                }
            }
        }
        this.setVvalue(scrollbarValue);
        return true;
    }

    public void adjustViewportToCurrentTarget() {
        DataObject currentTarget = InstanceManager.getTarget().getCurrentTarget();
        if (SceneUtil.isFullView() || currentTarget == null) return;
        int targetIndex = this.getVisibleDataObjects().indexOf(currentTarget);
        if (targetIndex < 0) return;

        ObservableList<Node> tilePaneItems = tilePane.getChildren();
        int columnCount = this.getColumnCount();
        int targetRow = targetIndex / columnCount;

        //try to figure out how and why does this work
        Bounds buggyBounds = this.getViewportBounds();
        Bounds correctBounds = new BoundingBox(0, 0, 0, buggyBounds.getWidth(), buggyBounds.getHeight(), buggyBounds.getDepth());
        Bounds viewportBoundsTransform = tilePane.sceneToLocal(this.localToScene(correctBounds));
        Bounds currentTargetTileBounds = tilePaneItems.get(targetIndex).getBoundsInParent();

        double viewportHeight = viewportBoundsTransform.getHeight();
        double contentHeight = tilePane.getHeight() - viewportHeight;
        double rowHeight = tilePane.getPrefTileHeight() + tilePane.getVgap();

        double rowToContentRatio = rowHeight / contentHeight;
        double viewportToContentRatio = viewportHeight / contentHeight;

        double viewportTop = viewportBoundsTransform.getMinY();
        double viewportBottom = viewportBoundsTransform.getMinY() + viewportBoundsTransform.getHeight();
        double tileTop = currentTargetTileBounds.getMinY();
        double tileBottom = currentTargetTileBounds.getMaxY();

        if (tileBottom > viewportBottom) {
            this.setVvalue((targetRow + 1) * rowToContentRatio - viewportToContentRatio);
        } else if (tileTop < viewportTop) {
            this.setVvalue(targetRow * rowToContentRatio);
        }
    }

    public int getColumnCount() {
        return tilePane.getPrefColumns();
    }
    public int getRowCount() {
        int tileCount = tilePane.getChildren().size();
        int columnCount = this.getColumnCount();
        return (int) Math.ceil(tileCount / columnCount);
    }

    public ArrayList<BaseTile> getVisibleTiles() {
        ArrayList<BaseTile> arrayList = new ArrayList<>();
        tilePane.getChildren().forEach(tile -> arrayList.add((BaseTile) tile));
        return arrayList;
    }
    public ArrayList<DataObject> getVisibleDataObjects() {
        ArrayList<DataObject> dataObjects = new ArrayList<>();
        tilePane.getChildren().forEach(tile -> dataObjects.add(((BaseTile) tile).getParentObject()));
        return dataObjects;
    }

    public TilePane getTilePane() {
        return tilePane;
    }
    public ArrayList<Integer> getExpandedGroups() {
        return expandedGroups;
    }
}
