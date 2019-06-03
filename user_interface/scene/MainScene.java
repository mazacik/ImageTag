package user_interface.scene;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import lifecycle.InstanceManager;
import system.CommonUtil;
import user_interface.factory.NodeUtil;
import user_interface.factory.util.enums.ColorType;
import user_interface.singleton.center.GalleryPane;
import user_interface.singleton.center.MediaPane;
import user_interface.singleton.utils.EventUtil;
import user_interface.singleton.utils.SizeUtil;
import user_interface.singleton.utils.StyleUtil;

public class MainScene {
    private static ObservableList<Node> panes;
    private final Scene mainScene;

    MainScene() {
        mainScene = create();
    }

    private Scene create() {
        HBox mainHBox = NodeUtil.getHBox(ColorType.DEF, InstanceManager.getFilterPane(), InstanceManager.getGalleryPane(), InstanceManager.getSelectPane());
        panes = mainHBox.getChildren();
        Scene mainScene = new Scene(NodeUtil.getVBox(ColorType.DEF, InstanceManager.getToolbarPane(), mainHBox));
        CommonUtil.updateNodeProperties(mainScene);
        return mainScene;
    }
    //todo move these two methods somewhere else
    public static void swapViewMode() {
        MediaPane mediaPane = InstanceManager.getMediaPane();
        GalleryPane galleryPane = InstanceManager.getGalleryPane();
        if (panes.contains(mediaPane)) {
            if (InstanceManager.getMediaPane().getControls().isShowing()) {
                InstanceManager.getMediaPane().getControls().hide();
            }
            if (InstanceManager.getMediaPane().getVideoPlayer().isPlaying()) {
                InstanceManager.getMediaPane().getVideoPlayer().pause();
            }

            panes.set(panes.indexOf(mediaPane), galleryPane);
            InstanceManager.getGalleryPane().adjustViewportToCurrentTarget();
            InstanceManager.getGalleryPane().requestFocus();
        } else if (panes.contains(galleryPane)) {
            panes.set(panes.indexOf(galleryPane), mediaPane);
            InstanceManager.getMediaPane().requestFocus();
        }
    }
    public static boolean isFullView() {
        return panes.contains(InstanceManager.getMediaPane());
    }
    void show() {
        InstanceManager.getMainStage().setOpacity(0);
        InstanceManager.getMainStage().setScene(mainScene);
        InstanceManager.getGalleryPane().requestFocus();

        SizeUtil.stageWidthChangeHandler();
        SizeUtil.stageHeightChangehandler();
        mainScene.widthProperty().addListener((observable, oldValue, newValue) -> SizeUtil.stageWidthChangeHandler());
        mainScene.heightProperty().addListener((observable, oldValue, newValue) -> SizeUtil.stageHeightChangehandler());

        Platform.runLater(() -> InstanceManager.getMainStage().setOpacity(1));

        EventUtil.init();

        StyleUtil.applyScrollbarStyle(InstanceManager.getGalleryPane());
        StyleUtil.applyScrollbarStyle(InstanceManager.getFilterPane().getTagListScrollPane());
        StyleUtil.applyScrollbarStyle(InstanceManager.getSelectPane().getTagListScrollPane());
    }
}
