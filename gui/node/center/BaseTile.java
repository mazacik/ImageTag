package gui.node.center;

import database.object.DataObject;
import gui.event.center.BaseTileEvent;
import javafx.scene.effect.Blend;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.ColorInput;
import javafx.scene.effect.InnerShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import settings.SettingsNamespace;
import utils.MainUtil;

public class BaseTile extends ImageView implements MainUtil {
    private static final InnerShadow effectSelect = createEffectSelect();

    private final DataObject parentDataObject;

    public BaseTile(DataObject dataObject, Image thumbnail) {
        super(thumbnail);
        parentDataObject = dataObject;
        setFitWidth(tileViewIconSize);
        setFitHeight(tileViewIconSize);
        new BaseTileEvent(this);
    }
    private static int tileViewIconSize = settings.valueOf(SettingsNamespace.TILEVIEW_ICONSIZE);
    private static final ColorInput effectTarget = createEffectTarget();
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
        int markPositionInTile = (tileViewIconSize - markSize) / 2;
        Color markColor = Color.RED;
        return new ColorInput(markPositionInTile, markPositionInTile, markSize, markSize, markColor);
    }
    public void generateEffect() {
        boolean booleanSelect = false;
        if (parentDataObject != null) {
            booleanSelect = select.contains(parentDataObject);
        }

        DataObject currentFocus = target.getCurrentFocus();
        boolean booleanTarget = false;
        if (currentFocus != null) {
            booleanTarget = currentFocus.equals(parentDataObject);
        }

        if (!booleanSelect && !booleanTarget) {
            this.setEffect(null);
        } else if (!booleanSelect && booleanTarget) {
            Blend blend = new Blend();
            blend.setTopInput(effectTarget);
            this.setEffect(blend);
        } else if (booleanSelect && !booleanTarget) {
            this.setEffect(effectSelect);
        } else if (booleanSelect && booleanTarget) {
            Blend effect = new Blend();
            effect.setTopInput(effectSelect);
            effect.setBottomInput(effectTarget);
            effect.setMode(BlendMode.OVERLAY);
            this.setEffect(effect);
        }
    }

    public DataObject getParentDataObject() {
        return parentDataObject;
    }
}
