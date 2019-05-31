package main;

import javafx.application.Application;
import javafx.stage.Stage;
import system.Instances;
import user_interface.scene.SceneUtil;

public class Main extends Application implements Instances {
    public static void main(String[] args) {
        launch(args);
    }
    public void start(Stage stage) {
        SceneUtil.createIntroScene();
        SceneUtil.showIntroScene();
        SceneUtil.createMainScene();
    }
}
