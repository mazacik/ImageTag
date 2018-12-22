package gui;

import gui.event.center.FullViewEvent;
import gui.event.center.TileViewEvent;
import gui.event.global.GlobalContextMenuEvent;
import gui.event.global.GlobalEvent;
import gui.event.side.InfoListRightEvent;
import gui.event.toolbar.ToolbarEvent;
import gui.template.generic.DataObjectContextMenu;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import settings.SettingsNamespace;
import utils.MainUtil;

public class MainStage extends Stage implements MainUtil {
    private final SplitPane splitPane = new SplitPane(infoListL, tileView, infoListR);
    private final DataObjectContextMenu dataObjectContextMenu = new DataObjectContextMenu();

    public void initialize() {
        logger.debug(this, "gui initialize start");
        setDefaultValues();
        setDefaultValuesChildren();
        initializeEvents();
        logger.debug(this, "gui initialize done");
    }
    private void setDefaultValues() {
        this.setTitle("ImageTag");
        this.setMinWidth(settings.valueOf(SettingsNamespace.MAINSCENE_WIDTH));
        this.setMinHeight(settings.valueOf(SettingsNamespace.MAINSCENE_HEIGHT));
        this.setScene(new Scene(new VBox(toolbar, splitPane)));
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
        new GlobalContextMenuEvent();
        new GlobalEvent();

        new ToolbarEvent();
        new TileViewEvent();
        new FullViewEvent();
        new InfoListRightEvent();
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

    public DataObjectContextMenu getDataObjectContextMenu() {
        return dataObjectContextMenu;
    }
}
