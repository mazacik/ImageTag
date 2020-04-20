package client.ui.main.gallery;

import client.ui.custom.ClickMenu;
import client.ui.custom.ListMenu;
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
import main.Root;
import server.base.entity.Entity;
import server.control.reload.Reload;
import server.enums.Direction;
import server.misc.Settings;

public class Tile extends Pane {
	public static final double HIGHLIGHT_PADDING = 5;
	
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
		int tileSize = Settings.GALLERY_TILE_SIZE.getInteger();
		
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
		
		ClickMenu.register(Tile.class, ListMenu.Preset.ENTITY);
	}
	
	private final Entity entity;
	
	public Tile(Entity entity) {
		this.entity = entity;
		
		this.updateBorder();
		this.updateCollectionIcon();
		
		ImageView imageView = new ImageView(loadingImage);
		imageView.setX(HIGHLIGHT_PADDING);
		imageView.setY(HIGHLIGHT_PADDING);
		
		this.getChildren().add(imageView);
		this.setPadding(new Insets(HIGHLIGHT_PADDING));
		
		this.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
			if (event.isStillSincePress()) {
				switch (event.getButton()) {
					case PRIMARY:
						ListMenu.hideMenus();
						
						boolean hitWidth = event.getX() >= collectionIconX && event.getX() <= collectionIconX + collectionIconSize;
						boolean hitHeight = event.getY() <= collectionIconY + collectionIconSize && event.getY() >= collectionIconY;
						
						if (entity.hasCollection() && hitWidth && hitHeight) {
							this.clickOnCollectionIcon();
						} else {
							this.clickOnTile(event);
						}
						
						Reload.start();
						break;
					case SECONDARY:
						//additional functionality from ClickMenu
						
						if (!Root.SELECT.contains(entity)) Root.SELECT.set(entity);
						Root.SELECT.setTarget(entity);
						
						Reload.start();
						break;
				}
			}
		});
		this.addEventFilter(MouseEvent.MOUSE_ENTERED, event -> {
			if (Root.SELECT.contains(entity)) {
				this.setBackground(highlightSelectHover);
			} else {
				this.setBackground(highlightHover);
			}
		});
		this.addEventFilter(MouseEvent.MOUSE_EXITED, event -> {
			if (Root.SELECT.contains(entity)) {
				this.setBackground(highlightSelect);
			} else {
				this.setBackground(null);
			}
		});
		
		ClickMenu.install(this, Direction.NONE, MouseButton.SECONDARY);
	}
	
	private void clickOnTile(MouseEvent event) {
		if (event.getClickCount() % 2 == 0) {
			Root.PSC.MAIN_STAGE.viewDisplay();
		} else {
			if (event.isShiftDown()) {
				Root.SELECT.shiftSelectTo(entity);
			} else if (event.isControlDown()) {
				if (Root.SELECT.contains(entity)) {
					Root.SELECT.remove(entity);
				} else {
					Root.SELECT.add(entity);
				}
				Root.SELECT.setupShiftSelect();
			} else {
				Root.SELECT.set(entity);
				Root.SELECT.setupShiftSelect();
			}
			Root.SELECT.setTarget(entity);
		}
	}
	private void clickOnCollectionIcon() {
		Root.SELECT.setTarget(entity);
		//Root.SELECT.setAll(entity.getCollection());
		if (entity.hasCollection()) {
			entity.getCollection().toggle();
		}
	}
	
	public void updateBorder() {
		if (Root.SELECT.contains(entity)) {
			this.setBackground(highlightSelect);
		} else {
			this.setBackground(null);
		}
	}
	public void updateCollectionIcon() {
		if (entity.hasCollection()) {
			if (entity.getCollection().isOpen()) {
				this.setEffect(collectionIconMinus);
			} else {
				this.setEffect(collectionIconPlus);
			}
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
