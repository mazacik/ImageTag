package gui.singleton.center;

import database.object.DataObject;
import gui.event.center.BaseTileEvent;
import javafx.scene.effect.Blend;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.ColorInput;
import javafx.scene.effect.InnerShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import settings.SettingsEnum;
import utils.MainUtil;

public class BaseTile extends ImageView implements MainUtil {
    private static final InnerShadow effectSelect = buildSelectEffect();
    private static final int tileViewIconSize = settings.getValueOf(SettingsEnum.TILEVIEW_ICONSIZE);
    private static final ColorInput effectTarget = buildTargetEffect();

    private final DataObject parentDataObject;

    public BaseTile(DataObject dataObject, Image thumbnail) {
        super(thumbnail);
        parentDataObject = dataObject;
        setFitWidth(tileViewIconSize);
        setFitHeight(tileViewIconSize);
        new BaseTileEvent(this);
    }
    private static InnerShadow buildSelectEffect() {
        InnerShadow innerShadow = new InnerShadow();
        innerShadow.setColor(Color.RED);
        innerShadow.setOffsetX(0);
        innerShadow.setOffsetY(0);
        innerShadow.setWidth(5);
        innerShadow.setHeight(5);
        innerShadow.setChoke(1);
        return innerShadow;
    }
    private static ColorInput buildTargetEffect() {
        int markSize = 6;
        int markPositionInTile = (tileViewIconSize - markSize) / 2;
        Color markColor = Color.RED;
        return new ColorInput(markPositionInTile, markPositionInTile, markSize, markSize, markColor);
    }
    public void generateEffect() {
        boolean booleanSelection = false;
        if (parentDataObject != null) {
            booleanSelection = select.contains(parentDataObject);
        }

        DataObject currentFocus = target.getCurrentFocus();
        boolean booleanFocus = false;
        if (currentFocus != null) {
            booleanFocus = currentFocus.equals(parentDataObject);
        }

        if (!booleanSelection && !booleanFocus) {
            setEffect(null);
        } else if (!booleanSelection && booleanFocus) {
            Blend blend = new Blend();
            blend.setTopInput(effectTarget);
            setEffect(blend);
        } else if (booleanSelection && !booleanFocus) {
            setEffect(effectSelect);
        } else if (booleanSelection && booleanFocus) {
            Blend effect = new Blend();
            effect.setTopInput(effectSelect);
            effect.setBottomInput(effectTarget);
            effect.setMode(BlendMode.OVERLAY);
            setEffect(effect);
        }
    }
    public DataObject getParentDataObject() {
        return parentDataObject;
    }
}
