package frontend.component.center.gallery;

import backend.entity.Entity;
import backend.misc.Direction;
import backend.reload.Reload;
import backend.settings.Settings;
import frontend.UserInterface;
import frontend.node.menu.ListMenu;
import frontend.node.menu.MenuPreset;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.effect.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import main.Main;

public class Tile extends Pane {
	public static final double HIGHLIGHT_PADDING = 3;
	
	private static final double groupIconSize;
	private static final double groupIconX;
	private static final double groupIconY;
	private static final Effect groupIconPlus;
	private static final Effect groupIconMinus;
	
	private static final Background highlightHover;
	private static final Background highlightSelect;
	private static final Background highlightSelectHover;
	
	private static final Image loadingImage;
	
	private final ImageView imageView;
	
	static {
		int tileSize = Settings.GALLERY_TILE_SIZE.getIntValue();
		
		Image imagePlus = new Image("/plus-16px.png");
		Image imageMinus = new Image("/minus-16px.png");
		
		groupIconSize = imagePlus.getWidth();
		groupIconX = tileSize + HIGHLIGHT_PADDING - imagePlus.getWidth() - 5;
		groupIconY = HIGHLIGHT_PADDING + 5;
		
		double tileSizeWithHighlight = tileSize + 2 * HIGHLIGHT_PADDING;
		ColorInput effectColor = new ColorInput(0, 0, tileSizeWithHighlight, tileSizeWithHighlight, Color.WHITE);
		groupIconPlus = new Blend(BlendMode.DIFFERENCE, new Blend(BlendMode.SRC_ATOP, new ImageInput(imagePlus, groupIconX, groupIconY), effectColor), null);
		groupIconMinus = new Blend(BlendMode.DIFFERENCE, new Blend(BlendMode.SRC_ATOP, new ImageInput(imageMinus, groupIconX, groupIconY), effectColor), null);
		
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
			
			if (Thread.currentThread() == Main.THREAD_MAIN) {
				scene.snapshot(this);
			} else {
				Platform.runLater(() -> scene.snapshot(this));
			}
		}};
	}
	
	private final Entity entity;
	
	public Tile(Entity entity) {
		this.entity = entity;
		
		imageView = new ImageView(loadingImage);
		imageView.setX(HIGHLIGHT_PADDING);
		imageView.setY(HIGHLIGHT_PADDING);
		
		this.getChildren().add(imageView);
		this.setPadding(new Insets(HIGHLIGHT_PADDING));
		
		this.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
			if (event.isStillSincePress()) {
				switch (event.getButton()) {
					case PRIMARY:
						ListMenu.hideMenus();
						
						boolean hitWidth = event.getX() >= groupIconX && event.getX() <= groupIconX + groupIconSize;
						boolean hitHeight = event.getY() <= groupIconY + groupIconSize && event.getY() >= groupIconY;
						
						if (entity.hasGroup() && hitWidth && hitHeight) {
							this.clickOnGroupIcon();
						} else {
							this.clickOnTile(event);
						}
						
						Reload.start();
						break;
					case SECONDARY:
						//additional functionality from ClickMenu
						
						if (!Main.SELECT.contains(entity)) Main.SELECT.set(entity);
						Main.SELECT.setTarget(entity);
						
						Reload.start();
						break;
				}
			}
		});
		this.addEventFilter(MouseEvent.MOUSE_ENTERED, event -> {
			if (Main.SELECT.contains(entity)) {
				this.setBackground(highlightSelectHover);
			} else {
				this.setBackground(highlightHover);
			}
		});
		this.addEventFilter(MouseEvent.MOUSE_EXITED, event -> {
			if (Main.SELECT.contains(entity)) {
				this.setBackground(highlightSelect);
			} else {
				this.setBackground(null);
			}
		});
		
		ListMenu.install(this, Direction.NONE, ListMenu.MenuTrigger.CLICK_RIGHT, MenuPreset.ENTITY_TILE.getTemplate());
	}
	
	private void clickOnTile(MouseEvent event) {
		if (event.getClickCount() % 2 == 0) {
			UserInterface.getCenterPane().swapCurrentPane();
		} else {
			if (event.isShiftDown()) {
				Main.SELECT.shiftSelectTo(entity);
			} else if (event.isControlDown()) {
				if (Main.SELECT.contains(entity)) {
					Main.SELECT.remove(entity);
				} else {
					Main.SELECT.add(entity);
				}
				Main.SELECT.setupShiftSelect();
			} else {
				Main.SELECT.set(entity);
				Main.SELECT.setupShiftSelect();
			}
			Main.SELECT.setTarget(entity);
		}
	}
	private void clickOnGroupIcon() {
		Main.SELECT.setTarget(entity);
		if (entity.hasGroup()) {
			entity.getGroup().toggle();
		}
	}
	
	public void updateBorder() {
		if (Main.SELECT.contains(entity)) {
			this.setBackground(highlightSelect);
		} else {
			this.setBackground(null);
		}
	}
	public void updateGroupIcon() {
		if (entity.hasGroup()) {
			if (entity.getGroup().isOpen()) {
				this.setEffect(groupIconMinus);
			} else {
				this.setEffect(groupIconPlus);
			}
		}
	}
	
	public Entity getEntity() {
		return entity;
	}
	
	public synchronized void setImage(Image image) {
		if (image != null) {
			imageView.setImage(image);
		} else {
			imageView.setImage(loadingImage);
		}
	}
}
