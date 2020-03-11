package ui.main.gallery;

import base.CustomList;
import base.entity.Entity;
import base.entity.EntityCollectionUtil;
import base.entity.EntityList;
import control.Select;
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
import ui.decorator.Decorator;
import ui.main.stage.MainStage;
import ui.stage.SimpleMessageStage;

public class GalleryPane extends ScrollPane {
	public static final double GAP = 1;
	
	private static final TilePane tilePane;
	
	private static final Rectangle selectRectangle;
	private static double selectRectangleX;
	private static double selectRectangleY;
	private static double localCursorPositionX;
	private static double localCursorPositionY;
	
	private static final EntityList selectRectangleHelper;
	
	private static double getContentY(MouseEvent event) {
		return getInstance().sceneToLocal(tilePane.localToScene(event.getX(), event.getY())).getY();
	}
	private static EntityList getSelectRectangleEntities() {
		EntityList entityList = new EntityList();
		for (Tile tile : tiles) {
			if (selectRectangle.localToScene(selectRectangle.getBoundsInLocal()).intersects(tile.localToScene(tile.getBoundsInLocal()))) {
				Entity entity = tile.getEntity();
				if (EntityCollectionUtil.hasOpenOrNoCollection(entity)) {
					entityList.add(entity);
				} else {
					entityList.addAll(entity.getCollection());
				}
				
			}
		}
		return entityList;
	}
	
	private static void onMousePress(MouseEvent event) {
		switch (event.getButton()) {
			case PRIMARY:
				double x = event.getX();
				double y = GalleryPane.getContentY(event);
				
				selectRectangleX = x;
				selectRectangleY = y;
				localCursorPositionX = x;
				localCursorPositionY = y;
				
				selectRectangleHelper.setAll(Select.getEntities());
				break;
			case SECONDARY:
				break;
		}
	}
	private static void onMouseDrag(MouseEvent event) {
		if (event.getButton() == MouseButton.PRIMARY) {
			localCursorPositionX = event.getX();
			localCursorPositionY = GalleryPane.getContentY(event);
			double width = Math.abs(localCursorPositionX - selectRectangleX);
			double height = Math.abs(localCursorPositionY - selectRectangleY);
			
			if (!getInstance().getChildren().contains(selectRectangle)) {
				if (width >= 5 || height >= 5) {
					getInstance().getChildren().add(selectRectangle);
				}
			} else {
				selectRectangle.setWidth(width);
				selectRectangle.setHeight(height);
				selectRectangle.setX(Math.min(selectRectangleX, localCursorPositionX));
				selectRectangle.setY(Math.min(selectRectangleY, localCursorPositionY));
				
				EntityList selectRectangleEntities = GalleryPane.getSelectRectangleEntities();
				Select.getEntities().clear();
				
				if (event.isControlDown()) {
					Select.getEntities().addAll(selectRectangleHelper, true);
					Select.getEntities().addAll(selectRectangleEntities, true);
					for (Entity entity : new EntityList(Select.getEntities())) {
						if (entity != Select.getTarget() && selectRectangleHelper.contains(entity) && selectRectangleEntities.contains(entity)) {
							Select.getEntities().remove(entity);
						}
					}
				} else if (event.isShiftDown()) {
					selectRectangleHelper.removeAll(selectRectangleEntities);
					Select.getEntities().addAll(selectRectangleHelper, true);
					Select.getEntities().addAll(selectRectangleEntities, true);
				} else {
					selectRectangleHelper.removeAll(selectRectangleEntities);
					Select.getEntities().addAll(selectRectangleEntities);
				}
				
				Reload.start();
			}
		}
	}
	private static void onMouseRelease(MouseEvent event) {
		if (event.getButton() == MouseButton.PRIMARY && getInstance().getChildren().contains(selectRectangle)) {
			selectRectangle.setWidth(0);
			selectRectangle.setHeight(0);
			selectRectangle.setX(0);
			selectRectangle.setY(0);
			
			getInstance().getChildren().remove(selectRectangle);
			
			Reload.start();
		}
	}
	
	private static void onScroll(ScrollEvent event) {
		event.consume();
		
		GalleryPane instance = getInstance();
		
		double rowHeight = tilePane.getPrefTileHeight() + GAP;
		double contentHeight = tilePane.getHeight() - instance.getViewportBounds().getHeight();
		double rowToContentRatio = rowHeight / contentHeight;
		double currentVvalue = instance.getVvalue();
		
		if (event.getDeltaY() > 0) {
			//mouse-scroll-up
			instance.setVvalue(currentVvalue - rowToContentRatio);
			
			//top-of-content fix
			if (currentVvalue == 0) return;
			
			double minY = Math.abs(instance.getViewportBounds().getMinY());
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
			instance.setVvalue(currentVvalue + rowToContentRatio);
			
			//bottom-of-content fix
			if (currentVvalue == 1) return;
			
			double viewportHeight = instance.getViewportBounds().getHeight();
			double viewportMinY = Math.abs(instance.getViewportBounds().getMaxY());
			
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
	
	static {
		tilePane = new TilePane(GAP, GAP);
		tilePane.setPadding(new Insets(GAP));
		
		double actualTileSize = Settings.GALLERY_TILE_SIZE.getValueInteger() + 2 * Tile.HIGHLIGHT_PADDING;
		tilePane.setPrefTileWidth(actualTileSize);
		tilePane.setPrefTileHeight(actualTileSize);
		
		tilePane.addEventFilter(MouseEvent.MOUSE_PRESSED, GalleryPane::onMousePress);
		tilePane.addEventFilter(MouseEvent.MOUSE_DRAGGED, GalleryPane::onMouseDrag);
		tilePane.addEventFilter(MouseEvent.MOUSE_RELEASED, GalleryPane::onMouseRelease);
		
		tilePane.heightProperty().addListener((observable, oldValue, newValue) -> GalleryPane.moveViewportToTarget());
		
		selectRectangle = new Rectangle(0, 0, Color.GRAY);
		selectRectangle.setStroke(Color.BLACK);
		selectRectangle.setStrokeWidth(1);
		selectRectangle.setOpacity(0.5);
		selectRectangle.setMouseTransparent(true);
		selectRectangleX = 0;
		selectRectangleY = 0;
		localCursorPositionX = 0;
		localCursorPositionY = 0;
		
		selectRectangleHelper = new EntityList();
		
		GalleryPane instance = getInstance();
		
		instance.setContent(tilePane);
		instance.getChildren().add(tilePane);
		instance.setBackground(Background.EMPTY);
		instance.setHbarPolicy(ScrollBarPolicy.NEVER);
		instance.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
		instance.setPrefViewportHeight(Decorator.getUsableScreenHeight());
		instance.addEventFilter(ScrollEvent.SCROLL, GalleryPane::onScroll);
	}
	
	private static EntityList tileEntities = new EntityList();
	private static CustomList<Tile> tiles = new CustomList<>();
	
	public boolean reload() {
		//	prepare
		Select.storeTargetPosition();
		
		tiles.clear();
		tileEntities.clear();
		
		CustomList<Integer> helper = new CustomList<>();
		
		//	main loop
		for (Entity entity : Filter.getEntities()) {
			if (tiles.size() >= 4000) {
				SimpleMessageStage.show("Gallery reached a limit of 4000 tiles.");
				break;
			}
			
			if (entity.getCollectionID() == 0) {
				tiles.add(entity.getTile());
				tileEntities.add(entity);
			} else if (!helper.contains(entity.getCollectionID())) {
				//	only one object in a collection needs to be processed
				helper.add(entity.getCollectionID());
				
				if (EntityCollectionUtil.getOpenCollections().contains(entity.getCollectionID())) {
					for (Entity entityInCollection : entity.getCollection()) {
						//	instead of letting the main loop take care of all objects in a collection
						//	the collection gets processed in a separate loop to keep its objects together
						//	however, each object needs to be checked for Filter validity an additional time
						if (Filter.getEntities().contains(entityInCollection)) {
							tiles.add(entityInCollection.getTile());
							tileEntities.add(entityInCollection);
							Reload.requestBorderUpdate(entityInCollection);
						}
					}
				} else {
					entity.getTile().updateCollectionIcon();
					tiles.add(entity.getTile());
					tileEntities.add(entity);
					Reload.requestBorderUpdate(entity);
				}
			}
		}
		
		//	apply changes
		tilePane.getChildren().setAll(tiles);
		
		//  finish
		Select.restoreTargetPosition();
		
		return true;
	}
	public static void moveViewportToTarget() {
		Entity currentTarget = Select.getTarget();
		if (!MainStage.getMainScene().isViewGallery() || currentTarget == null) return;
		if (currentTarget.getCollectionID() != 0 && !EntityCollectionUtil.getOpenCollections().contains(currentTarget.getCollectionID())) {
			currentTarget = currentTarget.getCollection().getFirst();
		}
		int targetIndex = tileEntities.indexOf(currentTarget);
		if (targetIndex < 0) return;
		
		int columnCount = tilePane.getPrefColumns();
		int targetRow = targetIndex / columnCount;
		
		Bounds reverseBounds = getInstance().getViewportBounds();
		Bounds correctBounds = new BoundingBox(Math.abs(reverseBounds.getMinX()), Math.abs(reverseBounds.getMinY()), Math.abs(reverseBounds.getMinZ()), reverseBounds.getWidth(), reverseBounds.getHeight(), reverseBounds.getDepth());
		Bounds targetIndexTileBounds = tilePane.getChildren().get(targetIndex).getBoundsInParent();
		
		double viewportHeight = correctBounds.getHeight();
		double contentHeight = tilePane.getHeight() - viewportHeight;
		double rowHeight = tilePane.getPrefTileHeight() + tilePane.getVgap();
		
		double rowToContentRatio = rowHeight / contentHeight;
		double viewportToContentRatio = viewportHeight / contentHeight;
		
		double viewportTop = correctBounds.getMinY();
		double viewportBottom = correctBounds.getMaxY();
		
		double tileTop = targetIndexTileBounds.getMinY();
		double tileBottom = targetIndexTileBounds.getMaxY();
		
		if (tileBottom > viewportBottom) {
			getInstance().setVvalue((targetRow + 1) * rowToContentRatio - viewportToContentRatio);
		} else if (tileTop < viewportTop) {
			getInstance().setVvalue(targetRow * rowToContentRatio);
		}
	}
	
	public static TilePane getTilePane() {
		return tilePane;
	}
	public static EntityList getTileEntities() {
		return tileEntities;
	}
	
	private GalleryPane() {}
	private static class Loader {
		private static final GalleryPane INSTANCE = new GalleryPane();
	}
	public static GalleryPane getInstance() {
		return Loader.INSTANCE;
	}
}
