package settings;

public enum SettingsNamespace {
    FONTSIZE("fontSize"),
    MAINSCENE_WIDTH("mainSceneWidth"),
    MAINSCENE_HEIGHT("mainSceneHeight"),
    TILEVIEW_ICONSIZE("tileViewIconSize"),
    GLOBAL_PADDING("globalSpacing"),
    ;

    private String value;

    SettingsNamespace(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
