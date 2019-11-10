package gui.main.center;

import baseobject.CustomList;
import baseobject.entity.Entity;
import baseobject.entity.EntityList;
import cache.CacheReader;
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
import tools.EntityGroupUtil;

import java.lang.reflect.Method;
import java.util.logging.Logger;

public class GalleryPane extends ScrollPane implements InstanceCollector, Reloadable {
	private TilePane tilePane;
	private CustomList<Integer> expandedGroups;
	
	private Rectangle selectRectangle;
	private double selectRectangleX;
	private double selectRectangleY;
	private boolean selectRectangleVisible;
	private double lastCursorPositionX;
	private double lastCursorPositionY;
	
	public GalleryPane() {
	
	}
	
	public void init() {
		methodsToInvokeOnNextReload = new CustomList<>();
		
		tilePane = new TilePane(30, 30);
		tilePane.setPadding(new Insets(5, 5, 25, 5));
		tilePane.setPrefTileWidth(SizeUtil.getGalleryTileSize());
		tilePane.setPrefTileHeight(SizeUtil.getGalleryTileSize());
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
		lastCursorPositionX = 0;
		lastCursorPositionY = 0;
		
		this.setBackground(Background.EMPTY);
		this.setMaxWidth(1);
		this.getChildren().add(tilePane);
		this.setContent(tilePane);
		this.setHbarPolicy(ScrollBarPolicy.NEVER);
		this.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
		this.setPrefViewportHeight(SizeUtil.getUsableScreenHeight());
		this.addEventFilter(ScrollEvent.SCROLL, this::onScroll);
		this.vvalueProperty().addListener((observable, oldValue, newValue) -> updateViewportTilesVisibility());
		
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
				lastCursorPositionX = event.getX();
				lastCursorPositionY = this.getContentY(event);
				selectRectangleX = event.getX();
				selectRectangleY = this.getContentY(event);
				break;
			case SECONDARY:
				break;
		}
	}
	private void onMouseDrag(MouseEvent event) {
		if (event.getButton() == MouseButton.PRIMARY) {
			lastCursorPositionX = event.getX();
			lastCursorPositionY = this.getContentY(event);
			double width = Math.abs(lastCursorPositionX - selectRectangleX);
			double height = Math.abs(lastCursorPositionY - selectRectangleY);
			
			if (!selectRectangleVisible) {
				if (width >= 5 || height >= 5) {
					selectRectangleVisible = true;
					this.getChildren().add(selectRectangle);
					selectRectangle.setWidth(width);
					selectRectangle.setHeight(height);
					selectRectangle.setX(Math.min(selectRectangleX, lastCursorPositionX));
					selectRectangle.setY(Math.min(selectRectangleY, lastCursorPositionY));
				}
			} else {
				selectRectangle.setWidth(width);
				selectRectangle.setHeight(height);
				selectRectangle.setX(Math.min(selectRectangleX, lastCursorPositionX));
				selectRectangle.setY(Math.min(selectRectangleY, lastCursorPositionY));
				
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
	private void onScroll(ScrollEvent event) {
		event.consume();
		
		//if something broke after changing tilePane padding or tilePane vGap, look at paddingFix
		double paddingFix = (tilePane.getPadding().getTop() + tilePane.getPadding().getBottom()) / 2;
		double rowHeight = tilePane.getHeight() / getRowCount();
		double contentHeight = tilePane.getHeight() - paddingFix;
		double rowToContentRatio = (rowHeight + 25) / contentHeight;
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
			
			if (lastCursorPositionY > selectRectangleY) {
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
				pxChange = rowHeight - (viewportPositionAfterChange - contentPosition) + paddingFix;
			}
			//bottom-of-content fixed
			
			if (lastCursorPositionY > selectRectangleY) {
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
	
	private double getContentY(MouseEvent event) {
		return this.sceneToLocal(tilePane.localToScene(event.getX(), event.getY())).getY();
	}
	
	public void moveViewportToTarget() {
		this.layout();
		
		Entity currentTarget = target.get();
		if (StageManager.getMainStage().isFullView() || currentTarget == null) return;
		if (currentTarget.getEntityGroupID() != 0 && !expandedGroups.contains(currentTarget.getEntityGroupID())) {
			currentTarget = EntityGroupUtil.getEntityGroup(currentTarget).getFirst();
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
		
		updateViewportTilesVisibility();
	}
	public void updateViewportTilesVisibility() {
		tilePane.layout(); //	force tilepane layout to update its viewport
		
		CustomList<GalleryTile> tilesInViewport = getTilesInViewport();
		CustomList<GalleryTile> tilesNotInViewport = getTiles();
		tilesNotInViewport.removeAll(tilesInViewport);
		
		for (GalleryTile galleryTile : tilesInViewport) {
			if (galleryTile.getImage() == null) {
				galleryTile.setImage(CacheReader.get(galleryTile.getEntity()));
			}
			galleryTile.setVisible(true);
		}
		
		for (GalleryTile galleryTile : tilesNotInViewport) {
			galleryTile.setVisible(false);
		}
	}
	public EntityList getEntitiesOfTiles() {
		EntityList entities = new EntityList();
		tilePane.getChildren().forEach(tile -> entities.add(((GalleryTile) tile).getEntity()));
		return entities;
	}
	
	private CustomList<GalleryTile> getTiles() {
		CustomList<GalleryTile> tiles = new CustomList<>();
		tilePane.getChildren().forEach(tile -> tiles.add((GalleryTile) tile));
		return tiles;
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
		
		int entityIndex;
		Bounds tileBounds;
		double tileTop;
		double tileBottom;
		
		for (GalleryTile entity : visibleTiles) {
			entityIndex = visibleTiles.indexOf(entity);
			tileBounds = visibleTiles.get(entityIndex).getBoundsInParent();
			tileTop = tileBounds.getMinY();
			tileBottom = tileBounds.getMaxY();
			if (tileTop <= viewportBottom + tileSize && tileBottom >= viewportTop - tileSize) {
				tilesInViewport.add(entity);
			}
		}
		
		return tilesInViewport;
	}
	private EntityList getSelectRectangleTiles() {
		EntityList intersectingTiles = new EntityList();
		
		Bounds rectBounds = selectRectangle.localToScene(selectRectangle.getBoundsInLocal());
		for (GalleryTile galleryTile : getTilesInViewport()) {
			Bounds tileBounds = galleryTile.localToScene(galleryTile.getBoundsInLocal());
			if (rectBounds.intersects(tileBounds)) {
				intersectingTiles.add(galleryTile.getEntity());
			}
		}
		
		return intersectingTiles;
	}
	
	private CustomList<Method> methodsToInvokeOnNextReload;
	@Override
	public CustomList<Method> getMethodsToInvokeOnNextReload() {
		return methodsToInvokeOnNextReload;
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
					for (Entity entityGroup : EntityGroupUtil.getEntityGroup(entity)) {
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
		
		//	apply changes to the actual TilePane
		tilePane.getChildren().setAll(tiles);
		
		updateViewportTilesVisibility();
		
		//	Target and Select adjustments
		Entity currentTarget = target.restorePosition();
		if (currentTarget != null) {
			this.moveViewportToTarget();
			if (currentTarget.getEntityGroupID() != 0) {
				if (!expandedGroups.contains(currentTarget.getEntityGroupID())) {
					if (select.containsAny(EntityGroupUtil.getEntityGroup(currentTarget))) {
						select.addAll(EntityGroupUtil.getEntityGroup(currentTarget));
					}
				}
			}
		}
		
		return true;
	}
	
	public int getColumnCount() {
		return tilePane.getPrefColumns();
	}
	public int getRowCount() {
		return (int) Math.ceil((double) this.getTiles().size() / (double) this.getColumnCount());
	}
	public TilePane getTilePane() {
		return tilePane;
	}
	public CustomList<Integer> getExpandedGroups() {
		return expandedGroups;
	}
}
