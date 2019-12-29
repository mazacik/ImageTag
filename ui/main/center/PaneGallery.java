package ui.main.center;

import base.CustomList;
import base.entity.Entity;
import base.entity.EntityList;
import control.Select;
import control.Target;
import control.filter.Filter;
import control.reload.Reload;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.TilePane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import misc.Settings;
import ui.component.clickmenu.ClickMenu;
import ui.decorator.SizeUtil;
import ui.stage.StageManager;

import java.util.logging.Logger;

public class PaneGallery extends ScrollPane {
	private TilePane tilePane;
	private CustomList<Integer> expandedGroups;
	
	private Rectangle selectRectangle;
	private boolean selectRectangleVisible;
	private double selectRectangleX;
	private double selectRectangleY;
	private double localCursorPositionX;
	private double localCursorPositionY;
	
	public static final double GAP = 5;
	
	public void init() {
		tilePane = new TilePane(GAP, GAP);
		tilePane.setPadding(new Insets(GAP));
		
		double actualTileSize = Settings.getTileSize() + 2 * GalleryTile.SELECT_BORDER_PADDING;
		tilePane.setPrefTileWidth(actualTileSize);
		tilePane.setPrefTileHeight(actualTileSize);
		
		tilePane.addEventFilter(MouseEvent.MOUSE_CLICKED, this::onMouseClick);
		tilePane.addEventFilter(MouseEvent.MOUSE_PRESSED, this::onMousePress);
		tilePane.addEventFilter(MouseEvent.MOUSE_DRAGGED, this::onMouseDrag);
		tilePane.addEventFilter(MouseEvent.MOUSE_RELEASED, this::onMouseRelease);
		
		expandedGroups = new CustomList<>();
		
		selectRectangle = new Rectangle(0, 0, Color.GRAY);
		selectRectangle.setStroke(Color.BLACK);
		selectRectangle.setStrokeWidth(1);
		selectRectangle.setOpacity(0.5);
		selectRectangleX = 0;
		selectRectangleY = 0;
		selectRectangleVisible = false;
		localCursorPositionX = 0;
		localCursorPositionY = 0;
		//todo calculate (min) pref width
		this.setContent(tilePane);
		this.getChildren().add(tilePane);
		this.setBackground(Background.EMPTY);
		this.setHbarPolicy(ScrollBarPolicy.NEVER);
		this.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
		this.setMinViewportWidth(actualTileSize);
		this.setPrefViewportHeight(SizeUtil.getUsableScreenHeight());
		this.addEventFilter(ScrollEvent.SCROLL, this::onScroll);
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
				localCursorPositionX = event.getX();
				localCursorPositionY = this.getContentY(event);
				selectRectangleX = event.getX();
				selectRectangleY = this.getContentY(event);
				break;
			case SECONDARY:
				break;
		}
	}
	private void onMouseDrag(MouseEvent event) {
		if (event.getButton() == MouseButton.PRIMARY) {
			localCursorPositionX = event.getX();
			localCursorPositionY = this.getContentY(event);
			double width = Math.abs(localCursorPositionX - selectRectangleX);
			double height = Math.abs(localCursorPositionY - selectRectangleY);
			
			if (!selectRectangleVisible) {
				if (width >= 5 || height >= 5) {
					selectRectangleVisible = true;
					this.getChildren().add(selectRectangle);
					selectRectangle.setWidth(width);
					selectRectangle.setHeight(height);
					selectRectangle.setX(Math.min(selectRectangleX, localCursorPositionX));
					selectRectangle.setY(Math.min(selectRectangleY, localCursorPositionY));
				}
			} else {
				selectRectangle.setWidth(width);
				selectRectangle.setHeight(height);
				selectRectangle.setX(Math.min(selectRectangleX, localCursorPositionX));
				selectRectangle.setY(Math.min(selectRectangleY, localCursorPositionY));
				
				//add a cooldown timer if necessary
				CustomList<Entity> intersectingTiles = getSelectRectangleTiles();
				
				if (event.isShiftDown()) {
					Select.getEntities().addAll(intersectingTiles);
				} else {
					Select.getEntities().setAll(intersectingTiles);
				}
				
				Reload.start();
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
			
			Reload.start();
		}
	}
	
	private double getContentY(MouseEvent event) {
		return this.sceneToLocal(tilePane.localToScene(event.getX(), event.getY())).getY();
	}
	private EntityList getSelectRectangleTiles() {
		EntityList entityList = new EntityList();
		CustomList<GalleryTile> tiles = new CustomList<>();
		tilePane.getChildren().forEach(tile -> tiles.add((GalleryTile) tile));
		
		Bounds rectBounds = selectRectangle.localToScene(selectRectangle.getBoundsInLocal());
		for (GalleryTile galleryTile : tiles) {
			Bounds tileBounds = galleryTile.localToScene(galleryTile.getBoundsInLocal());
			if (rectBounds.intersects(tileBounds)) {
				entityList.add(galleryTile.getEntity());
			}
		}
		return entityList;
	}
	
	private void onScroll(ScrollEvent event) {
		event.consume();
		
		double rowHeight = tilePane.getPrefTileHeight() + GAP;
		double contentHeight = tilePane.getHeight() - this.getViewportBounds().getHeight();
		double rowToContentRatio = rowHeight / contentHeight;
		double currentVvalue = this.getVvalue();
		
		if (event.getDeltaY() > 0) {
			//mouse-scroll-up
			this.setVvalue(currentVvalue - rowToContentRatio);
			
			//top-of-content fix
			if (currentVvalue == 0) return;
			
			double minY = Math.abs(this.getViewportBounds().getMinY());
			double pxChange = rowHeight;
			if (minY - rowHeight < 0) {
				pxChange = minY;
			}
			//top-of-content fixed
			
			if (localCursorPositionY > selectRectangleY) {
				//cursor y below rect y
				double rectHeight = selectRectangle.getHeight() - pxChange;
				selectRectangleY += pxChange;
				selectRectangle.setHeight(rectHeight);
				selectRectangle.setY(selectRectangleY);
			} else {
				//cursor y above rect y
				double rectHeight = selectRectangle.getHeight() + pxChange;
				selectRectangleY += pxChange;
				selectRectangle.setHeight(rectHeight);
			}
		} else {
			//mouse-scroll-down
			this.setVvalue(currentVvalue + rowToContentRatio);
			
			//bottom-of-content fix
			if (currentVvalue == 1) return;
			
			double viewportHeight = this.getViewportBounds().getHeight();
			double viewportMinY = Math.abs(this.getViewportBounds().getMaxY());
			
			double viewportPositionAfterChange = viewportMinY + viewportHeight + rowHeight;
			double contentPosition = contentHeight - viewportHeight;
			
			double pxChange = rowHeight;
			if (viewportPositionAfterChange > contentPosition) {
				pxChange = rowHeight - (viewportPositionAfterChange - contentPosition);
			}
			//bottom-of-content fixed
			
			if (localCursorPositionY > selectRectangleY) {
				//cursor y below rect y
				double rectHeight = selectRectangle.getHeight() + pxChange;
				selectRectangleY -= pxChange;
				selectRectangle.setHeight(rectHeight);
				selectRectangle.setY(selectRectangleY);
			} else {
				//cursor y above rect y
				double rectHeight = selectRectangle.getHeight() - pxChange;
				selectRectangleY -= pxChange;
				selectRectangle.setHeight(rectHeight);
			}
		}
	}
	
	public boolean reload() {
		Logger.getGlobal().info(this.toString());
		
		//	var init
		CustomList<Integer> collectionIDs = new CustomList<>();
		CustomList<GalleryTile> tiles = new CustomList<>();
		Target.storePosition();
		
		//	main loop
		for (Entity entity : Filter.getEntities()) {
			GalleryTile galleryTile = entity.getGalleryTile();
			int collectionID = entity.getCollectionID();
			
			if (collectionID == 0) {
				tiles.add(galleryTile);
			} else if (!collectionIDs.contains(collectionID)) {
				//	only one object in the entity group needs to be processed
				collectionIDs.add(collectionID);
				if (expandedGroups.contains(collectionID)) {
					for (Entity collection : entity.getCollection()) {
						//	instead of letting the main loop take care of all objects in the entity group
						//	the entity group gets processed in a separate loop to keep its objects together
						//	however, each object needs to be checked for Filter validity an additional time
						if (Filter.getEntities().contains(collection)) {
							tiles.add(collection.getGalleryTile());
							Reload.requestBorderUpdate(collection);
						}
					}
				} else {
					tiles.add(galleryTile);
					Reload.requestBorderUpdate(entity);
				}
			}
		}
		
		//	apply changes to TilePane
		tilePane.getChildren().setAll(tiles);
		
		//	Target and Select adjustments
		Entity currentTarget = Target.restorePosition();
		if (currentTarget != null) {
			this.moveViewportToTarget();
			if (currentTarget.getCollectionID() != 0) {
				if (!expandedGroups.contains(currentTarget.getCollectionID())) {
					EntityList collection = currentTarget.getCollection();
					if (Select.getEntities().containsAny(collection)) {
						Select.getEntities().addAll(collection);
					}
				}
			}
		}
		
		return true;
	}
	
	public void moveViewportToTarget() {
		this.layout();
		
		Entity currentTarget = Target.get();
		if (!StageManager.getStageMain().getSceneMain().isViewGallery() || currentTarget == null) return;
		if (currentTarget.getCollectionID() != 0 && !expandedGroups.contains(currentTarget.getCollectionID())) {
			currentTarget = currentTarget.getCollection().getFirst();
		}
		int TargetIndex = this.getEntitiesOfTiles().indexOf(currentTarget);
		if (TargetIndex < 0) return;
		
		int columnCount = this.getColumnCount();
		int TargetRow = TargetIndex / columnCount;
		
		Bounds buggyBounds = this.getViewportBounds();
		Bounds correctBounds = new BoundingBox(0, 0, 0, buggyBounds.getWidth(), buggyBounds.getHeight(), buggyBounds.getDepth());
		Bounds viewportBoundsTransform = tilePane.sceneToLocal(this.localToScene(correctBounds));
		Bounds currentTargetTileBounds = tilePane.getChildren().get(TargetIndex).getBoundsInParent();
		
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
			this.setVvalue((TargetRow + 1) * rowToContentRatio - viewportToContentRatio);
		} else if (tileTop < viewportTop) {
			this.setVvalue(TargetRow * rowToContentRatio);
		}
	}
	public EntityList getEntitiesOfTiles() {
		EntityList entities = new EntityList();
		tilePane.getChildren().forEach(tile -> entities.add(((GalleryTile) tile).getEntity()));
		return entities;
	}
	
	public int getColumnCount() {
		return tilePane.getPrefColumns();
	}
	public TilePane getTiles() {
		return tilePane;
	}
	public CustomList<Integer> getOpenCollections() {
		return expandedGroups;
	}
	
	private PaneGallery() {}
	private static class Loader {
		private static final PaneGallery INSTANCE = new PaneGallery();
	}
	public static PaneGallery get() {
		return Loader.INSTANCE;
	}
}
