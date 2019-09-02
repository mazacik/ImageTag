package application.gui.panes.center;

import application.database.list.CustomList;
import application.database.object.DataObject;
import application.gui.decorator.SizeUtil;
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
	private static final ColorInput effectOverlay = createEffectOverlay();
	private static final InnerShadow effectBorder = createEffectBorder();
	
	private final DataObject parentDataObject;
	private int groupEffectWidth;
	private int groupEffectHeight;
	
	public GalleryTile(DataObject parentDataObject, Image image) {
		super(image);
		this.parentDataObject = parentDataObject;
		new GalleryTileEvent(this);
	}
	
	public void requestEffect() {
		Instances.getReload().requestTileEffect(this);
	}
	public void generateEffect() {
		if (parentDataObject == null) return;
		
		Blend effect = null;
		
		if (Instances.getSelect().contains(parentDataObject)) {
			effect = new Blend(BlendMode.SRC_OVER, effect, effectOverlay);
			effect = new Blend(BlendMode.DARKEN, effect, effectBorder);
		}
		
		if (parentDataObject.getJointID() != 0) {
			effect = new Blend(BlendMode.SRC_OVER, effect, this.createEffectJointObject(parentDataObject));
		}
		
		this.setEffect(effect);
	}
	
	private ImageInput createEffectJointObject(DataObject dataObject) {
		String middle;
		CustomList<DataObject> jointObject = dataObject.getJointObjects();
		if (Instances.getGalleryPane().getExpandedGroups().contains(dataObject.getJointID())) {
			middle = (jointObject.indexOf(dataObject) + 1) + "/" + jointObject.size();
		} else {
			middle = String.valueOf(jointObject.size());
		}
		
		String groupIconText;
		if (Instances.getObjectListMain().getAllGroups().indexOf(dataObject.getJointID()) % 2 == 0) {
			groupIconText = "(" + middle + ")";
		} else {
			groupIconText = "[" + middle + "]";
		}
		int tileSize = Instances.getSettings().getGalleryTileSize();
		Image imageText = textToImage(groupIconText);
		groupEffectWidth = (int) imageText.getWidth() + 5;
		groupEffectHeight = (int) imageText.getHeight();
		return new ImageInput(imageText, tileSize - imageText.getWidth() - 5, 0);
	}
	private static ColorInput createEffectOverlay() {
		double tileSize = SizeUtil.getGalleryTileSize();
		Color opacityFull = Color.DARKBLUE;
		Color opacityLess = new Color(opacityFull.getRed(), opacityFull.getGreen(), opacityFull.getBlue(), 0.3);
		return new ColorInput(0, 0, tileSize, tileSize, opacityLess);
	}
	private static InnerShadow createEffectBorder() {
		Color opacityFull = Color.DARKBLUE;
		Color opacityLess = new Color(opacityFull.getRed(), opacityFull.getGreen(), opacityFull.getBlue(), 0.8);
		
		InnerShadow innerShadow = new InnerShadow();
		innerShadow.setColor(opacityLess);
		innerShadow.setOffsetX(0);
		innerShadow.setOffsetY(0);
		innerShadow.setWidth(5);
		innerShadow.setHeight(5);
		innerShadow.setChoke(1);
		return innerShadow;
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
