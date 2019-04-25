package user_interface.scene;

import system.CommonUtil;
import system.InstanceRepo;

import java.awt.*;

public class SceneUtil implements InstanceRepo {
    private static IntroScene introScene;
    private static MainScene mainScene;

    private static double sidePanelPrefWidth = CommonUtil.getUsableScreenWidth() / 10;

    public static void createMainStage() {
        mainStage.setOnCloseRequest(event -> {
            settings.writeToDisk();
            logger.debug(mainStage, "application exit");
        });

        CommonUtil.updateNodeProperties();
    }
    public static void showMainStage() {
        mainStage.show();
    }

    public static void createIntroScene() {
        introScene = new IntroScene();
    }
    public static void createMainScene() {
        mainScene = new MainScene();
    }

    public static void showIntroScene() {
        introScene.show();
    }
    public static void showMainScene() {
        mainStage.setOnCloseRequest(event -> {
            settings.writeToDisk();
            mainDataList.writeToDisk();
            logger.debug(mainStage, "application exit");
        });
        mainScene.show();
    }

    public static double getSidePanelMinWidth() {
        return getUsableScreenWidth() / 10;
    }

    public static double getUsableScreenWidth() {
        return GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().getWidth();
    }
    public static double getUsableScreenHeight() {
        return GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().getHeight();
    }
}
