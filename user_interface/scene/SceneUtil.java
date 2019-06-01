package user_interface.scene;

import javafx.stage.Stage;
import lifecycle.InstanceManager;
import user_interface.singleton.utils.SizeUtil;

public class SceneUtil {
    private static IntroScene introScene;
    private static ProjectScene projectScene;
    private static MainScene mainScene;

    public static void initStageLayoutIntro() {
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
    public static void initStageLayoutMain() {
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
            InstanceManager.getMediaView().getVideoPlayer().dispose();
            InstanceManager.getSettings().writeToDisk();
            InstanceManager.getMainDataList().writeToDisk();
            InstanceManager.getLogger().debug(SceneUtil.class, "application exit");
        });
        mainScene.show();
    }
}
