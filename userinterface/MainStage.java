package userinterface;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import settings.SettingsNamespace;
import userinterface.node.GlobalEvent;
import userinterface.node.center.FullViewEvent;
import userinterface.node.center.TileViewEvent;
import userinterface.node.side.InfoListViewLEvent;
import userinterface.node.side.InfoListViewREvent;
import userinterface.node.topmenu.TopMenuEvent;
import userinterface.template.specific.DataContextMenu;
import userinterface.template.specific.InfoContextMenu;
import utils.MainUtil;

public class MainStage extends Stage implements MainUtil {
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
        this.setMinWidth(settings.valueOf(SettingsNamespace.MAINSCENE_WIDTH));
        this.setMinHeight(settings.valueOf(SettingsNamespace.MAINSCENE_HEIGHT));
        this.setScene(new Scene(new VBox(topMenu, splitPane)));
        this.setMaximized(true);
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
            tileView.adjustViewportToCurrentFocus();
        } else {
            splitPaneItems.set(splitPaneItems.indexOf(tileView), fullView);
            fullView.reload();
        }
        splitPane.setDividerPositions(dividerPositions);
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
