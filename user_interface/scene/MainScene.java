package user_interface.scene;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import system.CommonUtil;
import system.InstanceRepo;
import user_interface.event.EventUtil;
import user_interface.factory.NodeFactory;
import user_interface.factory.util.enums.ColorType;

public class MainScene implements InstanceRepo {
    private static ObservableList<Node> views;
    private final Scene mainScene;

    MainScene() {
        mainScene = create();
    }

    private Scene create() {
        HBox mainHBox = NodeFactory.getHBox(ColorType.DEF, tagListViewL, tileView, tagListViewR);
        views = mainHBox.getChildren();
        Scene mainScene = new Scene(NodeFactory.getVBox(ColorType.DEF, topMenu, mainHBox));

        mainStage.widthProperty().addListener((observable, oldValue, newValue) -> tileView.adjustPrefColumns());
        CommonUtil.updateNodeProperties(mainScene);

        return mainScene;
    }
    void show() {
        mainStage.setMinWidth(tagListViewL.getMinWidth() + tileView.getTilePane().getPrefTileWidth() + tagListViewR.getMinWidth());
        mainStage.setMinHeight(topMenu.getHeight() + tileView.getTilePane().getPrefTileHeight());

        mainStage.setScene(mainScene);
        mainStage.setMaximized(true);

        tileView.onShown();
        tagListViewL.onShown();
        tagListViewR.onShown();

        topMenu.requestFocus();

        EventUtil.init();
    }

    public static void swapViewMode() {
        if (views.contains(mediaView)) {
            if (mediaView.getVideoPlayer().isPlaying()) {
                mediaView.getVideoPlayer().pause();
            }
            views.set(views.indexOf(mediaView), tileView);
            tileView.adjustViewportToCurrentTarget();
            tileView.requestFocus();
        } else if (views.contains(tileView)) {
            views.set(views.indexOf(tileView), mediaView);
            mediaView.reload();
            mediaView.requestFocus();
        }
    }
    public static boolean isFullView() {
        return views.contains(mediaView);
    }
}
