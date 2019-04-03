package main;

import javafx.application.Application;
import javafx.stage.Stage;
import system.InstanceRepo;
import user_interface.factory.stage.IntroStage;

public class Main extends Application implements InstanceRepo {
    public static void main(String[] args) {
        launch(args);
    }
    public void start(Stage stage) {
        new IntroStage();
    }
}
