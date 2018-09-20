package project;

import javafx.application.Application;
import javafx.stage.Stage;
import project.gui.custom.specific.IntroWindow;

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

    /* set */
    public static void setStage(Stage mainStage) {
        Main.primaryStage = mainStage;
    }
}
