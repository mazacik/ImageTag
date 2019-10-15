package application.gui.panes.center;

import application.control.Reload;
import application.data.list.CustomList;
import application.data.object.DataObject;
import application.gui.nodes.ClickMenu;
import application.gui.nodes.simple.TextNode;
import application.gui.stage.Stages;
import application.main.Instances;
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

public class GalleryTile extends Pane {
	private final DataObject dataObject;
	private int groupEffectWidth;
	private int groupEffectHeight;
	
	private ImageView imageView;
	private BorderPane selectHelper;
	
	public GalleryTile(DataObject dataObject, Image image) {
		this.dataObject = dataObject;
		
		imageView = new ImageView(image);
		selectHelper = new BorderPane(imageView);
		selectHelper.setPadding(new Insets(10));
		
		this.getChildren().add(selectHelper);
		
		initEvents();
		ClickMenu.install(imageView, MouseButton.SECONDARY, ClickMenu.StaticInstance.DATA);
	}
	
	public void generateEffect() {
		if (Instances.getSelect().contains(dataObject)) {
			selectHelper.setBackground(new Background(new BackgroundFill(Color.rgb(120, 120, 120), null, null)));
		} else {
			selectHelper.setBackground(null);
		}
		
		if (dataObject.getJointID() != 0) {
			imageView.setEffect(this.createJointObjectEffect(dataObject));
		}
	}
	private Blend createJointObjectEffect(DataObject dataObject) {
		String middle;
		CustomList<DataObject> jointObject = dataObject.getJointObjects();
		if (Instances.getGalleryPane().getExpandedGroups().contains(dataObject.getJointID())) {
			middle = (jointObject.indexOf(dataObject) + 1) + "/" + jointObject.size();
		} else {
			middle = String.valueOf(jointObject.size());
		}
		
		Image imageText = textToImage("[" + middle + "]");
		groupEffectWidth = (int) imageText.getWidth();
		groupEffectHeight = (int) imageText.getHeight() + 10; //helper insets
		int tileSize = Instances.getSettings().getGalleryTileSize();
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
						Instances.getReload().doReload();
						Instances.getGalleryPane().updateViewportTilesVisibility();
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
			if (Instances.getSelect().contains(dataObject)) {
				selectHelper.setBackground(new Background(new BackgroundFill(Color.rgb(150, 150, 150), null, null)));
			} else {
				selectHelper.setBackground(new Background(new BackgroundFill(Color.rgb(90, 90, 90), null, null)));
			}
		});
		this.addEventFilter(MouseEvent.MOUSE_EXITED, event -> {
			if (Instances.getSelect().contains(dataObject)) {
				selectHelper.setBackground(new Background(new BackgroundFill(Color.rgb(120, 120, 120), null, null)));
			} else {
				selectHelper.setBackground(null);
			}
		});
	}
	private void onLeftClick(MouseEvent event) {
		Instances.getTarget().set(dataObject);
		
		if (event.isControlDown()) {
			Instances.getSelect().swapState(dataObject);
		} else if (event.isShiftDown()) {
			Instances.getSelect().shiftSelectTo(dataObject);
		} else {
			Instances.getSelect().set(dataObject);
		}
		
		Instances.getReload().doReload();
		
		ClickMenu.hideAll();
	}
	private void onLeftDoubleClick(MouseEvent event) {
		if (!isClickOnGroupEffect(event)) {
			Stages.getMainStage().swapViewMode();
			Instances.getReload().doReload();
		}
	}
	private void onRightClick() {
		if (!Instances.getSelect().contains(dataObject)) {
			Instances.getSelect().set(dataObject);
		}
		
		Instances.getTarget().set(dataObject);
		Instances.getReload().doReload();
	}
	private boolean isClickOnGroupEffect(MouseEvent event) {
		int tileSize = Instances.getSettings().getGalleryTileSize();
		boolean hitWidth = event.getX() >= tileSize - groupEffectWidth - 5;
		boolean hitHeight = event.getY() <= groupEffectHeight;
		return hitWidth && hitHeight;
	}
	public void onGroupEffectClick() {
		int jointID = dataObject.getJointID();
		
		if (jointID != 0) {
			CustomList<Integer> expandedGroups = Instances.getGalleryPane().getExpandedGroups();
			if (!expandedGroups.contains(jointID)) {
				expandedGroups.add(jointID);
			} else {
				//noinspection RedundantCollectionOperation
				expandedGroups.remove(expandedGroups.indexOf(jointID));
			}
			
			Instances.getReload().notify(Reload.Control.DATA);
		}
	}
	
	public DataObject getDataObject() {
		return dataObject;
	}
	public Image getImage() {
		return imageView.getImage();
	}
	public void setImage(Image image) {
		imageView.setImage(image);
	}
}
