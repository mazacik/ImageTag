package user_interface.utils;

import javafx.stage.Stage;
import lifecycle.InstanceManager;
import user_interface.scene.IntroScene;
import user_interface.scene.MainScene;
import user_interface.scene.ProjectScene;
import user_interface.singleton.center.VideoPlayer;
import user_interface.utils.SizeUtil;

public class SceneUtil {
    private static IntroScene introScene;
    private static ProjectScene projectScene;
    private static MainScene mainScene;

    public static void stageLayoutIntro() {
        Stage mainStage = InstanceManager.getMainStage();
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
    public static void stageLayoutMain() {
        Stage mainStage = InstanceManager.getMainStage();
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
        InstanceManager.getMainStage().setOnCloseRequest(event -> {
            VideoPlayer videoPlayer = InstanceManager.getMediaPane().getVideoPlayer();
            if (videoPlayer != null) videoPlayer.dispose();
            InstanceManager.getSettings().writeToDisk();
            InstanceManager.getObjectListMain().writeToDisk();
            InstanceManager.getTagListMain().writeDummyToDisk();
            InstanceManager.getLogger().debug("application exit");
        });
        mainScene.show();
    }
}