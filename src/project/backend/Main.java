package project.backend;

import javafx.application.Application;
import javafx.stage.Stage;
import project.frontend.SharedFE;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        SharedFE.initialize(primaryStage);
        SharedBE.initialize();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
