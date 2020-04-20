package server.control.filter;

public enum FilterOption {
	ENABLE_IMG(1, 0, 1),
	ENABLE_IMG_JPG(1, 0, 1),
	ENABLE_IMG_JPEG(1, 0, 1),
	ENABLE_IMG_PNG(1, 0, 1),
	
	ENABLE_VID(0, 0, 1),
	ENABLE_VID_MP4(1, 0, 1),
	ENABLE_VID_M4V(1, 0, 1),
	ENABLE_VID_MOV(1, 0, 1),
	ENABLE_VID_WMV(1, 0, 1),
	ENABLE_VID_AVI(1, 0, 1),
	ENABLE_VID_WEBM(1, 0, 1),
	ENABLE_VID_GIF(1, 0, 1),
	
	TAG_COUNT_MIN(0, 0, 999),
	TAG_COUNT_MAX(999, 0, 999),
	
	MEDIA_LENGTH_MIN(0, 0, 999999),
	MEDIA_LENGTH_MAX(999999, 0, 999999),
	
	LAST_IMPORT_ONLY(0, 0, 1),
	SIMILARITY_FACTOR(50, 0, 100);
	
	private int value;
	private int defaultValue;
	private int minValue;
	private int maxValue;
	
	FilterOption(int defaultValue, int minValue, int maxValue) {
		this.value = defaultValue;
		this.defaultValue = defaultValue;
		this.minValue = minValue;
		this.maxValue = maxValue;
	}
	
	public int getIntValue() {
		return value;
	}
	public boolean getBooleanValue() {
		return value != 0;
	}
	public int getDefaultValue() {
		return defaultValue;
	}
	public void setValue(int value) {
		this.value = value;
	}
	public void setValue(boolean value) {
		if (value) {
			this.value = 1;
		} else {
			this.value = 0;
		}
	}
}
