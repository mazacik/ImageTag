package application.gui.panes.center;

import application.database.list.CustomList;
import application.database.list.DataObjectList;
import application.database.loader.utils.ThumbnailReader;
import application.database.object.DataObject;
import application.gui.decorator.SizeUtil;
import application.gui.nodes.ClickMenu;
import application.gui.panes.NodeBase;
import application.gui.stage.Stages;
import application.main.Instances;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.TilePane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;

public class GalleryPane extends ScrollPane implements NodeBase {
	private final TilePane tilePane;
	private final CustomList<Integer> expandedGroups;
	private final Rectangle selectRectangle;
	private double selectRectangleX;
	private double selectRectangleY;
	private boolean selectRectangleVisible;
	
	public GalleryPane() {
		needsReload = false;
		
		selectRectangle = new Rectangle(0, 0, Color.GRAY);
		selectRectangle.setStroke(Color.BLACK);
		selectRectangle.setStrokeWidth(1);
		selectRectangle.setOpacity(0.5);
		selectRectangleX = 0;
		selectRectangleY = 0;
		selectRectangleVisible = false;
		
		tilePane = new TilePane(30, 30);
		tilePane.setPadding(new Insets(5, 5, 25, 5));
		tilePane.setPrefTileWidth(SizeUtil.getGalleryTileSize());
		tilePane.setPrefTileHeight(SizeUtil.getGalleryTileSize());
		tilePane.addEventFilter(MouseEvent.MOUSE_CLICKED, this::onMouseClick);
		tilePane.addEventFilter(MouseEvent.MOUSE_PRESSED, this::onMousePress);
		tilePane.addEventFilter(MouseEvent.MOUSE_DRAGGED, this::onMouseDrag);
		tilePane.addEventFilter(MouseEvent.MOUSE_RELEASED, this::onMouseRelease);
		tilePane.addEventFilter(MouseEvent.MOUSE_RELEASED, this::onMouseRelease);
		tilePane.addEventFilter(ScrollEvent.SCROLL, this::onScroll);
		
		expandedGroups = new CustomList<>();
		
		this.vvalueProperty().addListener((observable, oldValue, newValue) -> updateViewportTilesVisibility());
		this.setBackground(Background.EMPTY);
		this.setMaxWidth(1);
		this.getChildren().add(tilePane);
		this.setContent(tilePane);
		this.setHbarPolicy(ScrollBarPolicy.NEVER);
		this.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
		this.setPrefViewportHeight(SizeUtil.getUsableScreenHeight());
	}
	
	private void onMouseClick(MouseEvent event) {
		switch (event.getButton()) {
			case PRIMARY:
				ClickMenu.hideAll();
				break;
			case SECONDARY:
				ClickMenu.show(tilePane, event, ClickMenu.StaticInstance.SELECT);
				break;
		}
	}
	private void onMousePress(MouseEvent event) {
		switch (event.getButton()) {
			case PRIMARY:
				ClickMenu.hideAll();
				selectRectangleX = event.getX();
				selectRectangleY = this.sceneToLocal(tilePane.localToScene(event.getX(), event.getY())).getY();
				break;
			case SECONDARY:
				break;
		}
	}
	private void onMouseDrag(MouseEvent event) {
		if (event.getButton() == MouseButton.PRIMARY) {
			double eventY = this.sceneToLocal(tilePane.localToScene(event.getX(), event.getY())).getY();
			double width = Math.abs(event.getX() - selectRectangleX);
			double height = Math.abs(eventY - selectRectangleY);
			
			if (!selectRectangleVisible) {
				if (width >= 5 || height >= 5) {
					selectRectangleVisible = true;
					this.getChildren().add(selectRectangle);
					updateSelectRectangle(event.getX(), eventY, width, height);
				}
			} else {
				updateSelectRectangle(event.getX(), eventY, width, height);
				
				//todo add a cooldown timer if necessary
				CustomList<DataObject> intersectingTiles = getSelectRectangleTiles();
				
				if (Stages.getMainStage().isShiftDown()) {
					Instances.getSelect().addAll(intersectingTiles);
				} else {
					Instances.getSelect().setAll(intersectingTiles);
				}
				
				Instances.getReload().doReload();
			}
		}
	}
	private void onMouseRelease(MouseEvent event) {
		if (selectRectangleVisible && event.getButton() == MouseButton.PRIMARY) {
			selectRectangle.setWidth(0);
			selectRectangle.setHeight(0);
			selectRectangle.setX(0);
			selectRectangle.setY(0);
			
			selectRectangleVisible = false;
			this.getChildren().remove(selectRectangle);
			
			Instances.getReload().doReload();
		}
	}
	private void onScroll(ScrollEvent event) {
		double paddingFix = (tilePane.getPadding().getTop() + tilePane.getPadding().getBottom()) / 2;
		double contentHeight = tilePane.getHeight() - paddingFix;
		double rowHeight = tilePane.getPrefTileHeight() + tilePane.getVgap() - paddingFix;
		double rowToContentRatio = rowHeight / contentHeight;
		
		//todo fix a bug when the box is "changing direction"
		if (event.getDeltaY() > 0) {
			//scroll-up
			this.setVvalue(this.getVvalue() - rowToContentRatio);
			
			double actualRowHeight = tilePane.getPrefTileHeight() + tilePane.getVgap();
			double rectHeight = selectRectangle.getHeight() - actualRowHeight;
			selectRectangleY += actualRowHeight;
			
			selectRectangle.setHeight(rectHeight);
			selectRectangle.setY(selectRectangleY);
		} else {
			//scroll-down
			this.setVvalue(this.getVvalue() + rowToContentRatio);
			
			double actualRowHeight = tilePane.getPrefTileHeight() + tilePane.getVgap();
			double rectHeight = selectRectangle.getHeight() + actualRowHeight;
			selectRectangleY -= actualRowHeight;
			
			selectRectangle.setHeight(rectHeight);
			selectRectangle.setY(selectRectangleY);
		}
	}
	
	public void adjustViewportToTarget() {
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
		
		updateViewportTilesVisibility();
	}
	public void updateViewportTilesVisibility() {
		tilePane.layout(); //	force tilepane layout to update its viewport
		ArrayList<GalleryTile> tilesInViewport = getTilesInViewport();
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
	public DataObjectList getDataObjectsOfTiles() {
		DataObjectList dataObjects = new DataObjectList();
		tilePane.getChildren().forEach(tile -> dataObjects.add(((GalleryTile) tile).getParentDataObject()));
		return dataObjects;
	}
	
	private CustomList<GalleryTile> getTiles() {
		CustomList<GalleryTile> visibleTiles = new CustomList<>();
		tilePane.getChildren().forEach(tile -> visibleTiles.add((GalleryTile) tile));
		return visibleTiles;
	}
	private CustomList<GalleryTile> getTilesInViewport() {
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
		
		for (GalleryTile dataObject : getTiles()) {
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
	private void updateSelectRectangle(double eventX, double eventY, double width, double height) {
		selectRectangle.setWidth(width);
		selectRectangle.setHeight(height);
		selectRectangle.setX(Math.min(selectRectangleX, eventX));
		selectRectangle.setY(Math.min(selectRectangleY, eventY));
	}
	private DataObjectList getSelectRectangleTiles() {
		DataObjectList intersectingTiles = new DataObjectList();
		
		Bounds rectBounds = selectRectangle.localToScene(selectRectangle.getBoundsInLocal());
		for (GalleryTile galleryTile : getTilesInViewport()) {
			Bounds tileBounds = galleryTile.localToScene(galleryTile.getBoundsInLocal());
			if (rectBounds.intersects(tileBounds)) {
				intersectingTiles.add(galleryTile.getParentDataObject());
			}
		}
		
		return intersectingTiles;
	}
	
	private boolean needsReload;
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
			this.adjustViewportToTarget();
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
	public boolean getNeedsReload() {
		return needsReload;
	}
	public void setNeedsReload(boolean needsReload) {
		this.needsReload = needsReload;
	}
	
	public int getColumnCount() {
		return tilePane.getPrefColumns();
	}
	public TilePane getTilePane() {
		return tilePane;
	}
	public CustomList<Integer> getExpandedGroups() {
		return expandedGroups;
	}
}
