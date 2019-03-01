package user_interface;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import system.CommonUtil;
import system.InstanceRepo;
import user_interface.node_factory.NodeFactory;
import user_interface.node_factory.template.DataContextMenu;
import user_interface.node_factory.template.InfoContextMenu;
import user_interface.node_factory.template.generic.TitleBar;
import user_interface.node_factory.utils.ColorType;
import user_interface.single_instance.center.FullViewEvent;
import user_interface.single_instance.center.TileViewEvent;
import user_interface.single_instance.side.InfoListViewLEvent;
import user_interface.single_instance.side.InfoListViewREvent;
import user_interface.single_instance.top.TopMenuEvent;

public class MainStage extends Stage implements InstanceRepo {
    private final HBox hBox = NodeFactory.getHBox(ColorType.DEF, infoListViewL, tileView, infoListViewR);
    private final DataContextMenu dataContextMenu = new DataContextMenu();
    private final InfoContextMenu infoContextMenu = new InfoContextMenu();

    public void initialize() {
        logger.debug(this, "user_interface initialize start");
        setDefaultValues();
        initializeEvents();
        CommonUtil.updateNodeProperties();
        logger.debug(this, "user_interface initialize done");
    }
    private void setDefaultValues() {
        TitleBar titleBar = new TitleBar(this);
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
            target.set(mainListData.get(0));
            tileView.lookupAll(".scroll-bar").forEach(sb -> sb.setStyle("-fx-background-color: transparent;"));
            tileView.lookupAll(".increment-button").forEach(sb -> sb.setStyle("-fx-background-color: transparent;"));
            tileView.lookupAll(".decrement-button").forEach(sb -> sb.setStyle("-fx-background-color: transparent;"));
            tileView.lookupAll(".thumb").forEach(sb -> sb.setStyle("-fx-background-color: gray; -fx-background-insets: 0 4 0 4;"));
        });
        this.setOnCloseRequest(event -> {
            mainListData.writeToDisk();
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

    public DataContextMenu getDataContextMenu() {
        return dataContextMenu;
    }
    public InfoContextMenu getInfoContextMenu() {
        return infoContextMenu;
    }
}
