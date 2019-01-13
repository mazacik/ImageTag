package userinterface;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
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
    private final SplitPane splitPane = new SplitPane(infoListViewL, tileView, infoListViewR);
    private final DataContextMenu dataContextMenu = new DataContextMenu();
    private final InfoContextMenu infoContextMenu = new InfoContextMenu();

    public void initialize() {
        logger.debug(this, "userinterface initialize start");
        setDefaultValues();
        setDefaultValuesChildren();
        initializeEvents();
        logger.debug(this, "userinterface initialize done");
    }
    private void setDefaultValues() {
        this.setTitle("ImageTag");
        this.setScene(new Scene(new VBox(topMenu, splitPane)));
        splitPane.setBackground(CommonUtil.getBackgroundDefault());
        this.setMaximized(true);
        this.setOnShown(event -> {
            splitPane.lookupAll(".split-pane-divider").forEach(div -> div.setStyle("-fx-padding: 0; -fx-background-color: transparent;"));
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
    private void setDefaultValuesChildren() {
        splitPane.setDividerPositions(0.0, 1.0);
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
        final ObservableList<Node> splitPaneItems = splitPane.getItems();
        double[] dividerPositions = splitPane.getDividerPositions();
        if (this.isFullView()) {
            splitPaneItems.set(splitPaneItems.indexOf(fullView), tileView);
            tileView.adjustViewportToCurrentTarget();
        } else {
            splitPaneItems.set(splitPaneItems.indexOf(tileView), fullView);
            fullView.reload();
        }
        splitPane.setDividerPositions(dividerPositions);
        splitPane.lookupAll(".split-pane-divider").forEach(div -> div.setStyle("-fx-padding: 0; -fx-background-color: transparent;"));
    }
    public boolean isFullView() {
        return splitPane.getItems().contains(fullView);
    }

    public DataContextMenu getDataContextMenu() {
        return dataContextMenu;
    }
    public InfoContextMenu getInfoContextMenu() {
        return infoContextMenu;
    }
}
