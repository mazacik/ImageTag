package application.settings;

import application.misc.JsonUtil;

import java.io.Serializable;
import java.lang.reflect.Type;

public class Settings implements Serializable {
	private Integer galleryTileSize;
	private Integer guiColorStyle;
	private Integer fontSize;
	
	private Stack<String> recentProjects;
	
	public Settings() {
	
	}
	
	public void writeToDisk() {
		Type typeToken = JsonUtil.TypeTokenEnum.SETTINGS.getValue();
		JsonUtil.write(this, typeToken, "settings.json");
	}
	public static Settings readFromDisk() {
		Type typeToken = JsonUtil.TypeTokenEnum.SETTINGS.getValue();
		return (Settings) JsonUtil.read(typeToken, "settings.json");
	}
	
	public int getGalleryTileSize() {
		if (galleryTileSize == null) galleryTileSize = 200;
		return galleryTileSize;
	}
	public int getGuiColorStyle() {
		if (guiColorStyle == null) guiColorStyle = 0;
		return guiColorStyle;
	}
	public int getFontSize() {
		if (fontSize == null) fontSize = 16;
		return fontSize;
	}
	public Stack<String> getRecentProjects() {
		if (recentProjects == null) recentProjects = new Stack<>(10);
		return recentProjects;
	}
	
	public void setGalleryTileSize(int galleryTileSize) {
		this.galleryTileSize = galleryTileSize;
	}
	public void setGuiColorStyle(int guiColorStyle) {
		this.guiColorStyle = guiColorStyle;
	}
	public void setFontSize(int fontSize) {
		this.fontSize = fontSize;
	}
}
