package settings;

import system.JsonUtil;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class Settings implements Serializable {
    private ArrayList<Setting> settingsList;
    private ArrayList<String> recentProjects;

    public Settings() {
        Settings settings = readFromDisk();
        if (settings != null && settings.settingsList != null && settings.recentProjects != null) {
            settingsList = settings.settingsList;
            recentProjects = settings.recentProjects;
        } else {
            setDefaults();
        }
    }
    private void setDefaults() {
        settingsList = new ArrayList<>();
        settingsList.add(new Setting(SettingsEnum.THUMBSIZE, 200));
        settingsList.add(new Setting(SettingsEnum.COLORMODE, 0));
        settingsList.add(new Setting(SettingsEnum.FONTSIZE, 14));

        recentProjects = new ArrayList<>();
    }

    public void writeToDisk() {
        Type typeToken = JsonUtil.TypeTokenEnum.SETTINGS.getValue();
        JsonUtil.write(this, typeToken, "settings.json");
    }
    private static Settings readFromDisk() {
        Type typeToken = JsonUtil.TypeTokenEnum.SETTINGS.getValue();
        return (Settings) JsonUtil.read(typeToken, "settings.json");
    }

    public String strValueOf(SettingsEnum setting) {
        for (Setting object : settingsList) {
            if (object.getSettingsEnum().equals(setting)) {
                return object.getValue();
            }
        }
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
                object.setValue(value);
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
}
