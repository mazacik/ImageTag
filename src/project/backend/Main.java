package project.backend;

import javafx.application.Application;
import javafx.stage.Stage;
import project.frontend.SharedFrontend;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        SharedFrontend.initialize(primaryStage);
        SharedBackend.initialize();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
