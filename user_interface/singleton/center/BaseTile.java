package user_interface.singleton.center;

import database.object.DataObject;
import javafx.scene.effect.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import settings.SettingsEnum;
import system.CommonUtil;
import system.Instances;

import java.util.ArrayList;

public class BaseTile extends ImageView implements Instances {

    private static final InnerShadow effectSelect = createEffectSelect();
    private static final ColorInput effectTarget = createEffectTarget();
    private static int effectGroupSize;

    private final DataObject parentDataObject;

    public BaseTile(DataObject parentDataObject, Image image) {
        super(image);
        this.parentDataObject = parentDataObject;
        new BaseTileEvent(this);
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
        int markPositionInTile = (settings.intValueOf(SettingsEnum.THUMBSIZE) - markSize) / 2;
        Color markColor = Color.RED;
        return new ColorInput(markPositionInTile, markPositionInTile, markSize, markSize, markColor);
    }
    public static int getEffectGroupSize() {
        return effectGroupSize;
    }
    public void generateEffect() {
        if (parentDataObject == null) return;

        ArrayList<Effect> effectList = new ArrayList<>();

        if (select.contains(parentDataObject)) {
            effectList.add(effectSelect);
        }
        if (target.getCurrentTarget() != null && target.getCurrentTarget().equals(parentDataObject)) {
            effectList.add(effectTarget);
        }
        if (parentDataObject.getMergeID() != 0) {
            String middle;
            if (tileView.getExpandedGroups().contains(parentDataObject.getMergeID())) {
                middle = "-";
            } else {
                middle = String.valueOf(parentDataObject.getMergeGroup().size());
            }

            String groupIconText;
            if (mainDataList.getAllGroups().indexOf(parentDataObject.getMergeID()) % 2 == 0) {
                groupIconText = "(" + middle + ")";
            } else {
                groupIconText = "[" + middle + "]";
            }
            int tileSize = settings.intValueOf(SettingsEnum.THUMBSIZE);
            Image imageText = CommonUtil.textToImage(groupIconText);
            effectList.add(new ImageInput(imageText, tileSize - imageText.getWidth() - 5, 1));
            effectGroupSize = (int) imageText.getWidth() + 10;
        }

        Blend helperEffect = new Blend();
        for (Effect effect : effectList) {
            helperEffect = new Blend(BlendMode.SRC_ATOP, helperEffect, effect);
        }

        this.setEffect(helperEffect);
    }
    public DataObject getParentDataObject() {
        return parentDataObject;
    }
}
