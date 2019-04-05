package settings;

public class Setting {
    private SettingsEnum settingsEnum;
    private String value;
    private SettingType settingType;

    public Setting(SettingsEnum settingsEnum, int value, SettingType settingType) {
        this(settingsEnum, String.valueOf(value), settingType);
    }
    public Setting(SettingsEnum settingsEnum, String value, SettingType settingType) {
        this.settingsEnum = settingsEnum;
        this.value = value;
        this.settingType = settingType;
    }

    public SettingsEnum getSettingsEnum() {
        return settingsEnum;
    }
    public SettingType getSettingType() {
        return settingType;
    }
    public String getValue() {
        return value;
    }
    public void setValue(String value) {
        this.value = value;
    }
}
