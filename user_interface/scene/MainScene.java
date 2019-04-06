package user_interface.scene;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import system.CommonUtil;
import system.InstanceRepo;
import user_interface.event.EventUtil;
import user_interface.factory.NodeFactory;
import user_interface.factory.node.TitleBar;
import user_interface.factory.util.enums.ColorType;

public class MainScene implements InstanceRepo {
    private static ObservableList<Node> views;
    private final Scene mainScene;

    MainScene() {
        mainScene = create();
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
    private Scene create() {
        HBox mainHBox = NodeFactory.getHBox(ColorType.DEF, infoListViewL, tileView, infoListViewR);
        views = mainHBox.getChildren();
        Scene mainScene = new Scene(NodeFactory.getVBox(ColorType.DEF, topMenu, mainHBox));
        TitleBar titleBar = new TitleBar(mainScene, false);
        titleBar.setPadding(new Insets(0, 5, 0, 0));
        titleBar.setBorder(null);

        topMenu.setRight(titleBar);

        CommonUtil.updateNodeProperties(mainScene);

        return mainScene;
    }
    void show() {
        //target.set(mainDataList.get(0));
        reload.doReload();

        mainStage.setScene(mainScene);
        mainStage.setWidth(CommonUtil.getUsableScreenWidth());
        mainStage.setHeight(CommonUtil.getUsableScreenHeight());
        mainStage.centerOnScreen();

        tileView.onShown();
        infoListViewL.onShown();
        infoListViewR.onShown();

        topMenu.requestFocus();

        EventUtil.init();
    }
}
