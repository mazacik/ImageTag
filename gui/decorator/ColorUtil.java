package gui.decorator;

import javafx.scene.layout.Background;
import javafx.scene.paint.Color;
import main.InstanceCollector;

public abstract class ColorUtil implements InstanceCollector {
	private static ColorPreset colorPreset = ColorPreset.values()[settings.getColorPreset()];
	
	public static ColorPreset getColorPreset() {
		return colorPreset;
	}
	public static void setColorPreset(ColorPreset colorPreset) {
		ColorUtil.colorPreset = colorPreset;
		InstanceCollector.settings.setColorPreset(colorPreset.ordinal());
	}
	
	public static Color getColorPrimary() {
		return colorPreset.getColorPrimary();
	}
	public static Color getColorSecondary() {
		return colorPreset.getColorSecondary();
	}
	public static Color getColorPositive() {
		return colorPreset.getColorPositive();
	}
	public static Color getColorNegative() {
		return colorPreset.getColorNegative();
	}
	public static Color getColorShare() {
		return colorPreset.getColorShare();
	}
	
	public static Color getColorBorder() {
		return colorPreset.getColorBorder();
	}
	
	public static Background getBackgroundPrimary() {
		return colorPreset.getBackgroundPrimary();
	}
	public static Background getBackgroundSecondary() {
		return colorPreset.getBackgroundSecondary();
	}
}
