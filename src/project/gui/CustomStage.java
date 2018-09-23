package project.gui;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import project.MainUtils;
import project.StringBank;
import project.database.loader.Serialization;
import project.gui.component.GUINode;
import project.gui.custom.generic.DataObjectContextMenu;
import project.settings.Settings;

public class CustomStage extends Stage implements MainUtils {
    private final SplitPane splitPane = new SplitPane(leftPane, galleryPane, rightPane);
    private final BorderPane mainPane = new BorderPane(splitPane, topPane, null, null, null);
    private final Scene mainScene = new Scene(mainPane);

    private final DataObjectContextMenu dataObjectContextMenu = new DataObjectContextMenu();

    public CustomStage() {

    }

    public void init() {
        this.setTitle(StringBank.APPLICATION_NAME.getValue());
        this.setMinWidth(Settings.getGuiMinWidth());
        this.setMinHeight(Settings.getGuiMinHeight());
        this.setMaximized(true);
        this.setScene(mainScene);
        this.setOnCloseRequest(event -> Serialization.writeToDisk());

        splitPane.setDividerPositions(0.0, 1.0);
    }

    public void swapDisplayMode() {
        final ObservableList<Node> splitPaneItems = splitPane.getItems();
        double[] dividerPositions = splitPane.getDividerPositions();
        if (splitPaneItems.contains(galleryPane)) {
            splitPaneItems.set(splitPaneItems.indexOf(galleryPane), previewPane);
            reloadControl.reload(GUINode.PREVIEWPANE);
        } else {
            splitPaneItems.set(splitPaneItems.indexOf(previewPane), galleryPane);
            reloadControl.reload(GUINode.PREVIEWPANE);
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
