package user_interface.single_instance.center;

import database.object.DataObject;
import javafx.scene.effect.Blend;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.ColorInput;
import javafx.scene.effect.InnerShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import settings.SettingsNamespace;
import system.InstanceRepo;

public class BaseTile extends ImageView implements InstanceRepo {
    private static final InnerShadow effectSelect = createEffectSelect();
    private static final ColorInput effectTarget = createEffectTarget();
    private final DataObject parentDataObject;
    public DataObject getParentDataObject() {
        return parentDataObject;
    }

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
        int markPositionInTile = (userSettings.valueOf(SettingsNamespace.TILEVIEW_ICONSIZE) - markSize) / 2;
        Color markColor = Color.RED;
        return new ColorInput(markPositionInTile, markPositionInTile, markSize, markSize, markColor);
    }
    public void generateEffect() {
        boolean bSelect = false;
        if (parentDataObject != null) {
            bSelect = select.contains(parentDataObject);
        }

        DataObject currentTarget = target.getCurrentTarget();
        boolean bTarget = false;
        if (currentTarget != null) {
            bTarget = currentTarget.equals(parentDataObject);
        }

        if (!bSelect && !bTarget) {
            this.setEffect(null);
        } else if (!bSelect && bTarget) {
            Blend blend = new Blend();
            blend.setTopInput(effectTarget);
            this.setEffect(blend);
        } else if (bSelect && !bTarget) {
            this.setEffect(effectSelect);
        } else if (bSelect && bTarget) {
            Blend effect = new Blend();
            effect.setTopInput(effectSelect);
            effect.setBottomInput(effectTarget);
            effect.setMode(BlendMode.OVERLAY);
            this.setEffect(effect);
        }
    }
}
