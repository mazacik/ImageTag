package settings;

import system.InstanceRepo;
import system.SerializationUtil;
import system.SystemUtil;

import java.io.File;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class CoreSettings implements InstanceRepo, Serializable {
    private transient static Type typeToken = SerializationUtil.TypeTokenEnum.CORESETTINGS.getValue();
    private transient String currentDirectory;
    private SettingsList settingsList;
    private List<String> recentDirectoriesList;

    private CoreSettings() {
        if (SettingsLoader.instance != null) {
            throw new IllegalStateException(this.getClass().getSimpleName() + " already instantiated");
        }
    }
    private static CoreSettings readFromDisk() {
        CoreSettings coreSettings = (CoreSettings) SerializationUtil.readJSON(typeToken, System.getenv("APPDATA") + "\\ImageTag\\CoreSettings.json");

        if (coreSettings == null) {
            coreSettings = new CoreSettings();
            coreSettings.setDefaults();
        } else {
            coreSettings.checkValues();
        }
        return coreSettings;
    }
    public static CoreSettings getInstance() {
        return SettingsLoader.instance;
    }
    private void setDefaults() {
        settingsList = new SettingsList();
        settingsList.add(new SettingsBase(SettingsNamespace.MAINSCENE_WIDTH.getValue(), 0, SystemUtil.getScreenWidth(), SystemUtil.getScreenWidth()));
        settingsList.add(new SettingsBase(SettingsNamespace.MAINSCENE_HEIGHT.getValue(), 0, SystemUtil.getScreenHeight(), SystemUtil.getScreenHeight()));
        settingsList.add(new SettingsBase(SettingsNamespace.GLOBAL_PADDING.getValue(), 2));
        settingsList.add(new SettingsBase(SettingsNamespace.FONTSIZE.getValue(), 14));

        recentDirectoriesList = new ArrayList<>();
    }
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
    private void writeToDisk() {
        String dir = System.getenv("APPDATA") + "\\ImageTag";
        String path = dir + "\\CoreSettings.json";

        new File(dir).mkdir();
        SerializationUtil.writeJSON(SettingsLoader.instance, typeToken, path);
    }

    private static class SettingsLoader {
        private static final CoreSettings instance = CoreSettings.readFromDisk();
    }
}
