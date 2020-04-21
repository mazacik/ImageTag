package frontend.decorator;

import backend.list.BaseList;
import backend.misc.Settings;
import javafx.scene.control.Labeled;
import javafx.scene.control.TextInputControl;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;

import java.awt.*;

public abstract class DecoratorUtil {
	/* Font */
	private static final Font font = new Font(Settings.FONT_SIZE.getInteger());
	public static Font getFont() {
		return font;
	}
	
	/* Color CSS String */
	public static String getCssString(Color color) {
		return String.format("#%02X%02X%02X",
		                     (int) (color.getRed() * 255),
		                     (int) (color.getGreen() * 255),
		                     (int) (color.getBlue() * 255)
		);
	}
	
	/* Border */
	public static Border getBorder(int border) {
		return getBorder(border, border, border, border);
	}
	public static Border getBorder(int top, int right, int bottom, int left) {
		return new Border(new BorderStroke(getColorBorder(), BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(top, right, bottom, left)));
	}
	
	/* Screen Size */
	public static Rectangle getUsableScreenBounds() {
		return GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
	}
	public static double getUsableScreenWidth() {
		return GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().getWidth();
	}
	public static double getUsableScreenHeight() {
		return GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().getHeight();
	}
	
	/* Node Updater */
	private static final BaseList<Region> nodeList = new BaseList<>();
	private static void updateNodes(DecoratorTemplate previousPreset) {
		for (Region region : nodeList) {
			Background background = region.getBackground();
			if (background != null) {
				if (background.equals(previousPreset.getBackgroundPrimary())) {
					region.setBackground(getBackgroundPrimary());
				} else if (background.equals(previousPreset.getBackgroundSecondary())) {
					region.setBackground(getBackgroundSecondary());
				}
			}
			
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
					} else if (textFill.equals(previousPreset.getColorUnion())) {
						labeled.setTextFill(getColorUnion());
					}
				}
			}
			
			if (region instanceof TextInputControl) {
				region.setStyle("-fx-text-fill: " + getCssString(getColorPrimary()) + ";");
			}
		}
	}
	public static BaseList<Region> getNodeList() {
		return nodeList;
	}
	
	/* Color Preset */
	private static DecoratorTemplate decoratorTemplate = DecoratorTemplate.values()[Settings.COLOR_PRESET.getInteger()];
	public static DecoratorTemplate getDecoratorTemplate() {
		return decoratorTemplate;
	}
	public static void setDecoratorTemplate(DecoratorTemplate decoratorTemplate) {
		DecoratorTemplate previousPreset = DecoratorUtil.decoratorTemplate;
		
		DecoratorUtil.decoratorTemplate = decoratorTemplate;
		Settings.COLOR_PRESET.setValue(String.valueOf(decoratorTemplate.ordinal()));
		
		updateNodes(previousPreset);
	}
	
	public static Color getColorPrimary() {
		return decoratorTemplate.getColorPrimary();
	}
	public static Color getColorSecondary() {
		return decoratorTemplate.getColorSecondary();
	}
	public static Color getColorPositive() {
		return decoratorTemplate.getColorPositive();
	}
	public static Color getColorNegative() {
		return decoratorTemplate.getColorNegative();
	}
	public static Color getColorUnion() {
		return decoratorTemplate.getColorUnion();
	}
	public static Color getColorBorder() {
		return decoratorTemplate.getColorBorder();
	}
	
	public static Background getBackgroundPrimary() {
		return decoratorTemplate.getBackgroundPrimary();
	}
	public static Background getBackgroundSecondary() {
		return decoratorTemplate.getBackgroundSecondary();
	}
}
