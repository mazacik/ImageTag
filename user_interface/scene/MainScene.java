package user_interface.scene;

import javafx.animation.Animation;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import lifecycle.InstanceManager;
import system.CommonUtil;
import user_interface.factory.NodeUtil;
import user_interface.factory.util.enums.ColorType;
import user_interface.singleton.center.MediaView;
import user_interface.singleton.center.TileView;
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
        HBox mainHBox = NodeUtil.getHBox(ColorType.DEF, InstanceManager.getTagListViewL(), InstanceManager.getTileView(), InstanceManager.getTagListViewR());
        panes = mainHBox.getChildren();
        Scene mainScene = new Scene(NodeUtil.getVBox(ColorType.DEF, InstanceManager.getTopMenu(), mainHBox));
        CommonUtil.updateNodeProperties(mainScene);
        return mainScene;
    }
    //todo move these two methods somewhere else
    public static void swapViewMode() {
        MediaView mediaView = InstanceManager.getMediaView();
        TileView tileView = InstanceManager.getTileView();
        if (panes.contains(mediaView)) {
            if (InstanceManager.getMediaView().getControlsPopupDelay().getStatus() == Animation.Status.RUNNING) {
                InstanceManager.getMediaView().getControlsPopupDelay().stop();
            }
            if (InstanceManager.getMediaView().getControlsPopup().isShowing()) {
                InstanceManager.getMediaView().getControlsPopup().hide();
            }
            if (InstanceManager.getMediaView().getVideoPlayer().isPlaying()) {
                InstanceManager.getMediaView().getVideoPlayer().pause();
            }

            panes.set(panes.indexOf(mediaView), tileView);
            InstanceManager.getTileView().adjustViewportToCurrentTarget();
            InstanceManager.getTileView().requestFocus();
        } else if (panes.contains(tileView)) {
            panes.set(panes.indexOf(tileView), mediaView);
            InstanceManager.getMediaView().requestFocus();
        }
    }
    public static boolean isFullView() {
        return panes.contains(InstanceManager.getMediaView());
    }
    void show() {
        InstanceManager.getMainStage().setOpacity(0);
        InstanceManager.getMainStage().setScene(mainScene);
        InstanceManager.getTileView().requestFocus();

        SizeUtil.stageWidthChangeHandler();
        SizeUtil.stageHeightChangehandler();
        mainScene.widthProperty().addListener((observable, oldValue, newValue) -> SizeUtil.stageWidthChangeHandler());
        mainScene.heightProperty().addListener((observable, oldValue, newValue) -> SizeUtil.stageHeightChangehandler());

        Platform.runLater(() -> InstanceManager.getMainStage().setOpacity(1));

        EventUtil.init();

        StyleUtil.applyScrollbarStyle(InstanceManager.getTileView());
        StyleUtil.applyScrollbarStyle(InstanceManager.getTagListViewL().getTagListScrollPane());
        StyleUtil.applyScrollbarStyle(InstanceManager.getTagListViewR().getTagListScrollPane());
    }
}
