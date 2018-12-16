package gui;

import database.loader.Serialization;
import gui.event.center.TileViewEvent;
import gui.event.global.GlobalContextMenuEvent;
import gui.event.global.GlobalEvent;
import gui.event.center.FullViewEvent;
import gui.event.side.InfoListRightEvent;
import gui.event.toolbar.ToolbarEvent;
import gui.template.generic.DataObjectContextMenu;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import settings.Settings;
import utils.MainUtil;

public class MainStage extends Stage implements MainUtil {
    private final SplitPane splitPane = new SplitPane(infoListL, tileView, infoListR);
    private final VBox mainPane = new VBox(toolbar, splitPane);
    private final Scene mainScene = new Scene(mainPane);

    private DataObjectContextMenu dataObjectContextMenu;

    public void init() {
        logger.out("gui init start", this.getClass());
        this.setTitle("ImageTag");
        this.setMinWidth(Settings.getGuiMinWidth());
        this.setMinHeight(Settings.getGuiMinHeight());
        this.setMaximized(true);
        this.setScene(mainScene);
        this.setOnCloseRequest(event -> {
            Serialization.writeToDisk();
            logger.out("application exit", this.getClass());
        });

        mainPane.setSpacing(2);
        splitPane.setPadding(new Insets(2));
        splitPane.setDividerPositions(0.0, 1.0);

        this.dataObjectContextMenu = new DataObjectContextMenu();
        this.initEvents();
        logger.out("gui init done", this.getClass());
    }
    private void initEvents() {
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
