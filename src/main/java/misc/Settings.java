package misc;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public class Settings {
	private static Integer COLOR_PRESET_INDEX;
	private static Integer FONT_SIZE;
	private static Integer GALLERY_TILE_SIZE;
	
	private transient static final Type typeToken;
	static {
		typeToken = new TypeToken<Settings>() {}.getType();
		
		try {
			Settings settings = (Settings) JsonUtil.read(typeToken, FileUtil.getFileSettings());
			COLOR_PRESET_INDEX = settings.colorPreset;
			FONT_SIZE = settings.fontSize;
			GALLERY_TILE_SIZE = settings.tileSize;
		} catch (Exception e) {
			COLOR_PRESET_INDEX = 0;
			FONT_SIZE = 16;
			GALLERY_TILE_SIZE = 200;
		}
	}
	
	private Integer colorPreset;
	private Integer fontSize;
	private Integer tileSize;
	
	private Settings() {
	
	}
	
	public static void writeToDisk() {
		Settings settings = new Settings();
		settings.colorPreset = COLOR_PRESET_INDEX;
		settings.fontSize = FONT_SIZE;
		settings.tileSize = GALLERY_TILE_SIZE;
		JsonUtil.write(settings, typeToken, FileUtil.getFileSettings());
	}
	
	public static int getColorPreset() {
		return COLOR_PRESET_INDEX;
	}
	public static int getFontSize() {
		return FONT_SIZE;
	}
	public static int getTileSize() {
		return GALLERY_TILE_SIZE;
	}
	
	public static void setColorPreset(int colorPreset) {
		Settings.COLOR_PRESET_INDEX = colorPreset;
	}
	public static void setFontSize(int fontSize) {
		Settings.FONT_SIZE = fontSize;
	}
	public static void setTileSize(int tileSize) {
		Settings.GALLERY_TILE_SIZE = tileSize;
	}
}
