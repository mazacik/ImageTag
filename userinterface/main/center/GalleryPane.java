package userinterface.main.center;

import database.list.CustomList;
import database.loader.ThumbnailReader;
import database.object.DataObject;
import javafx.collections.ObservableList;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.TilePane;
import main.InstanceManager;
import userinterface.main.NodeBase;
import userinterface.nodes.NodeUtil;
import userinterface.scene.SceneUtil;
import userinterface.style.SizeUtil;
import userinterface.style.enums.ColorType;

import java.util.ArrayList;

public class GalleryPane extends ScrollPane implements NodeBase {
	private final TilePane tilePane;
	private final ArrayList<Integer> expandedGroups;
	
	public GalleryPane() {
		tilePane = new TilePane(1, 1);
		expandedGroups = new ArrayList<>();
		
		tilePane.setPrefTileWidth(SizeUtil.getGalleryIconSize());
		tilePane.setPrefTileHeight(SizeUtil.getGalleryIconSize());
		
		this.vvalueProperty().addListener((observable, oldValue, newValue) -> loadViewportCache());
		
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
							if (!tilePaneItems.contains(dataObjectInMergeGroup.getBaseTile())) {
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
	
	public void loadViewportCache() {
		tilePane.layout(); //	force tilepane layout to update its viewport
		ArrayList<BaseTile> tilesInViewport = getTilesInViewport(getVisibleTiles());
		for (Node node : tilePane.getChildren()) {
			if (node instanceof BaseTile) {
				BaseTile baseTile = (BaseTile) node;
				if (tilesInViewport.contains(baseTile)) {
					if (baseTile.getImage() == null) {
						baseTile.setImage(ThumbnailReader.readThumbnail(baseTile.getParentObject()));
					}
				} else {
					baseTile.setImage(null);
				}
			}
		}
	}
	
	public ArrayList<BaseTile> getTilesInViewport(ArrayList<BaseTile> tiles) {
		Bounds buggyBounds = this.getViewportBounds();
		Bounds correctBounds = new BoundingBox(0, 0, 0, buggyBounds.getWidth(), buggyBounds.getHeight(), buggyBounds.getDepth());
		Bounds viewportBoundsTransform = tilePane.sceneToLocal(this.localToScene(correctBounds));
		double viewportTop = viewportBoundsTransform.getMinY();
		double viewportBottom = viewportBoundsTransform.getMaxY();
		double tileSize = tilePane.getPrefTileWidth();
		
		ArrayList<BaseTile> visibleTiles = getVisibleTiles();
		ArrayList<BaseTile> tilesInViewport = new ArrayList<>();
		
		int objectIndex;
		Bounds tileBounds;
		double tileTop;
		double tileBottom;
		
		for (BaseTile dataObject : tiles) {
			objectIndex = visibleTiles.indexOf(dataObject);
			tileBounds = visibleTiles.get(objectIndex).getBoundsInParent();
			tileTop = tileBounds.getMinY();
			tileBottom = tileBounds.getMaxY();
			if (tileTop <= viewportBottom + tileSize && tileBottom >= viewportTop - tileSize) {
				tilesInViewport.add(dataObject);
			}
		}
		
		return tilesInViewport;
	}
	
	public void adjustViewportToCurrentTarget() {
		DataObject currentTarget = InstanceManager.getTarget().getCurrentTarget();
		if (SceneUtil.isFullView() || currentTarget == null) return;
		int targetIndex = this.getVisibleDataObjects().indexOf(currentTarget);
		if (targetIndex < 0) return;
		
		int columnCount = this.getColumnCount();
		int targetRow = targetIndex / columnCount;
		
		Bounds buggyBounds = this.getViewportBounds();
		Bounds correctBounds = new BoundingBox(0, 0, 0, buggyBounds.getWidth(), buggyBounds.getHeight(), buggyBounds.getDepth());
		Bounds viewportBoundsTransform = tilePane.sceneToLocal(this.localToScene(correctBounds));
		Bounds currentTargetTileBounds = tilePane.getChildren().get(targetIndex).getBoundsInParent();
		
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
		
		loadViewportCache();
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
	public CustomList<DataObject> getVisibleDataObjects() {
		CustomList<DataObject> dataObjects = new CustomList<>();
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
