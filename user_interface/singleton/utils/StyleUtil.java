package user_interface.singleton.utils;

import javafx.scene.Node;

public abstract class StyleUtil {
    public static void applyScrollbarStyle(Node node) {
        try {
            node.applyCss();
            node.lookup(".track").setStyle("-fx-background-color: transparent;" +
                    " -fx-border-color: transparent;" +
                    " -fx-background-radius: 0.0em;" +
                    " -fx-border-radius: 0.0em;" +
                    " -fx-padding: 0.0 0.0 0.0 0.0;");
            node.lookup(".scroll-bar").setStyle("-fx-background-color: transparent;" +
                    " -fx-pref-width: 15;" +
                    " -fx-padding: 3 2 3 3;");
            node.lookup(".increment-button").setStyle("-fx-background-color: transparent;" +
                    " -fx-background-radius: 0.0em;" +
                    " -fx-padding: 0.0 0.0 0.0 0.0;");
            node.lookup(".decrement-button").setStyle("-fx-background-color: transparent;" +
                    " -fx-background-radius: 0.0em;" +
                    " -fx-padding: 0.0 0.0 0.0 0.0;");
            node.lookup(".increment-arrow").setStyle("-fx-padding: 0.0em 0.0;");
            node.lookup(".decrement-arrow").setStyle("-fx-padding: 0.0em 0.0;");
            node.lookup(".thumb").setStyle("-fx-background-color: derive(black, 90.0%);" +
                    " -fx-background-insets: 0.0, 0.0, 0.0;" +
                    " -fx-background-radius: 0.0em;");
            node.lookup(".viewport").setStyle("-fx-background-color: transparent;");
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }
}
