package settings;

public enum SettingsEnum {
    MAINSCENEW("mainSceneWidth"),
    MAINSCENEH("mainSceneHeight"),
    TILEVIEW_ICONSIZE("tileViewIconSize"),
    ;

    private String value;

    SettingsEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
