package settings;

public enum SettingsEnum {
    //System
    TILEVIEW_THUMBSIZE("tileViewIconSize"),
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
