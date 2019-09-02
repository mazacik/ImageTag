package application.gui.panes.center;

import application.database.list.CustomList;
import application.database.list.DataObjectList;
import application.database.loader.utils.ThumbnailReader;
import application.database.object.DataObject;
import application.gui.decorator.SizeUtil;
import application.gui.panes.NodeBase;
import application.gui.stage.Stages;
import application.main.Instances;
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
		
		tilePane.setPrefTileWidth(SizeUtil.getGalleryTileSize());
		tilePane.setPrefTileHeight(SizeUtil.getGalleryTileSize());
		
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
		//	var init
		CustomList<Integer> jointIDs = new CustomList<>();
		CustomList<GalleryTile> tiles = new CustomList<>();
		Instances.getTarget().storePosition();
		
		//	main loop
		for (DataObject dataObject : Instances.getFilter()) {
			GalleryTile galleryTile = dataObject.getGalleryTile();
			int jointID = dataObject.getJointID();
			
			if (jointID == 0) {
				tiles.add(galleryTile);
			} else if (!jointIDs.contains(jointID)) {
				//	only one object in the joint object needs to be processed
				jointIDs.add(jointID);
				if (expandedGroups.contains(jointID)) {
					for (DataObject jointObject : dataObject.getJointObjects()) {
						//	instead of letting the main loop take care of all objects in the joint object
						//	the joint object gets processed in a separate loop to keep its objects together
						//	however, each object needs to be checked for Filter validity an additional time
						if (Instances.getFilter().contains(jointObject)) {
							tiles.add(jointObject.getGalleryTile());
							Instances.getReload().requestTileEffect(jointObject);
						}
					}
				} else {
					tiles.add(galleryTile);
					Instances.getReload().requestTileEffect(dataObject);
				}
			}
		}
		
		//	apply changes to the actual TilePane
		tilePane.getChildren().setAll(tiles);
		
		//	Target and Select adjustments
		DataObject currentTarget = Instances.getTarget().restorePosition();
		if (currentTarget != null) {
			this.adjustViewportToCurrentTarget();
			if (currentTarget.getJointID() != 0) {
				if (!expandedGroups.contains(currentTarget.getJointID())) {
					if (Instances.getSelect().containsAny(currentTarget.getJointObjects())) {
						Instances.getSelect().addAll(currentTarget.getJointObjects());
					}
				}
			}
		}
		
		return true;
	}
	
	public void adjustViewportToCurrentTarget() {
		this.layout();
		
		DataObject currentTarget = Instances.getTarget().getCurrentTarget();
		if (Stages.getMainStage().isFullView() || currentTarget == null) return;
		if (currentTarget.getJointID() != 0 && !expandedGroups.contains(currentTarget.getJointID())) {
			currentTarget = currentTarget.getJointObjects().getFirst();
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
	public boolean getNeedsReload() {
		return needsReload;
	}
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
