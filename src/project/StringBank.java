package project;

public enum StringBank {
    APPLICATION_NAME("JavaExplorer"),
    ;

    private String string;

    StringBank(String string) {
        this.string = string;
    }

    public String getValue() {
        return string;
    }
}
