package ui.main.gallery;

import base.entity.Entity;
import base.entity.EntityCollectionUtil;
import control.Select;
import control.reload.Reload;
import enums.Direction;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.effect.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import misc.Settings;
import ui.custom.ClickMenu;
import ui.main.stage.MainStage;
import ui.node.NodeTemplates;

public class Tile extends Pane {
	public static final double HIGHLIGHT_PADDING = 10;
	
	private static final double collectionIconSize;
	private static final double collectionIconX;
	private static final double collectionIconY;
	private static final Effect collectionIconPlus;
	private static final Effect collectionIconMinus;
	
	private static final Background highlightHover;
	private static final Background highlightSelect;
	private static final Background highlightSelectHover;
	
	private static final Image loadingImage;
	
	static {
		int tileSize = Settings.GALLERY_TILE_SIZE.getIntegerValue();
		
		Image imagePlus = new Image("/plus-16px.png");
		Image imageMinus = new Image("/minus-16px.png");
		
		double tileSizeWithHighlight = tileSize + 2 * HIGHLIGHT_PADDING;
		
		collectionIconSize = imagePlus.getWidth();
		collectionIconX = tileSizeWithHighlight - HIGHLIGHT_PADDING - imagePlus.getWidth() - 5;
		collectionIconY = HIGHLIGHT_PADDING + 5;
		
		ColorInput effectColor = new ColorInput(0, 0, tileSizeWithHighlight, tileSizeWithHighlight, Color.WHITE);
		collectionIconPlus = new Blend(BlendMode.DIFFERENCE, new Blend(BlendMode.SRC_ATOP, new ImageInput(imagePlus, collectionIconX, collectionIconY), effectColor), null);
		collectionIconMinus = new Blend(BlendMode.DIFFERENCE, new Blend(BlendMode.SRC_ATOP, new ImageInput(imageMinus, collectionIconX, collectionIconY), effectColor), null);
		
		highlightHover = new Background(new BackgroundFill(Color.rgb(90, 90, 90), null, null));
		highlightSelect = new Background(new BackgroundFill(Color.rgb(120, 120, 120), null, null));
		highlightSelectHover = new Background(new BackgroundFill(Color.rgb(150, 150, 150), null, null));
		
		loadingImage = new WritableImage(tileSize, tileSize) {{
			Label label = new Label("Loading");
			label.setWrapText(true);
			label.setAlignment(Pos.CENTER);
			label.setFont(new Font(26));
			label.setBackground(new Background(new BackgroundFill(Color.GRAY, null, null)));
			
			label.setMinWidth(tileSize);
			label.setMinHeight(tileSize);
			
			label.setMaxWidth(tileSize);
			label.setMaxHeight(tileSize);
			
			Scene scene = new Scene(new Group(label));
			scene.setFill(Color.GRAY);
			scene.snapshot(this);
		}};
		
		ClickMenu.register(Tile.class, Direction.NONE, MouseButton.SECONDARY
				, NodeTemplates.FILE.get()
				, NodeTemplates.SELECTION.get()
		);
	}
	
	private final Entity entity;
	
	public Tile(Entity entity) {
		this.entity = entity;
		
		ImageView imageView = new ImageView(loadingImage);
		imageView.setX(HIGHLIGHT_PADDING);
		imageView.setY(HIGHLIGHT_PADDING);
		
		if (entity.getCollectionID() != 0) {
			if (entity.getCollection().getFirst() == entity) {
				this.setEffect(collectionIconPlus);
			} else {
				this.setEffect(collectionIconMinus);
			}
		}
		
		this.getChildren().add(imageView);
		this.setPadding(new Insets(HIGHLIGHT_PADDING));
		
		this.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
			if (!event.isStillSincePress()) return;
			switch (event.getButton()) {
				case PRIMARY:
					ClickMenu.hideMenus();
					
					boolean hitWidth = event.getX() >= collectionIconX && event.getX() <= collectionIconX + collectionIconSize;
					boolean hitHeight = event.getY() <= collectionIconY + collectionIconSize && event.getY() >= collectionIconY;
					if (entity.getCollectionID() != 0 && hitWidth && hitHeight) {
						EntityCollectionUtil.openCollection(entity);
					} else {
						if (event.getClickCount() % 2 != 0) {
							if (event.isControlDown()) {
								if (Select.getEntities().contains(entity)) {
									Select.getEntities().remove(entity);
								} else {
									Select.getEntities().add(entity);
								}
							} else if (event.isShiftDown()) {
								Select.shiftSelectTo(entity);
							} else {
								Select.getEntities().set(entity);
							}
							Select.setTarget(entity);
						} else {
							MainStage.getMainScene().viewEntity();
						}
					}
					
					Reload.start();
					break;
				case SECONDARY:
					if (!Select.getEntities().contains(entity)) {
						Select.getEntities().set(entity);
					}
					
					//additional functionality from ClickMenu
					
					Select.setTarget(entity);
					Reload.start();
					break;
			}
		});
		this.addEventFilter(MouseEvent.MOUSE_ENTERED, event -> {
			if (Select.getEntities().contains(entity)) {
				this.setBackground(highlightSelectHover);
			} else {
				this.setBackground(highlightHover);
			}
		});
		this.addEventFilter(MouseEvent.MOUSE_EXITED, event -> {
			if (Select.getEntities().contains(entity)) {
				this.setBackground(highlightSelect);
			} else {
				this.setBackground(null);
			}
		});
		
		ClickMenu.install(this);
	}
	
	public void updateHighlight() {
		if (Select.getEntities().contains(entity)) {
			this.setBackground(highlightSelect);
		} else {
			this.setBackground(null);
		}
	}
	public void updateCollectionIcon() {
		if (!EntityCollectionUtil.getOpenCollections().contains(entity.getCollectionID())) {
			this.setEffect(collectionIconPlus);
		} else {
			this.setEffect(collectionIconMinus);
		}
	}
	
	public Entity getEntity() {
		return entity;
	}
	
	public synchronized void setImage(Image image) {
		ImageView imageView = ((ImageView) this.getChildren().get(0));
		if (image != null) {
			imageView.setImage(image);
		} else {
			imageView.setImage(loadingImage);
		}
	}
}
