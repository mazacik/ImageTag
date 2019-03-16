package settings;

import system.InstanceRepo;
import system.SerializationUtil;

import java.awt.*;
import java.io.File;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class CoreSettings implements InstanceRepo, Serializable {
    private ArrayList<SettingBase> settingsList;
    private ArrayList<String> recentDirectoriesList;

    private transient String currentDirectory;

    private CoreSettings() {
        if (SettingsLoader.instance != null) {
            throw new IllegalStateException(this.getClass().getSimpleName() + " already instantiated");
        }
    }
    public static CoreSettings getInstance() {
        return SettingsLoader.instance;
    }
    private void setDefaults() {
        settingsList = new ArrayList<>();
        int width = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().width;
        int height = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().height;
        settingsList.add(new SettingBase(SettingsEnum.MAINSCENE_WIDTH, 0, width, width));
        settingsList.add(new SettingBase(SettingsEnum.MAINSCENE_HEIGHT, 0, height, height));
        settingsList.add(new SettingBase(SettingsEnum.TILEVIEW_ICONSIZE, 100, 200, 150));
        settingsList.add(new SettingBase(SettingsEnum.GLOBAL_PADDING, 2));
        settingsList.add(new SettingBase(SettingsEnum.FONTSIZE, 14));

        recentDirectoriesList = new ArrayList<>();
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
    public void writeToDisk() {
        String dir = System.getenv("APPDATA") + "\\ImageTag";
        String path = dir + "\\CoreSettings.json";

        new File(dir).mkdir();
        Type typeToken = SerializationUtil.TypeTokenEnum.CORESETTINGS.getValue();
        SerializationUtil.writeJSON(SettingsLoader.instance, typeToken, path);
    }

    public Integer valueOf(SettingsEnum setting) {
        for (SettingBase object : settingsList) {
            if (object.getId().equals(setting)) {
                return object.getValue();
            }
        }
        logger.error(this, "valueof() -> " + setting + " not found");
        return null;
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
    public ArrayList<String> getRecentDirectoriesList() {
        return recentDirectoriesList;
    }

    private static class SettingsLoader {
        private static final CoreSettings instance = readFromDisk();
        private static CoreSettings readFromDisk() {
            Type typeToken = SerializationUtil.TypeTokenEnum.CORESETTINGS.getValue();
            CoreSettings coreSettings = (CoreSettings) SerializationUtil.readJSON(typeToken, System.getenv("APPDATA") + "\\ImageTag\\CoreSettings.json");

            if (coreSettings == null) {
                coreSettings = new CoreSettings();
                coreSettings.setDefaults();
                coreSettings.writeToDisk();
            } else {
                coreSettings.checkValues();
            }
            return coreSettings;
        }
    }
}
