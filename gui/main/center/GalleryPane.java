package gui.main.center;

import baseobject.CustomList;
import baseobject.entity.Entity;
import baseobject.entity.EntityList;
import control.reload.Reloadable;
import gui.component.clickmenu.ClickMenu;
import gui.decorator.SizeUtil;
import gui.stage.StageManager;
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
import main.InstanceCollector;
import tools.Stopwatch;

import java.util.logging.Logger;

public class GalleryPane extends ScrollPane implements InstanceCollector, Reloadable {
	private TilePane tilePane;
	private CustomList<Integer> expandedGroups;
	
	private Rectangle selectRectangle;
	private boolean selectRectangleVisible;
	private double selectRectangleX;
	private double selectRectangleY;
	private double localCursorPositionX;
	private double localCursorPositionY;
	
	public static final double GAP = 5;
	
	public GalleryPane() {
	
	}
	
	public void init() {
		tilePane = new TilePane(GAP, GAP);
		tilePane.setPadding(new Insets(GAP));
		
		double actualTileSize = settings.getTileSize() + 2 * GalleryTile.SELECT_BORDER_PADDING;
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
		
		this.setContent(tilePane);
		this.getChildren().add(tilePane);
		this.setBackground(Background.EMPTY);
		this.setHbarPolicy(ScrollBarPolicy.NEVER);
		this.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
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
				
				if (StageManager.getMainStage().isShiftDown()) {
					select.addAll(intersectingTiles);
				} else {
					select.setAll(intersectingTiles);
				}
				
				reload.doReload();
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
			
			reload.doReload();
		}
	}
	
	private double getContentY(MouseEvent event) {
		return this.sceneToLocal(tilePane.localToScene(event.getX(), event.getY())).getY();
	}
	private EntityList getSelectRectangleTiles() {
		Stopwatch.start();
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
		Stopwatch.stop();
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
		CustomList<Integer> entityGroupIDs = new CustomList<>();
		CustomList<GalleryTile> tiles = new CustomList<>();
		target.storePosition();
		
		//	main loop
		for (Entity entity : filter) {
			GalleryTile galleryTile = entity.getGalleryTile();
			int entityGroupID = entity.getEntityGroupID();
			
			if (entityGroupID == 0) {
				tiles.add(galleryTile);
			} else if (!entityGroupIDs.contains(entityGroupID)) {
				//	only one object in the entity group needs to be processed
				entityGroupIDs.add(entityGroupID);
				if (expandedGroups.contains(entityGroupID)) {
					for (Entity entityGroup : entity.getEntityGroup()) {
						//	instead of letting the main loop take care of all objects in the entity group
						//	the entity group gets processed in a separate loop to keep its objects together
						//	however, each object needs to be checked for Filter validity an additional time
						if (filter.contains(entityGroup)) {
							tiles.add(entityGroup.getGalleryTile());
							reload.requestTileEffect(entityGroup);
						}
					}
				} else {
					tiles.add(galleryTile);
					reload.requestTileEffect(entity);
				}
			}
		}
		
		//	apply changes to TilePane
		tilePane.getChildren().setAll(tiles);
		
		//	Target and Select adjustments
		Entity currentTarget = target.restorePosition();
		if (currentTarget != null) {
			this.moveViewportToTarget();
			if (currentTarget.getEntityGroupID() != 0) {
				if (!expandedGroups.contains(currentTarget.getEntityGroupID())) {
					EntityList entityGroup = currentTarget.getEntityGroup();
					if (select.containsAny(entityGroup)) {
						select.addAll(entityGroup);
					}
				}
			}
		}
		
		return true;
	}
	
	public void moveViewportToTarget() {
		this.layout();
		
		Entity currentTarget = target.get();
		if (StageManager.getMainStage().isFullView() || currentTarget == null) return;
		if (currentTarget.getEntityGroupID() != 0 && !expandedGroups.contains(currentTarget.getEntityGroupID())) {
			currentTarget = currentTarget.getEntityGroup().getFirst();
		}
		int targetIndex = this.getEntitiesOfTiles().indexOf(currentTarget);
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
	}
	public EntityList getEntitiesOfTiles() {
		EntityList entities = new EntityList();
		tilePane.getChildren().forEach(tile -> entities.add(((GalleryTile) tile).getEntity()));
		return entities;
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
