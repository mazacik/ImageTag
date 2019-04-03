package user_interface;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import system.InstanceRepo;
import user_interface.event.EventUtil;
import user_interface.factory.NodeFactory;
import user_interface.factory.node.TitleBar;
import user_interface.factory.util.enums.ColorType;

public class MainStage extends Stage implements InstanceRepo {
    private ObservableList<Node> views;

    public void init() {
        logger.debug(this, "user interface init start");

        HBox.setHgrow(infoListViewL, Priority.ALWAYS);
        HBox.setHgrow(infoListViewR, Priority.ALWAYS);

        this.initStyle(StageStyle.UNDECORATED);
        this.setScene(this.createScene());

        EventUtil.init();

        logger.debug(this, "user interface init end");
    }

    public void swapViewMode() {
        if (this.isFullView()) {
            int nodeIndex = views.indexOf(fullView);
            if (nodeIndex != -1) {
                views.set(nodeIndex, tileView);
                tileView.adjustViewportToCurrentTarget();
                //tileView.requestFocus();
            }
        } else {
            int nodeIndex = views.indexOf(tileView);
            if (nodeIndex != -1) {
                views.set(nodeIndex, fullView);
                fullView.reload();
                //fullView.requestFocus();
            }
        }
    }
    public boolean isFullView() {
        return views.contains(fullView);
    }

    private Scene createScene() {
        TitleBar titleBar = new TitleBar(this, false);
        titleBar.setPadding(new Insets(0, 5, 0, 0));
        titleBar.setBorder(null);

        topMenu.setRight(titleBar);

        HBox mainHBox = NodeFactory.getHBox(ColorType.DEF, infoListViewL, tileView, infoListViewR);
        views = mainHBox.getChildren();

        return new Scene(NodeFactory.getVBox(ColorType.DEF, topMenu, mainHBox));
    }
}
