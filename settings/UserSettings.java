package settings;

import utils.InstanceRepo;
import utils.serialization.SerializationUtil;
import utils.serialization.TypeTokenEnum;

import java.io.File;
import java.io.Serializable;
import java.lang.reflect.Type;

public class UserSettings implements InstanceRepo, Serializable {
    private transient static Type typeToken = TypeTokenEnum.USERSETTINGS.getValue();
    private UserSettings() {
        if (SettingsLoader.instance != null) {
            throw new IllegalStateException(this.getClass().getSimpleName() + " already instantiated");
        }
    }
    private static UserSettings readFromDisk() {
        UserSettings userSettings = (UserSettings) SerializationUtil.readJSON(typeToken, System.getenv("APPDATA") + "\\ImageTag\\userSettings.json");

        if (userSettings == null) {
            userSettings = new UserSettings();
            userSettings.setDefaults();
        } else {
            userSettings.checkValues();
        }
        return userSettings;
    }
    private void setDefaults() {
        settingsList = new SettingsList();
        settingsList.add(new SettingsBase(SettingsNamespace.TILEVIEW_ICONSIZE.getValue(), 100, 200, 150));
    }
    public static UserSettings getInstance() {
        return SettingsLoader.instance;
    }
    private transient String currentDirectory;

    private SettingsList settingsList;
    public Integer valueOf(SettingsNamespace sn) {
        return settingsList.valueOf(sn.getValue());
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
        String path = dir + "\\userSettings.json";

        new File(dir).mkdir();
        SerializationUtil.writeJSON(SettingsLoader.instance, typeToken, path);
    }

    private static class SettingsLoader {
        private static final UserSettings instance = UserSettings.readFromDisk();
    }
}
