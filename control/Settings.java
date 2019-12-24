package control;

import com.google.gson.reflect.TypeToken;
import tools.FileUtil;
import tools.JsonUtil;

import java.io.Serializable;
import java.lang.reflect.Type;

public class Settings implements Serializable {
	private Integer tileSize;
	private Integer colorPreset;
	private Integer fontSize;
	
	public Settings() {
	
	}
	
	private transient static final Type typeToken = new TypeToken<Settings>() {}.getType();
	public void readFromDisk() {
		try {
			Settings settings = (Settings) JsonUtil.read(typeToken, FileUtil.getFileSettings());
			this.tileSize = settings.tileSize;
			this.colorPreset = settings.colorPreset;
			this.fontSize = settings.fontSize;
		} catch (Exception e) {
			this.tileSize = 200;
			this.colorPreset = 0;
			this.fontSize = 16;
		}
	}
	public void writeToDisk() {
		JsonUtil.write(this, typeToken, FileUtil.getFileSettings());
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
