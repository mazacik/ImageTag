package application.gui.decorator;

import application.gui.decorator.enums.BackgroundEnum;
import application.gui.decorator.enums.ColorEnum;
import application.main.InstanceCollector;
import javafx.scene.layout.Background;
import javafx.scene.paint.Color;

public abstract class ColorUtil implements InstanceCollector {
	private static int nightMode = settings.getGuiColorStyle();
	public static boolean isNightMode() {
		return nightMode == 1;
	}
	public static void setNightMode(boolean value) {
		if (!value) {
			nightMode = 0;
		} else {
			nightMode = 1;
		}
		settings.setGuiColorStyle(nightMode);
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
}
