package main;

import javafx.application.Application;
import javafx.stage.Stage;
import system.InstanceRepo;
import user_interface.scene.SceneUtil;

public class Main extends Application implements InstanceRepo {
    public static void main(String[] args) {
        launch(args);
    }
    public void start(Stage stage) {
        SceneUtil.createMainStage();
        SceneUtil.createIntroScene();
        SceneUtil.showIntroScene();
    }
}
