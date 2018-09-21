package project;

import javafx.application.Application;
import javafx.stage.Stage;
import project.control.MainControl;
import project.gui.custom.specific.IntroWindow;

public class Main extends Application {
    private static Stage primaryStage = null;

    @Override
    public void start(Stage primaryStage) {
        MainControl.getLogControl().out(Main.class, "starting application");
        Main.setStage(new IntroWindow());
    }

    public static void main(String[] args) {
        launch(args);
    }

    public static Stage getStage() {
        return primaryStage;
    }

    public static void setStage(Stage mainStage) {
        Main.primaryStage = mainStage;
    }
}
