package misc;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public class Settings {
	private Integer colorPreset;
	private static final Integer colorPresetDefault = 0;
	public static int getColorPreset() {
		return getInstance().colorPreset;
	}
	public static void setColorPreset(int colorPreset) {
		getInstance().colorPreset = colorPreset;
	}
	
	private Integer fontSize;
	private static final Integer fontSizeDefault = 16;
	public static int getFontSize() {
		return getInstance().fontSize;
	}
	public static void setFontSize(int fontSize) {
		getInstance().fontSize = fontSize;
	}
	
	private Integer tileSize;
	private static final Integer tileSizeDefault = 200;
	public static int getTileSize() {
		return getInstance().tileSize;
	}
	public static void setTileSize(int tileSize) {
		getInstance().tileSize = tileSize;
	}
	
	private Integer collageSize;
	private static final Integer collageSizeDefault = 25;
	public static int getCollageSize() {
		return getInstance().collageSize;
	}
	public static void setCollageSize(Integer collageSize) {
		getInstance().collageSize = collageSize;
	}
	
	private transient static final Type typeToken = new TypeToken<Settings>() {}.getType();
	public static void writeToDisk() {
		JsonUtil.write(getInstance(), typeToken, FileUtil.getFileSettings());
	}
	private static void readFromDisk() {
		Settings settings;
		try {
			settings = (Settings) JsonUtil.read(typeToken, FileUtil.getFileSettings());
		} catch (Exception e) {
			settings = new Settings();
		}
		
		if (settings.colorPreset == null) settings.colorPreset = colorPresetDefault;
		if (settings.fontSize == null) settings.fontSize = fontSizeDefault;
		if (settings.tileSize == null) settings.tileSize = tileSizeDefault;
		if (settings.collageSize == null) settings.collageSize = collageSizeDefault;
		
		getInstance().colorPreset = settings.colorPreset;
		getInstance().fontSize = settings.fontSize;
		getInstance().tileSize = settings.tileSize;
		getInstance().collageSize = settings.collageSize;
	}
	
	static {
		readFromDisk();
	}
	
	private Settings() {}
	private static class Loader {
		private static final Settings INSTANCE = new Settings();
	}
	public static Settings getInstance() {
		return Loader.INSTANCE;
	}
}
