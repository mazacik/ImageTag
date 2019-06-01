package user_interface.scene;

import system.Instances;
import user_interface.singleton.utils.SizeUtil;

public class SceneUtil implements Instances {
    private static IntroScene introScene;
    private static ProjectScene projectScene;
    private static MainScene mainScene;

    public static void initStageLayoutIntro() {
        mainStage.setTitle("Welcome");
        mainStage.show();
        double width = SizeUtil.getUsableScreenWidth() / 2.5;
        double height = SizeUtil.getUsableScreenHeight() / 2;
        mainStage.setWidth(width);
        mainStage.setHeight(height);
        mainStage.setMinWidth(width);
        mainStage.setMinHeight(height);
        mainStage.centerOnScreen();
    }
    public static void initStageLayoutMain() {
        mainStage.setMinWidth(100 + SizeUtil.getMinWidthSideLists() * 2 + SizeUtil.getGalleryIconSize());
        mainStage.setMinHeight(100 + SizeUtil.getPrefHeightTopMenu() + SizeUtil.getGalleryIconSize());
        mainStage.setMaximized(true);
    }

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
