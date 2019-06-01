package user_interface.scene;

import javafx.animation.Animation;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import system.CommonUtil;
import system.Instances;
import user_interface.factory.NodeUtil;
import user_interface.factory.util.enums.ColorType;
import user_interface.singleton.utils.EventUtil;
import user_interface.singleton.utils.SizeUtil;

public class MainScene implements Instances {
    private static ObservableList<Node> panes;
    private final Scene mainScene;

    MainScene() {
        mainScene = create();
    }

    private Scene create() {
        HBox mainHBox = NodeUtil.getHBox(ColorType.DEF, tagListViewL, tileView, tagListViewR);
        panes = mainHBox.getChildren();
        Scene mainScene = new Scene(NodeUtil.getVBox(ColorType.DEF, topMenu, mainHBox));
        CommonUtil.updateNodeProperties();
        return mainScene;
    }
    //todo move these two methods somewhere else
    public static void swapViewMode() {
        if (panes.contains(mediaView)) {
            if (mediaView.getControlsPopupDelay().getStatus() == Animation.Status.RUNNING) {
                mediaView.getControlsPopupDelay().stop();
            }
            if (mediaView.getControlsPopup().isShowing()) {
                mediaView.getControlsPopup().hide();
            }
            if (mediaView.getVideoPlayer().isPlaying()) {
                mediaView.getVideoPlayer().pause();
            }

            panes.set(panes.indexOf(mediaView), tileView);
            tileView.adjustViewportToCurrentTarget();
            tileView.requestFocus();
        } else if (panes.contains(tileView)) {
            panes.set(panes.indexOf(tileView), mediaView);
            mediaView.requestFocus();
        }
    }
    public static boolean isFullView() {
        return panes.contains(mediaView);
    }
    void show() {
        mainStage.setOpacity(0);
        mainStage.setScene(mainScene);
        tileView.requestFocus();

        SizeUtil.stageWidthChangeHandler();
        SizeUtil.stageHeightChangehandler();
        mainScene.widthProperty().addListener((observable, oldValue, newValue) -> SizeUtil.stageWidthChangeHandler());
        mainScene.heightProperty().addListener((observable, oldValue, newValue) -> SizeUtil.stageHeightChangehandler());

        Platform.runLater(() -> mainStage.setOpacity(1));

        EventUtil.init();

        //StyleUtil.applyScrollbarStyle(tileView);
        //StyleUtil.applyScrollbarStyle(tagListViewL.getTagListScrollPane());
        //StyleUtil.applyScrollbarStyle(tagListViewR.getTagListScrollPane());
    }
}
