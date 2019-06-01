package lifecycle;

import javafx.application.Platform;
import user_interface.scene.SceneUtil;

public abstract class LifeCycleManager {
    public static void start() {
        InstanceManager.init();

        initGUI2();
    }


    private static void initGUI2() {
        SceneUtil.createIntroScene();
        SceneUtil.initStageLayoutIntro();
        SceneUtil.showIntroScene();
        Platform.runLater(() -> {
            SceneUtil.createProjectScene();
            SceneUtil.createMainScene();
        });
    }
}
