package settings;

import utils.MainUtil;
import utils.serialization.SerializationUtil;
import utils.serialization.TypeTokenEnum;
import utils.system.SystemUtil;

import java.io.File;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Settings implements MainUtil, Serializable {
    private transient static Type typeToken = TypeTokenEnum.SETTINGS.getValue();
    private transient String currentDirectory;

    private SettingsList settingsList;
    private List<String> recentDirectoriesList;

    public void checkValues() {
        settingsList.forEach(setting -> {
            if (setting.getMinValue() > setting.getMaxValue()) {
                logger.error(this, setting.getId() + ": minValue (" + setting.getMinValue() + ") is bigger than maxValue(" + setting.getMaxValue() + ")");
            } else if (setting.getValue() < setting.getMinValue()) {
                setting.setValue(setting.getMinValue());
            } else if (setting.getValue() > setting.getMaxValue()) {
                setting.setValue(setting.getMaxValue());
            }
        });
    }
    public void checkValue(String id) {
        SettingObject settingObject = settingsList.getObject(id);
        int minValue = settingObject.getMinValue();
        int maxValue = settingObject.getMaxValue();
        int value = settingObject.getValue();

        if (minValue > maxValue) {
            logger.error(this, settingObject.getId() + ": minValue (" + minValue + ") is bigger than maxValue(" + maxValue + ")");
        } else if (value < minValue) {
            settingObject.setValue(minValue);
        } else if (value > maxValue) {
            settingObject.setValue(maxValue);
        }
    }

    public Integer valueOf(SettingsNamespace settingsEnum) {
        return settingsList.valueOf(settingsEnum.getValue());
    }
    public String getCurrentDirectory() {
        return currentDirectory;
    }
    public void setCurrentDirectory(String currentDirectory) {
        this.currentDirectory = currentDirectory;

        recentDirectoriesList.remove(currentDirectory);
        recentDirectoriesList.add(0, currentDirectory);

        int size = recentDirectoriesList.size();
        if (size > 10) recentDirectoriesList.subList(10, size).clear();

        this.writeToDisk();
    }

    public List<String> getRecentDirectoriesList() {
        return recentDirectoriesList;
    }

    /* serialization */
    public Settings readFromDisk() {
        Settings settings = (Settings) SerializationUtil.readJSON(typeToken, System.getenv("APPDATA") + "\\ImageTag\\settings.json");
        if (settings == null) {
            this.setDefaults();
            return this;
        } else {
            settings.checkValues();
            return settings;
        }
    }
    public void writeToDisk() {
        String dir = System.getenv("APPDATA") + "\\ImageTag";
        String path = dir + "\\settings.json";

        new File(dir).mkdir();
        SerializationUtil.writeJSON(settings, typeToken, path);
    }
    private void setDefaults() {
        settingsList = new SettingsList();
        settingsList.add(new SettingObject(SettingsNamespace.MAINSCENE_WIDTH.getValue(), 0, SystemUtil.getScreenWidth(), SystemUtil.getScreenWidth()));
        settingsList.add(new SettingObject(SettingsNamespace.MAINSCENE_HEIGHT.getValue(), 0, SystemUtil.getScreenHeight(), SystemUtil.getScreenHeight()));
        settingsList.add(new SettingObject(SettingsNamespace.TILEVIEW_ICONSIZE.getValue(), 100, 200, 150));
        settingsList.add(new SettingObject(SettingsNamespace.GLOBAL_SPACING.getValue(), 2));

        recentDirectoriesList = new ArrayList<>();
    }
}
