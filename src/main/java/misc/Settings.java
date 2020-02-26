package misc;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public enum Settings {
	COLOR_PRESET("ColorPreset", "0", true),
	FONT_SIZE("FontSize", "16", true),
	GALLERY_TILE_SIZE("GalleryTileSize", "200", true),
	COLLAGE_SIZE("CollageSize", "50", true),
	
	IMPORT_LAST_PATH("ImportLastPath", System.getProperty("user.dir"), false),
	;
	
	private String value;
	private String defaultValue;
	private String name;
	private boolean userModifiable;
	
	Settings(String name, String defaultValue, boolean userModifiable) {
		this.name = name;
		this.defaultValue = defaultValue;
		this.userModifiable = userModifiable;
	}
	
	static {
		readFromDisk();
	}
	private static void readFromDisk() {
		File settingsFile = new File(FileUtil.getFileSettings());
		if (!settingsFile.exists()) {
			for (Settings setting : Settings.values()) setting.value = setting.defaultValue;
			return;
		}
		try {
			List<String> lines = Files.readAllLines(Paths.get(settingsFile.toURI()));
			for (String line : lines) {
				String[] strings = line.split("=");
				if (strings.length > 1) {
					for (Settings setting : Settings.values()) {
						if (setting.name.equals(strings[0])) {
							setting.value = strings[1];
							break;
						}
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		for (Settings setting : Settings.values()) {
			if (setting.value == null || setting.value.isEmpty()) {
				setting.value = setting.defaultValue;
			}
		}
	}
	public static void writeToDisk() {
		File settingsFile = new File(FileUtil.getFileSettings());
		if (settingsFile.exists()) {
			settingsFile.delete();
		}
		try {
			settingsFile.createNewFile();
			BufferedWriter writer = new BufferedWriter(new FileWriter(settingsFile));
			
			StringBuilder sb = new StringBuilder();
			for (Settings setting : Settings.values()) {
				sb.append(setting.name).append('=').append(setting.value).append('\n');
			}
			
			writer.write(sb.toString());
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public String getValue() {
		return value;
	}
	public String getDefaultValue() {
		return defaultValue;
	}
	public int getIntegerValue() {
		return Integer.parseInt(value);
	}
	public boolean isUserModifiable() {
		return userModifiable;
	}
	
	public void setValue(String value) {
		this.value = value;
	}
}
