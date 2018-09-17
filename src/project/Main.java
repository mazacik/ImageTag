package project;

import javafx.application.Application;
import javafx.stage.Stage;
import project.gui.custom.specific.IntroWindow;
import project.gui.custom.specific.LoadingWindow;

public class Main extends Application {
    private static Stage primaryStage = null;

    @Override
    public void start(Stage primaryStage) {
        Main.setStage(new IntroWindow());
    }

    public static void main(String[] args) {
        launch(args);
    }

    /* get */
    public static Stage getStage() {
        return primaryStage;
    }
    //todo just stage
    public static LoadingWindow getLoadingWindow() {
        return (LoadingWindow) primaryStage;
    }
    public static IntroWindow getIntroWindow() {
        return (IntroWindow) primaryStage;
    }

    /* set */
    public static void setStage(Stage mainStage) {
        Main.primaryStage = mainStage;
    }
}
