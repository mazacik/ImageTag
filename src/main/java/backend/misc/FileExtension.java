package backend.misc;

public enum FileExtension {
	JPG("jpg"),
	JPEG("jpeg"),
	PNG("png"),
	GIF("gif"),
	MP4("mp4"),
	M4V("m4v"),
	MOV("mov"),
	WMV("wmv"),
	AVI("avi"),
	WEBM("webm");
	
	private final String value;
	
	FileExtension(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
	
	public static FileExtension of(String name) {
		if (name == null || name.isEmpty()) return null;
		try {
			return valueOf(name.toUpperCase());
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			return null;
		}
	}
	
}
