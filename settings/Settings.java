package application.settings;

import application.data.list.CustomList;
import application.misc.JsonUtil;

import java.io.Serializable;
import java.lang.reflect.Type;

public class Settings implements Serializable {
	private Integer galleryTileSize;
	private Integer guiColorStyle;
	private Integer fontSize;
	
	private CustomList<String> recentProjects;
	
	public Settings() {
	
	}
	
	public static Settings readFromDisk() {
		Type typeToken = JsonUtil.TypeTokenEnum.SETTINGS.getValue();
		Settings settings;
		try {
			settings = (Settings) JsonUtil.read(typeToken, "settings.json");
		} catch (Exception e) {
			settings = new Settings();
			settings.galleryTileSize = 200;
			settings.guiColorStyle = 0;
			settings.fontSize = 16;
			settings.recentProjects = new CustomList<>();
		}
		return settings;
	}
	public void writeToDisk() {
		Type typeToken = JsonUtil.TypeTokenEnum.SETTINGS.getValue();
		JsonUtil.write(this, typeToken, "settings.json");
	}
	
	public int getGalleryTileSize() {
		return galleryTileSize;
	}
	public int getGuiColorStyle() {
		return guiColorStyle;
	}
	public int getFontSize() {
		return fontSize;
	}
	public CustomList<String> getRecentProjects() {
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
