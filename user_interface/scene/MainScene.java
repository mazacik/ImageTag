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
        if (isFullView()) {
            int nodeIndex = views.indexOf(fullView);
            if (nodeIndex != -1) {
                views.set(nodeIndex, tileView);
                tileView.adjustViewportToCurrentTarget();
                tileView.requestFocus();
            }
        } else {
            int nodeIndex = views.indexOf(tileView);
            if (nodeIndex != -1) {
                views.set(nodeIndex, fullView);
                fullView.reload();
                fullView.requestFocus();
            }
        }
    }
    public static boolean isFullView() {
        return views.contains(fullView);
    }
}
