package application.backend.control;

import application.backend.base.CustomList;
import application.backend.util.JsonUtil;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.lang.reflect.Type;

public class Settings implements Serializable {
	private transient static final Type typeToken = new TypeToken<Settings>() {}.getType();
	
	private Integer galleryTileSize;
	private Integer guiColorStyle;
	private Integer fontSize;
	
	private CustomList<String> recentProjects;
	
	public Settings() {
	
	}
	
	public void readFromDisk() {
		try {
			Settings settings = (Settings) JsonUtil.read(typeToken, "settings.json");
			this.galleryTileSize = settings.galleryTileSize;
			this.guiColorStyle = settings.guiColorStyle;
			this.fontSize = settings.fontSize;
			this.recentProjects = settings.recentProjects;
		} catch (Exception e) {
			this.galleryTileSize = 200;
			this.guiColorStyle = 0;
			this.fontSize = 16;
			this.recentProjects = new CustomList<>();
		}
	}
	public void writeToDisk() {
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
