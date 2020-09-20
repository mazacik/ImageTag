package frontend.component.center.gallery;

import backend.BaseList;
import backend.entity.Entity;
import backend.entity.EntityList;
import backend.misc.Settings;
import backend.reload.Reload;
import frontend.UserInterface;
import frontend.stage.SimpleMessageStage;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.TilePane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import main.Main;

public class GalleryPane extends ScrollPane {
	public static final double GAP = 1;
	public static final int TILE_LIMIT = 4000;
	
	private final TilePane tilePane;
	private final EntityList tileEntities;
	private final BaseList<Tile> tiles;
	
	private final Rectangle selectRectangle;
	private double selectRectangleX;
	private double selectRectangleY;
	private double localCursorPositionX;
	private double localCursorPositionY;
	
	private final EntityList selectRectangleHelper;
	
	public GalleryPane() {
		tilePane = new TilePane(GAP, GAP);
		
		tileEntities = new EntityList();
		tiles = new BaseList<>();
		
		selectRectangle = new Rectangle(0, 0, Color.GRAY);
		
		selectRectangleHelper = new EntityList();
	}
	
	public void initialize() {
		double actualTileSize = Settings.GALLERY_TILE_SIZE.getInteger() + 2 * Tile.HIGHLIGHT_PADDING;
		tilePane.setPrefTileWidth(actualTileSize);
		tilePane.setPrefTileHeight(actualTileSize);
		tilePane.setAlignment(Pos.CENTER);
		
		tilePane.addEventFilter(MouseEvent.MOUSE_PRESSED, this::onMousePress);
		tilePane.addEventFilter(MouseEvent.MOUSE_DRAGGED, this::onMouseDrag);
		tilePane.addEventFilter(MouseEvent.MOUSE_RELEASED, this::onMouseRelease);
		
		tilePane.heightProperty().addListener((observable, oldValue, newValue) -> this.moveViewportToTarget());
		tilePane.setPadding(new Insets(0, 1, 0, 0));
		
		selectRectangle.setStroke(Color.BLACK);
		selectRectangle.setStrokeWidth(1);
		selectRectangle.setOpacity(0.5);
		selectRectangle.setMouseTransparent(true);
		selectRectangleX = 0;
		selectRectangleY = 0;
		localCursorPositionX = 0;
		localCursorPositionY = 0;
		
		this.setFitToWidth(true);
		this.setMinViewportWidth(actualTileSize);
		this.setMinViewportHeight(actualTileSize + 2 * GAP);
		this.setContent(tilePane);
		this.setBackground(Background.EMPTY);
		this.setHbarPolicy(ScrollBarPolicy.NEVER);
		this.setVbarPolicy(ScrollBarPolicy.ALWAYS);
		this.addEventFilter(ScrollEvent.SCROLL, this::onScroll);
	}
	
	private double getContentY(MouseEvent event) {
		return this.sceneToLocal(tilePane.localToScene(event.getX(), event.getY())).getY();
	}
	private EntityList getSelectRectangleEntities() {
		EntityList entityList = new EntityList();
		for (Tile tile : tiles) {
			if (selectRectangle.localToScene(selectRectangle.getBoundsInLocal()).intersects(tile.localToScene(tile.getBoundsInLocal()))) {
				Entity entity = tile.getEntity();
				if (entity.hasGroup()) {
					if (entity.getEntityGroup().isOpen()) {
						entityList.add(entity);
					} else {
						entityList.addAll(Main.FILTER.getFilteredList(entity.getEntityGroup()));
					}
				} else {
					entityList.add(entity);
				}
			}
		}
		return entityList;
	}
	
	private void onMousePress(MouseEvent event) {
		switch (event.getButton()) {
			case PRIMARY:
				double x = event.getX();
				double y = this.getContentY(event);
				
				selectRectangleX = x;
				selectRectangleY = y;
				localCursorPositionX = x;
				localCursorPositionY = y;
				
				selectRectangleHelper.setAll(Main.SELECT);
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
			
			if (!this.getChildren().contains(selectRectangle)) {
				if (width >= 5 || height >= 5) {
					this.getChildren().add(selectRectangle);
				}
			} else {
				selectRectangle.setWidth(width);
				selectRectangle.setHeight(height);
				selectRectangle.setX(Math.min(selectRectangleX, localCursorPositionX));
				selectRectangle.setY(Math.min(selectRectangleY, localCursorPositionY));
				
				EntityList selectRectangleEntities = this.getSelectRectangleEntities();
				Main.SELECT.clear();
				
				if (event.isControlDown()) {
					Main.SELECT.addAll(selectRectangleHelper, true);
					Main.SELECT.addAll(selectRectangleEntities, true);
					for (Entity entity : new EntityList(Main.SELECT)) {
						if (entity != Main.SELECT.getTarget() && selectRectangleHelper.contains(entity) && selectRectangleEntities.contains(entity)) {
							Main.SELECT.remove(entity);
						}
					}
				} else if (event.isShiftDown()) {
					selectRectangleHelper.removeAll(selectRectangleEntities);
					Main.SELECT.addAll(selectRectangleHelper, true);
					Main.SELECT.addAll(selectRectangleEntities, true);
				} else {
					selectRectangleHelper.removeAll(selectRectangleEntities);
					Main.SELECT.addAll(selectRectangleEntities, true);
				}
				
				Reload.start();
			}
		}
	}
	private void onMouseRelease(MouseEvent event) {
		if (event.getButton() == MouseButton.PRIMARY && this.getChildren().contains(selectRectangle)) {
			selectRectangle.setWidth(0);
			selectRectangle.setHeight(0);
			selectRectangle.setX(0);
			selectRectangle.setY(0);
			
			this.getChildren().remove(selectRectangle);
			
			Reload.start();
		}
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
		EntityList representingEntityList = Main.FILTER.createRepresentingList();
		if (representingEntityList.size() > TILE_LIMIT) {
			tileEntities.setAll(representingEntityList.subList(0, TILE_LIMIT));
			new SimpleMessageStage("Error", "Gallery reached a limit of " + TILE_LIMIT + " tiles.").show();
		} else {
			tileEntities.setAll(representingEntityList);
		}
		
		tiles.clear();
		
		for (Entity entity : tileEntities) {
			tiles.add(entity.getTile());
			Reload.requestBorderUpdate(entity);
			entity.getTile().updateGroupIcon();
		}
		
		tilePane.getChildren().setAll(tiles);
		
		this.moveViewportToTarget();
		
		return true;
	}
	public void moveViewportToTarget() {
		if (this.getHeight() > 0) {
			Entity currentTarget = Main.SELECT.getTarget();
			if (!UserInterface.getCenterPane().isViewGallery() || currentTarget == null) return;
			if (currentTarget.hasGroup() && !currentTarget.getEntityGroup().isOpen()) {
				currentTarget = currentTarget.getEntityGroup().getFirst();
			}
			int targetIndex = tileEntities.indexOf(currentTarget);
			if (targetIndex >= 0) {
				int columnCount = tilePane.getPrefColumns();
				int targetRow = targetIndex / columnCount;
				
				Bounds reverseBounds = this.getViewportBounds();
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
				
				double vValue = -1;
				if (tileBottom > viewportBottom) {
					vValue = (targetRow + 1) * rowToContentRatio - viewportToContentRatio;
				} else if (tileTop < viewportTop) {
					vValue = targetRow * rowToContentRatio;
				}
				if (vValue >= 0) {
					this.setVvalue(vValue);
				}
			}
		}
	}
	
	public TilePane getTilePane() {
		return tilePane;
	}
}
