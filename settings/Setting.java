package settings;

public class Setting {
    private SettingsEnum settingsEnum;
    private String value;

    Setting(SettingsEnum settingsEnum, int value) {
        this(settingsEnum, String.valueOf(value));
    }
    Setting(SettingsEnum settingsEnum, String value) {
        this.settingsEnum = settingsEnum;
        this.value = value;
    }

    public SettingsEnum getSettingsEnum() {
        return settingsEnum;
    }
    public String getValue() {
        return value;
    }
    public void setValue(String value) {
        this.value = value;
    }
}
