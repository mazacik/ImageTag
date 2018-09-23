package project;

import javafx.application.Application;
import javafx.stage.Stage;
import project.gui.custom.specific.IntroWindow;

public class Main extends Application implements MainUtils {
    public static void main(String[] args) {
        launch(args);
    }
    public void start(Stage stage) {
        logControl.out(this.getClass(), "starting application");
        new IntroWindow();
    }
}
