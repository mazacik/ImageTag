package user_interface.utils;

import javafx.scene.layout.Background;
import javafx.scene.paint.Color;
import user_interface.factory.ColorData;
import user_interface.utils.enums.BackgroundEnum;
import user_interface.utils.enums.ColorEnum;
import user_interface.utils.enums.ColorType;
import utils.CommonUtil;

public class ColorUtil {
    public static Color getTextColorDef() {
        if (!CommonUtil.isNightMode()) {
            return ColorEnum.TEXTDAYDEF.getValue();
        } else {
            return ColorEnum.TEXTNIGHTDEF.getValue();
        }
    }
    public static Color getTextColorAlt() {
        if (!CommonUtil.isNightMode()) {
            return ColorEnum.TEXTDAYALT.getValue();
        } else {
            return ColorEnum.TEXTNIGHTALT.getValue();
        }
    }
    public static Color getTextColorPos() {
        if (!CommonUtil.isNightMode()) {
            return ColorEnum.TEXTDAYPOS.getValue();
        } else {
            return ColorEnum.TEXTNIGHTPOS.getValue();
        }
    }
    public static Color getTextColorNeg() {
        if (!CommonUtil.isNightMode()) {
            return ColorEnum.TEXTDAYNEG.getValue();
        } else {
            return ColorEnum.TEXTNIGHTNEG.getValue();
        }
    }
    public static Color getTextColorShr() {
        if (!CommonUtil.isNightMode()) {
            return ColorEnum.TEXTDAYSHR.getValue();
        } else {
            return ColorEnum.TEXTNIGHTSHR.getValue();
        }
    }

    public static Background getBackgroundDef() {
        if (!CommonUtil.isNightMode()) {
            return BackgroundEnum.DAYDEF.getValue();
        } else {
            return BackgroundEnum.NIGHTDEF.getValue();
        }
    }
    public static Background getBackgroundAlt() {
        if (!CommonUtil.isNightMode()) {
            return BackgroundEnum.DAYALT.getValue();
        } else {
            return BackgroundEnum.NIGHTALT.getValue();
        }
    }

    public static Color getBorderColor() {
        if (!CommonUtil.isNightMode()) {
            return ColorEnum.BORDERDAY.getValue();
        } else {
            return ColorEnum.BORDERNIGHT.getValue();
        }
    }

    public static Background getBackgroundDef(ColorData colorData) {
        if (colorData.getBackgroundDef() == ColorType.DEF) {
            return getBackgroundDef();
        } else if (colorData.getBackgroundDef() == ColorType.ALT) {
            return getBackgroundAlt();
        } else {
            return null;
        }
    }
    public static Background getBackgroundAlt(ColorData colorData) {
        if (colorData.getBackgroundAlt() == ColorType.DEF) {
            return getBackgroundDef();
        } else if (colorData.getBackgroundAlt() == ColorType.ALT) {
            return getBackgroundAlt();
        } else {
            return null;
        }
    }
    public static Color getTextColorDef(ColorData colorData) {
        if (colorData.getTextFillDef() == ColorType.DEF) {
            return getTextColorDef();
        } else if (colorData.getTextFillDef() == ColorType.ALT) {
            return getTextColorAlt();
        } else {
            return null;
        }
    }
    public static Color getTextColorAlt(ColorData colorData) {
        if (colorData.getTextFillAlt() == ColorType.DEF) {
            return getTextColorDef();
        } else if (colorData.getTextFillAlt() == ColorType.ALT) {
            return getTextColorAlt();
        } else {
            return null;
        }
    }
}
