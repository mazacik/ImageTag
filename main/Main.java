package main;

import javafx.application.Application;
import javafx.application.Platform;
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
        SceneUtil.initStageLayoutIntro();
        Platform.runLater(() -> {
            SceneUtil.createProjectScene();
            SceneUtil.createMainScene();
        });
    }
}
