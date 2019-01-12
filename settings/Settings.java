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
    private Settings() {
        if (SettingsLoader.instance != null) {
            throw new IllegalStateException(this.getClass().getSimpleName() + " already instantiated");
        }
    }
    private static Settings readFromDisk() {
        Settings settings = (Settings) SerializationUtil.readJSON(typeToken, System.getenv("APPDATA") + "\\ImageTag\\settings.json");

        if (settings == null) {
            settings = new Settings();
            settings.setDefaults();
        } else {
            settings.checkValues();
        }
        return settings;
    }
    public static Settings getInstance() {
        return SettingsLoader.instance;
    }
    private void setDefaults() {
        settingsList = new SettingsList();
        settingsList.add(new SettingsBase(SettingsNamespace.MAINSCENE_WIDTH.getValue(), 0, SystemUtil.getScreenWidth(), SystemUtil.getScreenWidth()));
        settingsList.add(new SettingsBase(SettingsNamespace.MAINSCENE_HEIGHT.getValue(), 0, SystemUtil.getScreenHeight(), SystemUtil.getScreenHeight()));
        settingsList.add(new SettingsBase(SettingsNamespace.TILEVIEW_ICONSIZE.getValue(), 100, 200, 150));
        settingsList.add(new SettingsBase(SettingsNamespace.GLOBAL_PADDING.getValue(), 2));

        recentDirectoriesList = new ArrayList<>();
    }

    private transient static Type typeToken = TypeTokenEnum.SETTINGS.getValue();
    private transient String currentDirectory;

    private SettingsList settingsList;
    private List<String> recentDirectoriesList;
    public Integer valueOf(SettingsNamespace sn) {
        return settingsList.valueOf(sn.getValue());
    }
    public String getCurrentDirectory() {
        return currentDirectory;
    }
    public void setCurrentDirectory(String currentDirectory) {
        SettingsLoader.instance.currentDirectory = currentDirectory;

        recentDirectoriesList.remove(currentDirectory);
        recentDirectoriesList.add(0, currentDirectory);

        int size = recentDirectoriesList.size();
        if (size > 10) recentDirectoriesList.subList(10, size).clear();

        SettingsLoader.instance.writeToDisk();
    }

    public List<String> getRecentDirectoriesList() {
        return recentDirectoriesList;
    }
    private void checkValues() {
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

    //todo split to core and user
    private static class SettingsLoader {
        private static final Settings instance = Settings.readFromDisk();
    }
    private void writeToDisk() {
        String dir = System.getenv("APPDATA") + "\\ImageTag";
        String path = dir + "\\settings.json";

        new File(dir).mkdir();
        SerializationUtil.writeJSON(SettingsLoader.instance, typeToken, path);
    }
}
