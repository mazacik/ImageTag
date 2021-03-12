package backend.settings;

import backend.misc.FileUtil;
import backend.misc.GsonUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.ArrayList;
import java.util.List;

@JsonSerialize(using = SettingsSerializer.class)
@JsonDeserialize(using = SettingsDeserializer.class)
public enum Settings {
	
	COLOR_PRESET(0),
	FONT_SIZE(16),
	GALLERY_TILE_SIZE(200),
	COLLAGE_SIZE(100),
	IMAGE_UPSCALE(true),
	IMPORT_LAST_PATH(System.getProperty("user.dir")),
	
	ENABLE_IMG(true),
	ENABLE_IMG_JPG(true),
	ENABLE_IMG_JPEG(true),
	ENABLE_IMG_PNG(true),
	
	ENABLE_VID(false),
	ENABLE_VID_MP4(true),
	ENABLE_VID_M4V(true),
	ENABLE_VID_MOV(true),
	ENABLE_VID_WMV(true),
	ENABLE_VID_AVI(true),
	ENABLE_VID_WEBM(true),
	ENABLE_VID_GIF(true),
	
	MIN_TAG_COUNT(0, 0, Integer.MAX_VALUE),
	MAX_TAG_COUNT(Integer.MAX_VALUE, 0, Integer.MAX_VALUE),
	
	MIN_GROUP_SIZE(0, 0, Integer.MAX_VALUE),
	MAX_GROUP_SIZE(Integer.MAX_VALUE, 0, Integer.MAX_VALUE),
	
	MIN_MEDIA_LENGTH(0, 0, Integer.MAX_VALUE),
	MAX_MEDIA_LENGTH(Integer.MAX_VALUE, 0, Integer.MAX_VALUE),
	
	MIN_LIKES(0, 0, Integer.MAX_VALUE),
	MAX_LIKES(Integer.MAX_VALUE, 0, Integer.MAX_VALUE),
	
	ONLY_FAVORITES(false),
	ONLY_LAST_IMPORT(false),
	SIMILARITY_FACTOR(50, 0, 100);
	
	private static ArrayList<Settings> list;
	
	private Object value;
	private transient Object defaultValue;
	private transient Object minValue;
	private transient Object maxValue;
	
	Settings(Object defaultValue) {
		this(defaultValue, null, null);
	}
	Settings(Object defaultValue, Object minValue, Object maxValue) {
		value = defaultValue;
		this.defaultValue = defaultValue;
		this.minValue = minValue;
		this.maxValue = maxValue;
	}
	
	public static void read() {
		list = (ArrayList<Settings>) GsonUtil.readList(new TypeReference<List<Settings>>() {}, FileUtil.getFileSettings());
		if (list == null) list = new ArrayList<>();
		for (Settings setting : values()) {
			if (list.contains(setting)) {
				if (setting.value == null) setting.value = setting.defaultValue;
			} else {
				list.add(setting);
			}
		}
		write();
	}
	
	public static void write() {
		GsonUtil.write(list, FileUtil.getFileSettings());
	}
	
	public static void reset() {
	
	}
	
	public Object getValue() {
		if (value == null) value = defaultValue;
		return value;
	}
	
	public void setValue(Object value) {
		this.value = value;
	}
	
	public String getStringValue() {
		if (value == null) value = defaultValue;
		return String.valueOf(value);
	}
	
	public int getIntValue() {
		if (value == null) value = defaultValue;
		return (int) value;
	}
	
	public boolean getBooleanValue() {
		if (value == null) value = defaultValue;
		return (boolean) value;
	}
	
	public Object getDefaultValue() {
		return defaultValue;
	}
	
	public Object getMinValue() {
		return minValue;
	}
	
	public Object getMaxValue() {
		return maxValue;
	}
	
	public Object resetValue() {
		value = defaultValue;
		return value;
	}
	
}
