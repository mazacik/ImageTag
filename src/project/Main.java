package project;

import javafx.application.Application;
import javafx.stage.Stage;
import project.common.Settings;
import project.gui.stage.IntroWindow;
import project.gui.stage.LoadingWindow;

public class Main extends Application {
    private static Stage primaryStage = null;

    @Override
    public void start(Stage primaryStage) {
        if (Settings.readFromFile(getClass())) {
            Main.setStage(new LoadingWindow());
        } else {
            Main.setStage(new IntroWindow());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    /* get */
    public static LoadingWindow getLoadingWindow() {
        return (LoadingWindow) primaryStage;
    }

    /* set */
    public static void setStage(Stage mainStage) {
        Main.primaryStage = mainStage;
    }
}
