package user_interface.main.center;

import database.object.DataObject;
import javafx.scene.effect.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import main.InstanceManager;
import settings.SettingsEnum;
import user_interface.scene.SceneUtil;

import java.util.ArrayList;

public class BaseTile extends ImageView {
	private static final InnerShadow effectSelect = createEffectSelect();
	private static final ColorInput effectTarget = createEffectTarget();
	private static int effectGroupSize;
	
	private final DataObject parentObject;
	
	public BaseTile(DataObject parentObject, Image image) {
		super(image);
		this.parentObject = parentObject;
		new BaseTileEvent(this);
	}
	
	public void generateEffect() {
		if (parentObject == null) return;
		
		ArrayList<Effect> effectList = new ArrayList<>();
		
		if (InstanceManager.getSelect().contains(parentObject)) {
			effectList.add(effectSelect);
		}
		if (InstanceManager.getTarget().getCurrentTarget() != null && InstanceManager.getTarget().getCurrentTarget().equals(parentObject)) {
			effectList.add(effectTarget);
		}
		if (parentObject.getMergeID() != 0) {
			String middle;
			if (InstanceManager.getGalleryPane().getExpandedGroups().contains(parentObject.getMergeID())) {
				middle = "-";
			} else {
				middle = String.valueOf(parentObject.getMergeGroup().size());
			}
			
			String groupIconText;
			if (InstanceManager.getObjectListMain().getAllGroups().indexOf(parentObject.getMergeID()) % 2 == 0) {
				groupIconText = "(" + middle + ")";
			} else {
				groupIconText = "[" + middle + "]";
			}
			int tileSize = InstanceManager.getSettings().intValueOf(SettingsEnum.THUMBSIZE);
			Image imageText = SceneUtil.textToImage(groupIconText);
			effectList.add(new ImageInput(imageText, tileSize - imageText.getWidth() - 5, 1));
			effectGroupSize = (int) imageText.getWidth() + 10;
		}
		
		Blend helperEffect = new Blend();
		for (Effect effect : effectList) {
			helperEffect = new Blend(BlendMode.SRC_ATOP, helperEffect, effect);
		}
		
		this.setEffect(helperEffect);
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
		int markPositionInTile = (InstanceManager.getSettings().intValueOf(SettingsEnum.THUMBSIZE) - markSize) / 2;
		return new ColorInput(markPositionInTile, markPositionInTile, markSize, markSize, Color.RED);
	}
	
	public static int getEffectGroupSize() {
		return effectGroupSize;
	}
	public DataObject getParentObject() {
		return parentObject;
	}
}
