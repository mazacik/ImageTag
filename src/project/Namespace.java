package project;

public enum Namespace {
    APPLICATION_NAME("JavaExplorer"),
    ;

    private String string;

    Namespace(String string) {
        this.string = string;
    }

    public String getValue() {
        return string;
    }
}
