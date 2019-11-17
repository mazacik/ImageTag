package gui.decorator;

import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import main.InstanceCollector;

public enum ColorPreset {
	PRESET_BRIGHT(Color.BLACK, Color.DARKORANGE, Color.GREEN, Color.RED, Color.CORNFLOWERBLUE, Color.GRAY, getBackground("#DDDEEE"), getBackground("#CCCDDD")),
	PRESET_DARK(Color.LIGHTGRAY, Color.ORANGE, Color.LIGHTGREEN, Color.ORANGERED, Color.CORNFLOWERBLUE, Color.GRAY, getBackground("#3C3F41"), getBackground("#313335")),
	;
	
	private Color colorPrimary;
	private Color colorSecondary;
	private Color colorPositive;
	private Color colorNegative;
	private Color colorShare;
	
	private Color colorBorder;
	
	private Background backgroundPrimary;
	private Background backgroundSecondary;
	
	ColorPreset(Color colorPrimary, Color colorSecondary, Color colorPositive, Color colorNegative, Color colorShare, Color colorBorder, Background backgroundPrimary, Background backgroundSecondary) {
		this.colorPrimary = colorPrimary;
		this.colorSecondary = colorSecondary;
		this.colorPositive = colorPositive;
		this.colorNegative = colorNegative;
		this.colorShare = colorShare;
		
		this.colorBorder = colorBorder;
		
		this.backgroundPrimary = backgroundPrimary;
		this.backgroundSecondary = backgroundSecondary;
	}
	
	private static ColorPreset current = ColorPreset.values()[InstanceCollector.settings.getColorPreset()];
	public static ColorPreset getCurrent() {
		return current;
	}
	public static void setCurrent(ColorPreset current) {
		ColorPreset.current = current;
		InstanceCollector.settings.setColorPreset(current.ordinal());
	}
	
	private static Background getBackground(String hex) {
		return getBackground(Paint.valueOf(hex));
	}
	private static Background getBackground(Paint paint) {
		return new Background(new BackgroundFill(paint, null, null));
	}
	
	public Color getColorPrimary() {
		return colorPrimary;
	}
	public Color getColorSecondary() {
		return colorSecondary;
	}
	public Color getColorPositive() {
		return colorPositive;
	}
	public Color getColorNegative() {
		return colorNegative;
	}
	public Color getColorShare() {
		return colorShare;
	}
	
	public Color getColorBorder() {
		return colorBorder;
	}
	
	public Background getBackgroundPrimary() {
		return backgroundPrimary;
	}
	public Background getBackgroundSecondary() {
		return backgroundSecondary;
	}
}
