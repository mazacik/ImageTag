package project;

import javafx.application.Application;
import javafx.stage.Stage;
import project.gui.custom.specific.IntroWindow;
import project.gui.custom.specific.LoadingWindow;
import project.settings.Settings;

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
    public static Stage getStage() {
        return primaryStage;
    }
    public static LoadingWindow getLoadingWindow() {
        return (LoadingWindow) primaryStage;
    }

    /* set */
    public static void setStage(Stage mainStage) {
        Main.primaryStage = mainStage;
    }
}
