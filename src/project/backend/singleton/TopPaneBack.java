package project.backend.singleton;

public class TopPaneBack {
    private static TopPaneBack instance = new TopPaneBack();


    private TopPaneBack() {

    }

    public static TopPaneBack getInstance() {
        return instance;
    }
}
