package control;

import baseobject.CustomList;
import com.google.gson.reflect.TypeToken;
import tools.JsonUtil;

import java.io.Serializable;
import java.lang.reflect.Type;

public class Settings implements Serializable {
	private transient static final Type typeToken = new TypeToken<Settings>() {}.getType();
	
	private Integer tileSize;
	private Integer colorPreset;
	private Integer fontSize;
	
	private CustomList<String> recentProjects;
	
	public Settings() {
	
	}
	
	public void readFromDisk() {
		try {
			Settings settings = (Settings) JsonUtil.read(typeToken, "settings.json");
			this.tileSize = settings.tileSize;
			this.colorPreset = settings.colorPreset;
			this.fontSize = settings.fontSize;
			this.recentProjects = settings.recentProjects;
		} catch (Exception e) {
			this.tileSize = 200;
			this.colorPreset = 0;
			this.fontSize = 16;
			this.recentProjects = new CustomList<>();
		}
	}
	public void writeToDisk() {
		JsonUtil.write(this, typeToken, "settings.json");
	}
	
	public int getTileSize() {
		return tileSize;
	}
	public int getColorPreset() {
		return colorPreset;
	}
	public int getFontSize() {
		return fontSize;
	}
	public CustomList<String> getRecentProjects() {
		return recentProjects;
	}
	
	public void setTileSize(int tileSize) {
		this.tileSize = tileSize;
	}
	public void setColorPreset(int colorPreset) {
		this.colorPreset = colorPreset;
	}
	public void setFontSize(int fontSize) {
		this.fontSize = fontSize;
	}
}
