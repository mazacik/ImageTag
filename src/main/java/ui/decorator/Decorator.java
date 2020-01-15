package ui.decorator;

import base.CustomList;
import javafx.scene.Node;
import javafx.scene.control.Labeled;
import javafx.scene.control.TextInputControl;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import misc.Settings;

import java.awt.*;

public abstract class Decorator {
	/* Font */
	private static Font font = new Font(Settings.getFontSize());
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
	
	/* Scrollbar Style */
	public static void setScrollbarStyle(Node node) {
		try {
			node.applyCss();
			node.lookup(".track").setStyle("-fx-background-color: transparent;" +
			                               " -fx-border-color: transparent;" +
			                               " -fx-background-radius: 0.0em;" +
			                               " -fx-border-radius: 0.0em;" +
			                               " -fx-padding: 0.0 0.0 0.0 0.0;");
			node.lookup(".scroll-bar").setStyle("-fx-background-color: transparent;" +
			                                    " -fx-pref-width: 15;" +
			                                    " -fx-padding: 3 2 3 3;");
			node.lookup(".increment-button").setStyle("-fx-background-color: transparent;" +
			                                          " -fx-background-radius: 0.0em;" +
			                                          " -fx-padding: 0.0 0.0 0.0 0.0;");
			node.lookup(".decrement-button").setStyle("-fx-background-color: transparent;" +
			                                          " -fx-background-radius: 0.0em;" +
			                                          " -fx-padding: 0.0 0.0 0.0 0.0;");
			node.lookup(".increment-arrow").setStyle("-fx-padding: 0.0em 0.0;");
			node.lookup(".decrement-arrow").setStyle("-fx-padding: 0.0em 0.0;");
			node.lookup(".thumb").setStyle("-fx-background-color: derive(black, 90.0%);" +
			                               " -fx-background-insets: 0.0, 0.0, 0.0;" +
			                               " -fx-background-radius: 0.0em;");
			node.lookup(".viewport").setStyle("-fx-background-color: transparent;");
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
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
	private static CustomList<Region> nodeList = new CustomList<>();
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
	public static CustomList<Region> getNodeList() {
		return nodeList;
	}
	
	/* Color Preset */
	private static ColorPreset colorPreset = ColorPreset.values()[Settings.getColorPreset()];
	public static ColorPreset getColorPreset() {
		return colorPreset;
	}
	public static void setColorPreset(ColorPreset colorPreset) {
		ColorPreset previousPreset = Decorator.colorPreset;
		
		Decorator.colorPreset = colorPreset;
		Settings.setColorPreset(colorPreset.ordinal());
		
		updateNodes(previousPreset);
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
	public static Color getColorUnion() {
		return colorPreset.getColorUnion();
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
