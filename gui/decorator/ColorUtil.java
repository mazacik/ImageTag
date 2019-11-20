package gui.decorator;

import baseobject.CustomList;
import javafx.scene.control.Labeled;
import javafx.scene.control.TextInputControl;
import javafx.scene.layout.Background;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import main.InstanceCollector;

public abstract class ColorUtil implements InstanceCollector {
	private static CustomList<Region> nodeList = new CustomList<>();
	public static CustomList<Region> getNodeList() {
		return nodeList;
	}
	
	private static ColorPreset colorPreset = ColorPreset.values()[settings.getColorPreset()];
	public static ColorPreset getColorPreset() {
		return colorPreset;
	}
	public static void setColorPreset(ColorPreset colorPreset) {
		ColorPreset previousPreset = ColorUtil.colorPreset;
		
		ColorUtil.colorPreset = colorPreset;
		InstanceCollector.settings.setColorPreset(colorPreset.ordinal());
		
		updateNodes(previousPreset);
	}
	private static void updateNodes(ColorPreset previousPreset) {
		for (Region region : nodeList) {
			Background background = region.getBackground();
			if (background != null) {
				if (background.equals(previousPreset.getBackgroundPrimary())) {
					region.setBackground(getBackgroundPrimary());
				} else if (background.equals(previousPreset.getBackgroundSecondary())) {
					region.setBackground(getBackgroundSecondary());
				}
			}
			
			/*Border border = region.getBorder();
			if (border != null) {
				Paint borderPaint;
				BorderStroke borderStroke = border.getStrokes().get(0);
				if (borderStroke.getTopStroke() != null) {
					borderPaint = borderStroke.getTopStroke();
				} else if (borderStroke.getRightStroke() != null) {
					borderPaint = borderStroke.getRightStroke();
				} else if (borderStroke.getBottomStroke() != null) {
					borderPaint = borderStroke.getBottomStroke();
				} else if (borderStroke.getLeftStroke() != null) {
					borderPaint = borderStroke.getLeftStroke();
				}
			}*/
			
			if (region instanceof Labeled) {
				Labeled labeled = (Labeled) region;
				Paint textFill = labeled.getTextFill();
				if (textFill != null) {
					if (textFill.equals(previousPreset.getColorPrimary())) {
						labeled.setTextFill(getColorPrimary());
					} else if (textFill.equals(previousPreset.getColorSecondary())) {
						labeled.setTextFill(getColorSecondary());
					} else if (textFill.equals(previousPreset.getColorPositive())) {
						labeled.setTextFill(getColorPositive());
					} else if (textFill.equals(previousPreset.getColorNegative())) {
						labeled.setTextFill(getColorNegative());
					} else if (textFill.equals(previousPreset.getColorShare())) {
						labeled.setTextFill(getColorShare());
					}
				}
			}
			
			if (region instanceof TextInputControl) {
				region.setStyle("-fx-text-fill: " + Decorator.getColorAsStringForCss(ColorUtil.getColorPrimary()) + ";");
			}
		}
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
