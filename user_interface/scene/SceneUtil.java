package user_interface.scene;

import system.Instances;

public class SceneUtil implements Instances {
    private static IntroScene introScene;
    private static ProjectScene projectScene;
    private static MainScene mainScene;

    public static void createIntroScene() {
        introScene = new IntroScene();
    }
    public static void createProjectScene() {
        projectScene = new ProjectScene();
    }
    public static void createMainScene() {
        mainScene = new MainScene();
    }

    public static void showIntroScene() {
        introScene.show();
    }
    public static void showProjectScene() {
        createProjectScene();
        projectScene.show();
    }
    public static void showMainScene() {
        mainStage.setOnCloseRequest(event -> {
            mediaView.getVideoPlayer().dispose();
            settings.writeToDisk();
            mainDataList.writeToDisk();
            logger.debug(mainStage, "application exit");
        });
        mainScene.show();
    }
}
