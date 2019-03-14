package settings;

import system.InstanceRepo;
import system.SerializationUtil;

import java.io.File;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class UserSettings implements InstanceRepo, Serializable {
    private transient static Type typeToken = SerializationUtil.TypeTokenEnum.USERSETTINGS.getValue();
    private UserSettings() {
        if (SettingsLoader.instance != null) {
            throw new IllegalStateException(this.getClass().getSimpleName() + " already instantiated");
        }
    }
    private ArrayList<SettingBase> settingsList;
    private static UserSettings readFromDisk() {
        UserSettings userSettings = (UserSettings) SerializationUtil.readJSON(typeToken, System.getenv("APPDATA") + "\\ImageTag\\UserSettings.json");

        if (userSettings == null) {
            userSettings = new UserSettings();
            userSettings.setDefaults();
            userSettings.writeToDisk();
        } else {
            userSettings.checkValues();
        }
        return userSettings;
    }
    public static UserSettings getInstance() {
        return SettingsLoader.instance;
    }
    private void setDefaults() {
        settingsList = new ArrayList<>();
        settingsList.add(new SettingBase(SettingsEnum.COLORMODE, 0));
    }
    public void setValueOf(SettingsEnum sn, boolean value) {
        for (SettingBase object : settingsList) {
            if (object.getId().equals(sn)) {
                if (value) {
                    object.setValue(1);
                } else {
                    object.setValue(0);
                }
            }
        }
        //logger.error(this, "setValueOf() -> " + sn + " not found");
    }
    public boolean valueOf(SettingsEnum sn) {
        for (SettingBase object : settingsList) {
            if (object.getId().equals(sn)) {
                return object.getValue() != 0;
            }
        }
        logger.error(this, "valueof() -> " + sn + " not found");
        return false;
    }
    private void checkValues() {
        settingsList.forEach(setting -> {
            if (setting.getValue() < 0 || setting.getValue() > 1) {
                setting.setValue(0);
            }
        });
    }
    public void writeToDisk() {
        String dir = System.getenv("APPDATA") + "\\ImageTag";
        String path = dir + "\\UserSettings.json";

        new File(dir).mkdir();
        SerializationUtil.writeJSON(SettingsLoader.instance, typeToken, path);
    }

    private static class SettingsLoader {
        private static final UserSettings instance = UserSettings.readFromDisk();
    }
}
