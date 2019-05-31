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
import user_interface.singleton.utils.StyleUtil;

public class MainScene implements Instances {
    private static ObservableList<Node> panes;
    private final Scene mainScene;

    MainScene() {
        mainScene = create();
    }
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
    private Scene create() {
        HBox mainHBox = NodeUtil.getHBox(ColorType.DEF, tagListViewL, tileView, tagListViewR);
        panes = mainHBox.getChildren();
        Scene mainScene = new Scene(NodeUtil.getVBox(ColorType.DEF, topMenu, mainHBox));
        CommonUtil.updateNodeProperties();
        return mainScene;
    }
    void show() {
        mainStage.setOpacity(0);
        mainStage.setMinWidth(100 + SizeUtil.getMinWidthSideLists() * 2 + SizeUtil.getGalleryIconSize());
        mainStage.setMinHeight(100 + SizeUtil.getPrefHeightTopMenu() + SizeUtil.getGalleryIconSize());
        mainStage.setScene(mainScene);
        mainStage.setMaximized(true);
        tileView.requestFocus();

        StyleUtil.applyScrollbarStyle(tileView);
        StyleUtil.applyScrollbarStyle(tagListViewL.getTagListScrollPane());
        StyleUtil.applyScrollbarStyle(tagListViewR.getTagListScrollPane());

        SizeUtil.stageWidthChangeHandler();
        SizeUtil.stageHeightChangehandler();
        mainScene.widthProperty().addListener((observable, oldValue, newValue) -> SizeUtil.stageWidthChangeHandler());
        mainScene.heightProperty().addListener((observable, oldValue, newValue) -> SizeUtil.stageHeightChangehandler());

        Platform.runLater(() -> mainStage.setOpacity(1));

        EventUtil.init();
    }
}
