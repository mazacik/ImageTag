package user_interface.style;

import javafx.scene.layout.Background;
import javafx.scene.paint.Color;
import main.InstanceManager;
import settings.SettingsEnum;
import user_interface.nodes.ColorData;
import user_interface.style.enums.BackgroundEnum;
import user_interface.style.enums.ColorEnum;
import user_interface.style.enums.ColorType;

public abstract class ColorUtil {
    private static int nightMode = InstanceManager.getSettings().intValueOf(SettingsEnum.COLORMODE);
    public static boolean isNightMode() {
        return nightMode == 1;
    }
    public static void setNightMode(boolean value) {
        if (!value) {
            nightMode = 0;
        } else {
            nightMode = 1;
        }
        InstanceManager.getSettings().setValueOf(SettingsEnum.COLORMODE, nightMode);
        StyleUtil.applyStyle();
    }

    public static Color getTextColorDef() {
        if (!isNightMode()) {
            return ColorEnum.TEXTDAYDEF.getValue();
        } else {
            return ColorEnum.TEXTNIGHTDEF.getValue();
        }
    }
    public static Color getTextColorAlt() {
        if (!isNightMode()) {
            return ColorEnum.TEXTDAYALT.getValue();
        } else {
            return ColorEnum.TEXTNIGHTALT.getValue();
        }
    }
    public static Color getTextColorPos() {
        if (!isNightMode()) {
            return ColorEnum.TEXTDAYPOS.getValue();
        } else {
            return ColorEnum.TEXTNIGHTPOS.getValue();
        }
    }
    public static Color getTextColorNeg() {
        if (!isNightMode()) {
            return ColorEnum.TEXTDAYNEG.getValue();
        } else {
            return ColorEnum.TEXTNIGHTNEG.getValue();
        }
    }
    public static Color getTextColorShr() {
        if (!isNightMode()) {
            return ColorEnum.TEXTDAYSHR.getValue();
        } else {
            return ColorEnum.TEXTNIGHTSHR.getValue();
        }
    }

    public static Background getBackgroundDef() {
        if (!isNightMode()) {
            return BackgroundEnum.DAYDEF.getValue();
        } else {
            return BackgroundEnum.NIGHTDEF.getValue();
        }
    }
    public static Background getBackgroundAlt() {
        if (!isNightMode()) {
            return BackgroundEnum.DAYALT.getValue();
        } else {
            return BackgroundEnum.NIGHTALT.getValue();
        }
    }

    public static Color getBorderColor() {
        if (!isNightMode()) {
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
