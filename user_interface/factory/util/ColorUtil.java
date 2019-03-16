package user_interface.factory.util;

import javafx.scene.layout.Background;
import javafx.scene.paint.Color;
import system.CommonUtil;
import user_interface.factory.util.enums.BackgroundEnum;
import user_interface.factory.util.enums.ColorEnum;

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
    public static Color getTextColorInt() {
        if (!CommonUtil.isNightMode()) {
            return ColorEnum.TEXTDAYINT.getValue();
        } else {
            return ColorEnum.TEXTNIGHTINT.getValue();
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
}
