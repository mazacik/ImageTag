package settings;

public enum SettingsEnum {
    //System
    MAINSCENE_WIDTH("mainSceneWidth"),
    MAINSCENE_HEIGHT("mainSceneHeight"),
    TILEVIEW_ICONSIZE("tileViewIconSize"),
    GLOBAL_PADDING("globalSpacing"),
    FONTSIZE("fontSize"),

    //User
    COLORMODE("colorMode"),
    ;

    private String value;

    SettingsEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
