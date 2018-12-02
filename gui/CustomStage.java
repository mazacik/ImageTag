package gui;

import database.loader.Serialization;
import gui.component.NodeEnum;
import gui.event.gallerypane.GalleryPaneEvent;
import gui.event.global.ContextMenuEvent;
import gui.event.global.GlobalEvent;
import gui.event.previewpane.PreviewPaneEvent;
import gui.event.rightpane.RightPaneEvent;
import gui.event.toppane.TopPaneEvent;
import gui.template.generic.DataObjectContextMenu;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import settings.Settings;
import utils.MainUtil;

public class CustomStage extends Stage implements MainUtil {
    private final SplitPane splitPane = new SplitPane(leftPane, galleryPane, rightPane);
    private final BorderPane mainPane = new BorderPane(splitPane, topPane, null, null, null);
    private final Scene mainScene = new Scene(mainPane);

    private DataObjectContextMenu dataObjectContextMenu;

    public CustomStage() {

    }

    public void init() {
        log.out("gui init start", this.getClass());
        this.setTitle("ImageTag");
        this.setMinWidth(Settings.getGuiMinWidth());
        this.setMinHeight(Settings.getGuiMinHeight());
        this.setMaximized(true);
        this.setScene(mainScene);
        this.setOnCloseRequest(event -> {
            Serialization.writeToDisk();
            log.out("application exit", this.getClass());
        });

        mainPane.setPadding(new Insets(2));
        splitPane.setPadding(new Insets(2));
        splitPane.setDividerPositions(0.0, 1.0);

        this.dataObjectContextMenu = new DataObjectContextMenu();
        this.initEvents();
        log.out("gui init done", this.getClass());
    }
    private void initEvents() {
        new ContextMenuEvent();
        new GlobalEvent();

        new TopPaneEvent();
        new GalleryPaneEvent();
        new PreviewPaneEvent();
        new RightPaneEvent();
    }

    public void swapDisplayMode() {
        final ObservableList<Node> splitPaneItems = splitPane.getItems();
        double[] dividerPositions = splitPane.getDividerPositions();
        if (this.isPreviewFullscreen()) {
            splitPaneItems.set(splitPaneItems.indexOf(previewPane), galleryPane);
            galleryPane.adjustViewportToCurrentFocus();
            //reload.queue(NodeEnum.GALLERYPANE);
        } else {
            splitPaneItems.set(splitPaneItems.indexOf(galleryPane), previewPane);
            reload.queue(NodeEnum.PREVIEWPANE);
        }
        splitPane.setDividerPositions(dividerPositions);
    }
    public boolean isPreviewFullscreen() {
        return splitPane.getItems().contains(previewPane);
    }

    public DataObjectContextMenu getDataObjectContextMenu() {
        return dataObjectContextMenu;
    }
}
