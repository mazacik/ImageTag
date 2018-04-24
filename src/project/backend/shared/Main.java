package project.backend.shared;

import javafx.application.Application;
import javafx.stage.Stage;
import project.frontend.shared.Frontend;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        Frontend.initialize(primaryStage);
        Backend.initialize();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
