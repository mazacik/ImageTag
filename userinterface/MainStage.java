package userinterface;

import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import userinterface.node.center.FullViewEvent;
import userinterface.node.center.TileViewEvent;
import userinterface.node.side.InfoListViewLEvent;
import userinterface.node.side.InfoListViewREvent;
import userinterface.node.topmenu.TopMenuEvent;
import userinterface.template.specific.DataContextMenu;
import userinterface.template.specific.InfoContextMenu;
import utils.CommonUtil;
import utils.InstanceRepo;

public class MainStage extends Stage implements InstanceRepo {
    private final HBox hBox = new HBox(infoListViewL, tileView, infoListViewR);
    private final DataContextMenu dataContextMenu = new DataContextMenu();
    private final InfoContextMenu infoContextMenu = new InfoContextMenu();

    public void initialize() {
        logger.debug(this, "userinterface initialize start");
        setDefaultValues();
        initializeEvents();
        logger.debug(this, "userinterface initialize done");
    }
    private void setDefaultValues() {
        this.setTitle("ImageTag");
        this.setScene(new Scene(new VBox(topMenu, hBox)));
        hBox.setBackground(CommonUtil.getBackgroundDefault());
        HBox.setHgrow(infoListViewL, Priority.ALWAYS);
        HBox.setHgrow(infoListViewR, Priority.ALWAYS);
        this.setMaximized(true);
        this.setOnShown(event -> {
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
