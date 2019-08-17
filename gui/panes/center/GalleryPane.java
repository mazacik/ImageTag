package application.gui.panes.center;

import application.database.list.CustomList;
import application.database.list.DataObjectList;
import application.database.loader.utils.ThumbnailReader;
import application.database.object.DataObject;
import application.gui.decorator.SizeUtil;
import application.gui.nodes.NodeUtil;
import application.gui.panes.NodeBase;
import application.gui.stage.Stages;
import application.main.Instances;
import javafx.collections.ObservableList;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.TilePane;

import java.util.ArrayList;

public class GalleryPane extends ScrollPane implements NodeBase {
	private final TilePane tilePane;
	private final CustomList<Integer> expandedGroups;
	
	public GalleryPane() {
		needsReload = false;
		
		tilePane = new TilePane(1, 1);
		expandedGroups = new CustomList<>();
		
		tilePane.setPrefTileWidth(SizeUtil.getGalleryIconSize());
		tilePane.setPrefTileHeight(SizeUtil.getGalleryIconSize());
		
		this.vvalueProperty().addListener((observable, oldValue, newValue) -> loadCacheOfTilesInViewport());
		
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
		
		this.setBackground(Background.EMPTY);
		this.setMaxWidth(1);
		this.setContent(tilePane);
		this.setHbarPolicy(ScrollBarPolicy.NEVER);
		this.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
		this.setPrefViewportHeight(SizeUtil.getUsableScreenHeight());
	}
	
	public boolean reload() {
		Instances.getTarget().storePosition();
		double vValue = this.getVvalue();
		DataObject helperTarget = Instances.getTarget().getCurrentTarget();
		ObservableList<Node> tilePaneItems = tilePane.getChildren();
		tilePaneItems.clear();
		ArrayList<Integer> mergeIDs = new ArrayList<>();
		for (DataObject dataObject : Instances.getFilter()) {
			if (dataObject.getGalleryTile() != null) {
				if (dataObject.getMergeID() == 0 && !tilePaneItems.contains(dataObject.getGalleryTile())) {
					tilePaneItems.add(dataObject.getGalleryTile());
				} else if (!mergeIDs.contains(dataObject.getMergeID())) {
					if (!expandedGroups.contains(dataObject.getMergeID()) && !tilePaneItems.contains(dataObject.getGalleryTile())) {
						tilePaneItems.add(dataObject.getGalleryTile());
						Instances.getReload().requestTileEffect(dataObject);
					} else {
						dataObject.getMergeGroup().forEach(dataObjectInMergeGroup -> {
							if (!tilePaneItems.contains(dataObjectInMergeGroup.getGalleryTile())) {
								tilePaneItems.add(dataObjectInMergeGroup.getGalleryTile());
								Instances.getReload().requestTileEffect(dataObjectInMergeGroup);
							}
						});
					}
					mergeIDs.add(dataObject.getMergeID());
				}
			}
		}
		if (helperTarget != Instances.getTarget().getCurrentTarget() || !getDataObjectsOfTiles().contains(helperTarget)) {
			this.setVvalue(vValue);
			Instances.getTarget().restorePosition();
		}
		return true;
	}
	
	public void adjustViewportToCurrentTarget() {
		DataObject currentTarget = Instances.getTarget().getCurrentTarget();
		if (Stages.getMainStage().isFullView() || currentTarget == null) return;
		if (currentTarget.getMergeID() != 0 && !expandedGroups.contains(currentTarget.getMergeID())) {
			currentTarget = currentTarget.getMergeGroup().getFirst();
		}
		int targetIndex = this.getDataObjectsOfTiles().indexOf(currentTarget);
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
		
		loadCacheOfTilesInViewport();
	}
	
	public void loadCacheOfTilesInViewport() {
		tilePane.layout(); //	force tilepane layout to update its viewport
		ArrayList<GalleryTile> tilesInViewport = getTilesInViewport(getTiles());
		for (Node node : tilePane.getChildren()) {
			if (node instanceof GalleryTile) {
				GalleryTile galleryTile = (GalleryTile) node;
				if (tilesInViewport.contains(galleryTile)) {
					if (galleryTile.getImage() == null) {
						galleryTile.setImage(ThumbnailReader.readThumbnail(galleryTile.getParentDataObject()));
					}
					galleryTile.setVisible(true);
				} else {
					galleryTile.setVisible(false);
				}
			}
		}
	}
	public CustomList<GalleryTile> getTilesInViewport(CustomList<GalleryTile> tiles) {
		Bounds buggyBounds = this.getViewportBounds();
		Bounds correctBounds = new BoundingBox(0, 0, 0, buggyBounds.getWidth(), buggyBounds.getHeight(), buggyBounds.getDepth());
		Bounds viewportBoundsTransform = tilePane.sceneToLocal(this.localToScene(correctBounds));
		double viewportTop = viewportBoundsTransform.getMinY();
		double viewportBottom = viewportBoundsTransform.getMaxY();
		double tileSize = tilePane.getPrefTileWidth();
		
		CustomList<GalleryTile> visibleTiles = getTiles();
		CustomList<GalleryTile> tilesInViewport = new CustomList<>();
		
		int objectIndex;
		Bounds tileBounds;
		double tileTop;
		double tileBottom;
		
		for (GalleryTile dataObject : tiles) {
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
	
	public CustomList<GalleryTile> getTiles() {
		CustomList<GalleryTile> visibleTiles = new CustomList<>();
		tilePane.getChildren().forEach(tile -> visibleTiles.add((GalleryTile) tile));
		return visibleTiles;
	}
	public DataObjectList getDataObjectsOfTiles() {
		DataObjectList dataObjects = new DataObjectList();
		tilePane.getChildren().forEach(tile -> dataObjects.add(((GalleryTile) tile).getParentDataObject()));
		return dataObjects;
	}
	
	private boolean needsReload;
	@Override
	public boolean getNeedsReload() {
		return needsReload;
	}
	@Override
	public void setNeedsReload(boolean needsReload) {
		this.needsReload = needsReload;
	}
	
	public int getColumnCount() {
		return tilePane.getPrefColumns();
	}
	public int getRowCount() {
		int tileCount = tilePane.getChildren().size();
		int columnCount = this.getColumnCount();
		return (int) Math.ceil(tileCount / columnCount);
	}
	
	public TilePane getTilePane() {
		return tilePane;
	}
	public CustomList<Integer> getExpandedGroups() {
		return expandedGroups;
	}
}
