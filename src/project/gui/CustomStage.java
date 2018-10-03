package project.gui;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import project.database.loader.Serialization;
import project.gui.component.GUINode;
import project.gui.custom.generic.DataObjectContextMenu;
import project.gui.event.gallerypane.GalleryPaneEvent;
import project.gui.event.global.ContextMenuEvent;
import project.gui.event.global.GlobalEvent;
import project.gui.event.previewpane.PreviewPaneEvent;
import project.gui.event.rightpane.RightPaneEvent;
import project.gui.event.toppane.TopPaneEvent;
import project.settings.Settings;
import project.utils.MainUtil;

public class CustomStage extends Stage implements MainUtil {
    private final SplitPane splitPane = new SplitPane(leftPane, galleryPane, rightPane);
    private final BorderPane mainPane = new BorderPane(splitPane, topPane, null, null, null);
    private final Scene mainScene = new Scene(mainPane);

    private DataObjectContextMenu dataObjectContextMenu;

    public CustomStage() {

    }

    public void init() {
        log.out("gui init start", this.getClass());
        this.setTitle("JavaExplorer");
        this.setMinWidth(Settings.getGuiMinWidth());
        this.setMinHeight(Settings.getGuiMinHeight());
        this.setMaximized(true);
        this.setScene(mainScene);
        this.setOnCloseRequest(event -> {
            Serialization.writeToDisk();
            log.out("application exit", this.getClass());
        });

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
        if (splitPaneItems.contains(galleryPane)) {
            splitPaneItems.set(splitPaneItems.indexOf(galleryPane), previewPane);
            reload.queue(GUINode.PREVIEWPANE);
        } else {
            splitPaneItems.set(splitPaneItems.indexOf(previewPane), galleryPane);
            reload.queue(GUINode.PREVIEWPANE);
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
