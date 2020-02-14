package misc;

import base.CustomList;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public enum Settings {
	COLOR_PRESET(0),
	FONT_SIZE(16),
	TILE_SIZE(200),
	COLLAGE_SIZE(50),
	;
	
	private static final int ERROR_VALUE = -1;
	private transient int defaultValue;
	private int value = ERROR_VALUE;
	
	static {
		readFromDisk();
	}
	public static void readFromDisk() {
		Type type = new TypeToken<CustomList<SettingsGsonHelper>>() {}.getType();
		CustomList<SettingsGsonHelper> list = GsonUtil.read(type, FileUtil.getFileSettings());
		
		if (list != null) {
			for (SettingsGsonHelper settingHelper : list) {
				if (settingHelper != null) {
					Settings.valueOf(settingHelper.name).setValue(settingHelper.value);
				}
			}
		}
		
		for (Settings setting : Settings.values()) {
			if (setting.getValue() == ERROR_VALUE) {
				setting.setDefaultValue();
			}
		}
	}
	public static void writeToDisk() {
		CustomList<SettingsGsonHelper> list = new CustomList<>();
		for (Settings setting : Settings.values()) {
			list.add(new SettingsGsonHelper(setting.name(), setting.getValue()));
		}
		GsonUtil.write(list, new TypeToken<CustomList<SettingsGsonHelper>>() {}.getType(), FileUtil.getFileSettings());
	}
	private static class SettingsGsonHelper {
		private String name;
		private Integer value;
		
		public SettingsGsonHelper(String name, Integer value) {
			this.name = name;
			this.value = value;
		}
	}
	
	Settings(int defaultValue) {
		this.defaultValue = defaultValue;
	}
	
	public int getValue() {
		return value;
	}
	public int getDefaultValue() {
		return defaultValue;
	}
	
	public void setValue(int value) {
		this.value = value;
	}
	public void setDefaultValue() {
		this.value = this.defaultValue;
	}
}
