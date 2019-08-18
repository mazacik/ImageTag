package application.gui.panes.center;

import application.database.list.CustomList;
import application.database.object.DataObject;
import application.gui.nodes.simple.TextNode;
import application.main.Instances;
import javafx.scene.Scene;
import javafx.scene.effect.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class GalleryTile extends ImageView {
	private static final InnerShadow effectSelect = createEffectSelect();
	private static final ColorInput effectTarget = createEffectTarget();
	
	private final DataObject parentDataObject;
	private int groupEffectWidth;
	private int groupEffectHeight;
	
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
			effectList.add(new ImageInput(imageText, tileSize - imageText.getWidth() - 5, 0));
			groupEffectWidth = (int) imageText.getWidth() + 5;
			groupEffectHeight = (int) imageText.getHeight();
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
	
	public int getGroupEffectWidth() {
		return groupEffectWidth;
	}
	public int getGroupEffectHeight() {
		return groupEffectHeight;
	}
	public DataObject getParentDataObject() {
		return parentDataObject;
	}
}
