package project.gui.component.top;

public class TopPaneBack {
    /* lazy singleton */
    private static TopPaneBack instance;
    public static TopPaneBack getInstance() {
        if (instance == null) instance = new TopPaneBack();
        return instance;
    }

    /* constructors */
    private TopPaneBack() {
        TopPaneListener.getInstance();
    }
}
