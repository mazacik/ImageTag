package user_interface;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import system.CommonUtil;
import system.InstanceRepo;
import user_interface.factory.NodeFactory;
import user_interface.factory.node.TitleBar;
import user_interface.factory.node.popup.DataObjectRCM;
import user_interface.factory.node.popup.InfoObjectRCM;
import user_interface.factory.util.enums.ColorType;
import user_interface.singleton.center.FullViewEvent;
import user_interface.singleton.center.TileViewEvent;
import user_interface.singleton.side.InfoListViewLEvent;
import user_interface.singleton.side.InfoListViewREvent;
import user_interface.singleton.top.TopMenuEvent;

public class MainStage extends Stage implements InstanceRepo {
    private final HBox hBox = NodeFactory.getHBox(ColorType.DEF, infoListViewL, tileView, infoListViewR);
    private final DataObjectRCM dataObjectRCM = new DataObjectRCM();
    private final InfoObjectRCM infoObjectRCM = new InfoObjectRCM();

    public void initialize() {
        logger.debug(this, "user_interface initialize start");
        setDefaultValues();
        initializeEvents();
        CommonUtil.updateNodeProperties();
        logger.debug(this, "user_interface initialize done");
    }
    private void setDefaultValues() {
        TitleBar titleBar = new TitleBar(this, false);
        titleBar.setLeft(topMenu);
        titleBar.setPadding(new Insets(0, 5, 0, 0));
        this.setScene(new Scene(NodeFactory.getVBox(ColorType.DEF, titleBar, hBox)));
        this.initStyle(StageStyle.UNDECORATED);
        HBox.setHgrow(infoListViewL, Priority.ALWAYS);
        HBox.setHgrow(infoListViewR, Priority.ALWAYS);

        this.setOnShowing(event -> reload.doReload());
        this.setOnShown(event -> {
            this.centerOnScreen();
            this.setMaximized(true);
            tileView.setCustomBounds(tileView.getViewportBounds());
            tileView.lookupAll(".scroll-bar").forEach(sb -> sb.setStyle("-fx-background-color: transparent;"));
            tileView.lookupAll(".increment-button").forEach(sb -> sb.setStyle("-fx-background-color: transparent;"));
            tileView.lookupAll(".decrement-button").forEach(sb -> sb.setStyle("-fx-background-color: transparent;"));
            tileView.lookupAll(".thumb").forEach(sb -> sb.setStyle("-fx-background-color: gray; -fx-background-insets: 0 4 0 4;"));
            target.set(mainDataList.get(0));
        });
        this.setOnCloseRequest(event -> {
            mainDataList.writeToDisk();
            logger.debug(this, "application exit");
        });
    }
    private void initializeEvents() {
        new GlobalEvent();

        new TopMenuEvent();
        new TileViewEvent();
        new FullViewEvent();
        new InfoListViewLEvent();
        new InfoListViewREvent();
    }

    public void swapDisplayMode() {
        if (this.isFullView()) {
            hBox.getChildren().set(hBox.getChildren().indexOf(fullView), tileView);
            tileView.adjustViewportToCurrentTarget();
        } else {
            hBox.getChildren().set(hBox.getChildren().indexOf(tileView), fullView);
            fullView.reload();
        }
    }
    public boolean isFullView() {
        return hBox.getChildren().contains(fullView);
    }

    public DataObjectRCM getDataObjectRCM() {
        return dataObjectRCM;
    }
    public InfoObjectRCM getInfoObjectRCM() {
        return infoObjectRCM;
    }
}
