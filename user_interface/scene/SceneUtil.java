package user_interface.scene;

import javafx.stage.StageStyle;
import system.CommonUtil;
import system.InstanceRepo;

public class SceneUtil implements InstanceRepo {
    private static IntroScene introScene;
    private static LoadingScene loadingScene;
    private static MainScene mainScene;

    public static void createMainStage() {
        mainStage.initStyle(StageStyle.UNDECORATED);
        mainStage.setResizable(false);
        mainStage.setOnCloseRequest(event -> {
            settings.writeToDisk();
            logger.debug(mainStage, "application exit");
        });
        mainStage.show();

        CommonUtil.updateNodeProperties();
    }

    public static void createIntroScene() {
        introScene = new IntroScene();
    }
    public static void createLoadingScene() {
        loadingScene = new LoadingScene();
    }
    public static void createMainScene() {
        mainScene = new MainScene();
    }

    public static LoadingScene getLoadingScene() {
        return loadingScene;
    }

    public static void showIntroScene() {
        introScene.show();
    }
    public static void showLoadingScene() {
        loadingScene.show();
    }
    public static void showMainScene() {
        mainScene.show();
    }
}
