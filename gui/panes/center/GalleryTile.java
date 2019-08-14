package application.gui.panes.center;

import application.database.list.CustomList;
import application.database.object.DataObject;
import application.gui.decorator.Decorator;
import application.gui.nodes.simple.TextNode;
import application.main.Instances;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.effect.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class GalleryTile extends ImageView {
	private static final InnerShadow effectSelect = createEffectSelect();
	private static final ColorInput effectTarget = createEffectTarget();
	private static int effectGroupSize;
	
	private final DataObject parentDataObject;
	
	public GalleryTile(DataObject parentDataObject, Image image) {
		super(image);
		this.parentDataObject = parentDataObject;
		new BaseTileEvent(this);
	}
	
	public void requestEffect() {
		Instances.getReload().requestTileEffect(this);
	}
	public void generateEffect() {
		if (parentDataObject == null) return;
		
		CustomList<Effect> effectList = new CustomList<>();
		
		if (Instances.getSelect().contains(parentDataObject)) {
			effectList.add(effectSelect);
		}
		if (parentDataObject.equals(Instances.getTarget().getCurrentTarget())) {
			effectList.add(effectTarget);
		}
		if (parentDataObject.getMergeID() != 0) {
			String middle;
			CustomList<DataObject> mergeGroup = parentDataObject.getMergeGroup();
			if (Instances.getGalleryPane().getExpandedGroups().contains(parentDataObject.getMergeID())) {
				middle = (mergeGroup.indexOf(parentDataObject) + 1) + "/" + mergeGroup.size();
			} else {
				middle = String.valueOf(mergeGroup.size());
			}
			
			String groupIconText;
			if (Instances.getObjectListMain().getAllGroups().indexOf(parentDataObject.getMergeID()) % 2 == 0) {
				groupIconText = "(" + middle + ")";
			} else {
				groupIconText = "[" + middle + "]";
			}
			int tileSize = Instances.getSettings().getGalleryTileSize();
			Image imageText = textToImage(groupIconText);
			effectList.add(new ImageInput(imageText, tileSize - imageText.getWidth() - 5, 1));
			effectGroupSize = (int) imageText.getWidth() + 10;
		}
		
		if (effectList.size() == 0) {
			this.setEffect(null);
		} else {
			Blend helperEffect = new Blend();
			for (Effect effect : effectList) {
				helperEffect = new Blend(BlendMode.SRC_ATOP, helperEffect, effect);
			}
			this.setEffect(helperEffect);
		}
	}
	
	private static InnerShadow createEffectSelect() {
		InnerShadow innerShadow = new InnerShadow();
		innerShadow.setColor(Color.RED);
		innerShadow.setOffsetX(0);
		innerShadow.setOffsetY(0);
		innerShadow.setWidth(5);
		innerShadow.setHeight(5);
		innerShadow.setChoke(1);
		return innerShadow;
	}
	private static ColorInput createEffectTarget() {
		int markSize = 6;
		int markPositionInTile = (Instances.getSettings().getGalleryTileSize() - markSize) / 2;
		return new ColorInput(markPositionInTile, markPositionInTile, markSize, markSize, Color.RED);
	}
	
	public static Image textToImage(String text) {
		TextNode label = new TextNode(text);
		label.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, null, null)));
		label.setTextFill(Color.RED);
		label.setWrapText(true);
		label.setFont(Decorator.getFont());
		label.setPadding(new Insets(0, 0, 0, 0));
		
		Text theText = new Text(label.getText());
		theText.setFont(label.getFont());
		int width = (int) theText.getBoundsInLocal().getWidth();
		int height = (int) theText.getBoundsInLocal().getHeight();
		
		WritableImage img = new WritableImage(width, height);
		Scene scene = new Scene(new Group(label));
		scene.setFill(Color.TRANSPARENT);
		scene.snapshot(img);
		return img;
	}
	
	public static int getEffectGroupSize() {
		return effectGroupSize;
	}
	public DataObject getParentDataObject() {
		return parentDataObject;
	}
}
