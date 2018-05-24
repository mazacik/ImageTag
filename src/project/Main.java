package project;

import javafx.application.Application;
import javafx.stage.Stage;
import project.common.Settings;
import project.customdialog.IntroWindow;
import project.customdialog.LoadingWindow;

public class Main extends Application {
    private static LoadingWindow loadingWindow = null;

    @Override
    public void start(Stage primaryStage) {
        if (Settings.readFromFile(getClass())) {
            loadingWindow = new LoadingWindow();
        } else {
            new IntroWindow();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    /* getters */
    public static LoadingWindow getLoadingWindow() {
        return loadingWindow;
    }

    /* setters */
    public static void setLoadingWindow(LoadingWindow loadingWindow) {
        Main.loadingWindow = loadingWindow;
    }
}
