package project.gui;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import project.control.MainControl;
import project.database.loader.Serialization;
import project.gui.component.GUINode;
import project.gui.component.gallerypane.GalleryPane;
import project.gui.component.leftpane.LeftPane;
import project.gui.component.previewpane.PreviewPane;
import project.gui.component.rightpane.RightPane;
import project.gui.component.toppane.TopPane;
import project.gui.custom.generic.DataObjectContextMenu;
import project.gui.event.global.ContextMenuEvent;
import project.gui.event.global.GlobalEvent;

public abstract class GUIInstance extends Stage {
    private static final DataObjectContextMenu dataObjectContextMenu = new DataObjectContextMenu();

    private static Stage _this;
    private static final SplitPane splitPane = new SplitPane(LeftPane.getInstance(), GalleryPane.getInstance(), RightPane.getInstance());
    private static final BorderPane mainPane = new BorderPane(splitPane, TopPane.getInstance(), null, null, null);
    private static final Scene mainScene = new Scene(mainPane);

    private static final Region galleryPane = GalleryPane.getInstance();
    private static final Region previewPane = PreviewPane.getInstance();
    private static final ObservableList<Node> splitPaneItems = splitPane.getItems();

    public static void initialize() {
        _this = new Stage();
        initializeComponents();
        initializeInstance();
        ContextMenuEvent.initialize();
        GlobalEvent.initialize();
    }
    private static void initializeComponents() {
        TopPane.initialize();
        LeftPane.initialize();
        GalleryPane.initialize();
        PreviewPane.initialize();
        RightPane.initialize();

        splitPane.setDividerPositions(0.0, 1.0);
    }
    private static void initializeInstance() {
        _this.setTitle("JavaExplorer");
        _this.setMinWidth(800);
        _this.setMinHeight(600);
        _this.setMaximized(true);
        _this.setScene(mainScene);
        _this.setOnCloseRequest(event -> Serialization.writeToDisk());
        //_this.show();
    }

    public static Stage getInstance() {
        return _this;
    }
    public static DataObjectContextMenu getDataObjectContextMenu() {
        return dataObjectContextMenu;
    }
    public static void swapDisplayMode() {
        double[] dividerPositions = splitPane.getDividerPositions();
        if (splitPaneItems.contains(galleryPane)) {
            splitPaneItems.set(splitPaneItems.indexOf(galleryPane), previewPane);
            MainControl.getReloadControl().reload(GUINode.PREVIEWPANE);
        } else {
            splitPaneItems.set(splitPaneItems.indexOf(previewPane), galleryPane);
            MainControl.getReloadControl().reload(GUINode.PREVIEWPANE);
        }
        splitPane.setDividerPositions(dividerPositions);
    }
    public static boolean isPreviewFullscreen() {
        return splitPaneItems.contains(previewPane);
    }
}
