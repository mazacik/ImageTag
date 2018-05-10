package project.backend.shared;

import javafx.application.Application;
import javafx.stage.Stage;
import project.frontend.shared.Frontend;

public class Main extends Application {
    public static final int GALLERY_ICON_SIZE_MAX = 200;
    public static final int GALLERY_ICON_SIZE_MIN = 100;
    public static int GALLERY_ICON_SIZE_PREF = 150;

    @Override
    public void start(Stage primaryStage) {
        Frontend.initialize(primaryStage);
        Backend.initialize();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
