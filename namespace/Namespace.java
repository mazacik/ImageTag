package namespace;

public enum Namespace {
    GUI_SIDE_BTN_EXPCOL_STATE_TRUE("Collapse"),
    GUI_SIDE_BTN_EXPCOL_STATE_FALSE("Expand"),
    ;

    private String value;
    Namespace(String value) {
        this.value = value;
    }
    public String getValue() {
        return value;
    }
}
