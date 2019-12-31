package ui.main.center;

import base.CustomList;
import base.entity.Entity;
import control.Select;
import control.Target;
import control.reload.ChangeIn;
import control.reload.Reload;
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
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import misc.Settings;
import ui.component.clickmenu.ClickMenu;
import ui.stage.StageManager;

public class GalleryTile extends Pane {
	public static final double SELECT_BORDER_PADDING = 10;
	
	private final Entity entity;
	private ImageView imageView;
	
	private BorderPane selectBorder;
	private static Background selectBorderBackground;
	
	private static Image loadingImage = null;
	
	private static Effect effectGroupExpand;
	private static Effect effectGroupCollapse;
	private static double effectSize;
	private static double effectX;
	private static double effectY;
	
	public GalleryTile(Entity entity) {
		this.entity = entity;
		
		imageView = new ImageView(loadingImage);
		selectBorder = new BorderPane(imageView);
		selectBorder.setPadding(new Insets(SELECT_BORDER_PADDING));
		
		if (entity.getCollectionID() != 0) {
			if (entity.getCollection().getFirst().equals(entity)) {
				this.setEffect(effectGroupExpand);
			} else {
				this.setEffect(effectGroupCollapse);
			}
		}
		
		this.getChildren().add(selectBorder);
		this.initEvents();
		
		ClickMenu.install(imageView, MouseButton.SECONDARY, ClickMenu.StaticInstance.ENTITY);
	}
	
	public static void init() {
		selectBorderBackground = new Background(new BackgroundFill(Color.rgb(120, 120, 120), null, null));
		
		int tileSize = Settings.getTileSize();
		loadingImage = new WritableImage(tileSize, tileSize) {{
			Label label = new Label("Loading");
			label.setBackground(new Background(new BackgroundFill(Color.GRAY, null, null)));
			label.setWrapText(true);
			label.setFont(new Font(26));
			label.setAlignment(Pos.CENTER);
			
			label.setMinWidth(tileSize);
			label.setMinHeight(tileSize);
			label.setMaxWidth(tileSize);
			label.setMaxHeight(tileSize);
			
			Scene scene = new Scene(new Group(label));
			scene.setFill(Color.GRAY);
			scene.snapshot(this);
		}};
		
		Image imageExpand = new Image("/plus-16px.png");
		Image imageCollapse = new Image("/minus-16px.png");
		
		double tileSizeWithBorder = tileSize + 2 * SELECT_BORDER_PADDING;
		
		effectSize = imageExpand.getWidth();
		effectX = tileSizeWithBorder - SELECT_BORDER_PADDING - imageExpand.getWidth() - 5;
		effectY = SELECT_BORDER_PADDING + 5;
		
		ColorInput effectColor = new ColorInput(0, 0, tileSizeWithBorder, tileSizeWithBorder, Color.WHITE);
		effectGroupExpand = new Blend(BlendMode.DIFFERENCE, new Blend(BlendMode.SRC_ATOP, new ImageInput(imageExpand, effectX, effectY), effectColor), null);
		effectGroupCollapse = new Blend(BlendMode.DIFFERENCE, new Blend(BlendMode.SRC_ATOP, new ImageInput(imageCollapse, effectX, effectY), effectColor), null);
	}
	
	public void updateSelectBorder() {
		if (Select.getEntities().contains(entity)) {
			selectBorder.setBackground(selectBorderBackground);
		} else {
			selectBorder.setBackground(null);
		}
	}
	public void updateGroupIcon() {
		if (entity.getCollection().getFirst().equals(entity)) {
			if (!PaneGallery.get().getExpandedCollections().contains(entity.getCollectionID())) {
				this.setEffect(effectGroupExpand);
			} else {
				this.setEffect(effectGroupCollapse);
			}
		} else {
			this.setEffect(effectGroupCollapse);
		}
	}
	
	private void initEvents() {
		this.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
			switch (event.getButton()) {
				case PRIMARY:
					if (isClickOnCollectionIcon(event)) {
						onGroupIconClick();
						Reload.start();
					} else {
						if (event.getClickCount() % 2 != 0) {
							onLeftClick(event);
						} else {
							onLeftDoubleClick();
						}
					}
					break;
				case SECONDARY:
					onRightClick();
					break;
			}
		});
		
		Background backgroundHover = new Background(new BackgroundFill(Color.rgb(90, 90, 90), null, null));
		Background backgroundSelect = new Background(new BackgroundFill(Color.rgb(120, 120, 120), null, null));
		Background BackgroundSelectHover = new Background(new BackgroundFill(Color.rgb(150, 150, 150), null, null));
		
		this.addEventFilter(MouseEvent.MOUSE_ENTERED, event -> {
			if (Select.getEntities().contains(entity)) {
				selectBorder.setBackground(BackgroundSelectHover);
			} else {
				selectBorder.setBackground(backgroundHover);
			}
		});
		this.addEventFilter(MouseEvent.MOUSE_EXITED, event -> {
			if (Select.getEntities().contains(entity)) {
				selectBorder.setBackground(backgroundSelect);
			} else {
				selectBorder.setBackground(null);
			}
		});
	}
	private void onLeftClick(MouseEvent event) {
		Target.set(entity);
		
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
		
		Reload.start();
		
		ClickMenu.hideAll();
	}
	private void onLeftDoubleClick() {
		StageManager.getStageMain().getSceneMain().viewEntity();
		Reload.start();
	}
	private void onRightClick() {
		if (!Select.getEntities().contains(entity)) {
			Select.getEntities().set(entity);
		}
		
		//additional functionality from ClickMenu
		
		Target.set(entity);
		Reload.start();
	}
	public void onGroupIconClick() {
		int collectionID = entity.getCollectionID();
		
		if (collectionID != 0) {
			CustomList<Integer> expandedGroups = PaneGallery.get().getExpandedCollections();
			if (expandedGroups.contains(collectionID)) {
				//noinspection RedundantCollectionOperation
				expandedGroups.remove(expandedGroups.indexOf(collectionID));
			} else {
				expandedGroups.add(collectionID);
			}
			
			entity.getCollection().getFirst().getGalleryTile().updateGroupIcon();
			
			Target.set(entity);
			Reload.notify(ChangeIn.ENTITY_LIST_MAIN);
		}
	}
	private boolean isClickOnCollectionIcon(MouseEvent event) {
		if (entity.getCollectionID() == 0) return false;
		boolean hitWidth = event.getX() >= effectX && event.getX() <= effectX + effectSize;
		boolean hitHeight = event.getY() <= effectY + effectSize && event.getY() >= effectY;
		return hitWidth && hitHeight;
	}
	
	public Entity getEntity() {
		return entity;
	}
	
	public void setImage(Image image) {
		if (image == null) imageView.setImage(loadingImage);
		else imageView.setImage(image);
	}
}
