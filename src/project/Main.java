package project;

import javafx.application.Application;
import javafx.stage.Stage;
import project.customdialog.IntroWindow;

public class Main extends Application {
    private static Stage introStage;

    @Override
    public void start(Stage primaryStage) {
        introStage = primaryStage;
        new IntroWindow(introStage);
    }

    public static void main(String[] args) {
        launch(args);
    }

    public static Stage getIntroStage() {
        return introStage;
    }
}
