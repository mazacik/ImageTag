package application.frontend.pane.center;

import application.backend.base.CustomList;
import application.backend.base.entity.Entity;
import application.backend.control.Reload;
import application.backend.util.EntityGroupUtil;
import application.frontend.component.ClickMenu;
import application.frontend.component.simple.TextNode;
import application.frontend.stage.StageManager;
import application.main.InstanceCollector;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.effect.Blend;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.ImageInput;
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
import javafx.scene.text.Text;

public class GalleryTile extends Pane implements InstanceCollector {
	private final Entity entity;
	private int groupEffectWidth;
	private int groupEffectHeight;
	
	private ImageView imageView;
	private BorderPane selectBorder;
	
	public GalleryTile(Entity entity) {
		this.entity = entity;
		
		imageView = new ImageView();
		selectBorder = new BorderPane(imageView);
		selectBorder.setPadding(new Insets(10));
		
		this.getChildren().add(selectBorder);
		
		initEvents();
		
		ClickMenu.install(imageView, MouseButton.SECONDARY, ClickMenu.StaticInstance.DATA);
	}
	
	public void updateSelectBorder() {
		if (select.contains(entity)) {
			selectBorder.setBackground(new Background(new BackgroundFill(Color.rgb(120, 120, 120), null, null)));
		} else {
			selectBorder.setBackground(null);
		}
		
		if (entity.getEntityGroupID() != 0) {
			imageView.setEffect(this.createentityGroupEffect(entity));
		}
	}
	private Blend createentityGroupEffect(Entity entity) {
		String middle;
		CustomList<Entity> entityGroup = EntityGroupUtil.getEntityGroup(entity);
		if (galleryPane.getExpandedGroups().contains(entity.getEntityGroupID())) {
			middle = (entityGroup.indexOf(entity) + 1) + "/" + entityGroup.size();
		} else {
			middle = String.valueOf(entityGroup.size());
		}
		
		Image imageText = textToImage("[" + middle + "]");
		groupEffectWidth = (int) imageText.getWidth();
		groupEffectHeight = (int) imageText.getHeight() + 10; //helper insets
		int tileSize = settings.getGalleryTileSize();
		return new Blend(BlendMode.SRC_OVER, null, new ImageInput(imageText, tileSize - imageText.getWidth() - 5, 0));
	}
	public Image textToImage(String text) {
		TextNode textNode = new TextNode(text);
		textNode.setTextFill(Color.RED);
		
		Text t = new Text(textNode.getText());
		t.setFont(textNode.getFont());
		int width = (int) t.getBoundsInLocal().getWidth();
		int height = (int) t.getBoundsInLocal().getHeight();
		
		WritableImage img = new WritableImage(width, height);
		Scene scene = new Scene(textNode);
		scene.setFill(Color.TRANSPARENT);
		scene.snapshot(img);
		return img;
	}
	
	private void initEvents() {
		this.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
			switch (event.getButton()) {
				case PRIMARY:
					if (isClickOnGroupEffect(event)) {
						onGroupEffectClick();
						reload.doReload();
						galleryPane.updateViewportTilesVisibility();
					} else {
						if (event.getClickCount() % 2 != 0) onLeftClick(event);
						else onLeftDoubleClick(event);
					}
					break;
				case SECONDARY:
					onRightClick();
					break;
			}
		});
		this.addEventFilter(MouseEvent.MOUSE_ENTERED, event -> {
			if (select.contains(entity)) {
				selectBorder.setBackground(new Background(new BackgroundFill(Color.rgb(150, 150, 150), null, null)));
			} else {
				selectBorder.setBackground(new Background(new BackgroundFill(Color.rgb(90, 90, 90), null, null)));
			}
		});
		this.addEventFilter(MouseEvent.MOUSE_EXITED, event -> {
			if (select.contains(entity)) {
				selectBorder.setBackground(new Background(new BackgroundFill(Color.rgb(120, 120, 120), null, null)));
			} else {
				selectBorder.setBackground(null);
			}
		});
	}
	private void onLeftClick(MouseEvent event) {
		target.set(entity);
		
		if (event.isControlDown()) {
			select.swapState(entity);
		} else if (event.isShiftDown()) {
			select.shiftSelectTo(entity);
		} else {
			select.set(entity);
		}
		
		reload.doReload();
		
		ClickMenu.hideAll();
	}
	private void onLeftDoubleClick(MouseEvent event) {
		if (!isClickOnGroupEffect(event)) {
			StageManager.getMainStage().swapViewMode();
			reload.doReload();
		}
	}
	private void onRightClick() {
		if (!select.contains(entity)) {
			select.set(entity);
		}
		
		target.set(entity);
		reload.doReload();
	}
	private boolean isClickOnGroupEffect(MouseEvent event) {
		int tileSize = settings.getGalleryTileSize();
		boolean hitWidth = event.getX() >= tileSize - groupEffectWidth - 5;
		boolean hitHeight = event.getY() <= groupEffectHeight;
		return hitWidth && hitHeight;
	}
	public void onGroupEffectClick() {
		int entityGroupID = entity.getEntityGroupID();
		
		if (entityGroupID != 0) {
			CustomList<Integer> expandedGroups = galleryPane.getExpandedGroups();
			if (!expandedGroups.contains(entityGroupID)) {
				expandedGroups.add(entityGroupID);
			} else {
				//noinspection RedundantCollectionOperation
				expandedGroups.remove(expandedGroups.indexOf(entityGroupID));
			}
			
			reload.notify(Reload.Control.DATA);
		}
	}
	
	public Entity getEntity() {
		return entity;
	}
	public Image getImage() {
		return imageView.getImage();
	}
	public void setImage(Image image) {
		imageView.setImage(image);
	}
}
