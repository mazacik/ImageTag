package project;

import javafx.application.Application;
import javafx.stage.Stage;
import project.common.Settings;
import project.customdialog.IntroWindow;
import project.database.DatabaseLoader;

public class Main extends Application {
    private static Stage introStage;

    @Override
    public void start(Stage primaryStage) {
        introStage = primaryStage;
        if (Settings.readFromFile(getClass())) {
            GUIController.getInstance();
            new DatabaseLoader().start();
        } else
            new IntroWindow(introStage);
    }

    public static void main(String[] args) {
        launch(args);
    }

    public static Stage getIntroStage() {
        return introStage;
    }
}
