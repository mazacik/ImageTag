package settings;

import system.InstanceRepo;
import system.SerializationUtil;

import java.io.File;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class Settings implements InstanceRepo, Serializable {
    private ArrayList<Setting> settingsList;
    private ArrayList<String> recentDirList;
    private ArrayList<String> importDirList;

    private transient String currentDirectory;

    Settings() {
        if (SettingsLoader.instance != null) {
            throw new IllegalStateException(this.getClass().getSimpleName() + " already instantiated");
        }
    }
    public static Settings getInstance() {
        return SettingsLoader.instance;
    }
    private void setDefaults() {
        settingsList = new ArrayList<>();
        settingsList.add(new Setting(SettingsEnum.TILEVIEW_ICONSIZE, 150, SettingType.SYSTEM));
        settingsList.add(new Setting(SettingsEnum.GLOBAL_PADDING, 2, SettingType.SYSTEM));
        settingsList.add(new Setting(SettingsEnum.COLORMODE, 0, SettingType.USER));
        settingsList.add(new Setting(SettingsEnum.FONTSIZE, 14, SettingType.USER));

        recentDirList = new ArrayList<>();
        importDirList = new ArrayList<>();
    }
    public void writeToDisk() {
        String dir = System.getenv("APPDATA") + "\\ImageTag";
        String path = dir + "\\Settings.json";

        new File(dir).mkdir();
        Type typeToken = SerializationUtil.TypeTokenEnum.SETTINGS.getValue();
        SerializationUtil.writeJSON(this, typeToken, path);
    }

    public String strValueOf(SettingsEnum setting) {
        for (Setting object : settingsList) {
            if (object.getSettingsEnum().equals(setting)) {
                return object.getValue();
            }
        }
        logger.error(this, "valueof() -> " + setting + " not found");
        return null;
    }
    public int intValueOf(SettingsEnum setting) {
        for (Setting object : settingsList) {
            if (object.getSettingsEnum().equals(setting)) {
                try {
                    return Integer.valueOf(object.getValue());
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        }
        return -1;
    }
    public void setValueOf(SettingsEnum setting, String value) {
        for (Setting object : settingsList) {
            if (object.getSettingsEnum().equals(setting)) {
                if (object.getSettingType() == SettingType.USER) {
                    object.setValue(value);
                }
            }
        }
    }
    public void setValueOf(SettingsEnum setting, int value) {
        setValueOf(setting, String.valueOf(value));
    }

    public String getCurrentDirectory() {
        return currentDirectory;
    }
    public void setCurrentDirectory(String currentDirectory) {
        SettingsLoader.instance.currentDirectory = currentDirectory;

        recentDirList.remove(currentDirectory);
        recentDirList.add(0, currentDirectory);

        int size = recentDirList.size();
        if (size > 10) recentDirList.subList(10, size).clear();
    }
    public ArrayList<String> getRecentDirList() {
        return recentDirList;
    }
    public ArrayList<String> getImportDirList() {
        return importDirList;
    }
    public ArrayList<Setting> getSettingsList() {
        return settingsList;
    }
    private static class SettingsLoader {
        private static final Settings instance = readFromDisk();
        private static Settings readFromDisk() {
            Type typeToken = SerializationUtil.TypeTokenEnum.SETTINGS.getValue();
            Settings settings = (Settings) SerializationUtil.readJSON(typeToken, System.getenv("APPDATA") + "\\ImageTag\\Settings.json");

            if (settings == null) {
                settings = new Settings();
                settings.setDefaults();
            }
            return settings;
        }
    }
}
