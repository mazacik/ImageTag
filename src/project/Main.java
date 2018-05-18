package project;

import javafx.application.Application;
import javafx.stage.Stage;
import project.frontend.common.IntroWindow;

public class Main extends Application {
    private static Stage mainStage;


    @Override
    public void start(Stage primaryStage) {
        mainStage = primaryStage;
        new IntroWindow(mainStage);
    }

    public static void main(String[] args) {
        launch(args);
    }

    public static Stage getMainStage() {
        return mainStage;
    }
}
