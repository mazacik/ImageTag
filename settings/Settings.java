package settings;

import system.Instances;
import system.JsonUtil;

import java.io.File;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class Settings implements Instances, Serializable {
    private ArrayList<Setting> settingsList;
    private ArrayList<String> recentProjects;

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
        settingsList.add(new Setting(SettingsEnum.THUMBSIZE, 200, SettingType.USER));
        settingsList.add(new Setting(SettingsEnum.COLORMODE, 0, SettingType.USER));
        settingsList.add(new Setting(SettingsEnum.FONTSIZE, 14, SettingType.USER));

        recentProjects = new ArrayList<>();
    }
    public void writeToDisk() {
        String dir = System.getenv("APPDATA") + File.separator + "ImageTag";
        String path = dir + File.separator + "Settings.json";

        new File(dir).mkdir();
        Type typeToken = JsonUtil.TypeTokenEnum.SETTINGS.getValue();
        JsonUtil.write(this, typeToken, path);
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

    public void addProjectPath(String projectFile) {
        recentProjects.remove(projectFile);
        recentProjects.add(0, projectFile);

        int size = recentProjects.size();
        if (size > 10) recentProjects.subList(10, size).clear();
    }
    public ArrayList<String> getRecentProjects() {
        return recentProjects;
    }
    public ArrayList<Setting> getSettingsList() {
        return settingsList;
    }
    private static class SettingsLoader {
        private static final Settings instance = readFromDisk();
        private static Settings readFromDisk() {
            Type typeToken = JsonUtil.TypeTokenEnum.SETTINGS.getValue();
            Settings settings = (Settings) JsonUtil.read(typeToken, System.getenv("APPDATA") + "\\ImageTag\\Settings.json");

            if (settings == null) {
                settings = new Settings();
                settings.setDefaults();
            }
            return settings;
        }
    }
}
