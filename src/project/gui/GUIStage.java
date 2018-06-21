package project.gui;

import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import project.database.ItemDatabase;
import project.database.loader.Serialization;
import project.gui.component.gallery.GalleryPane;
import project.gui.component.left.LeftPane;
import project.gui.component.left.LeftPaneBack;
import project.gui.component.preview.PreviewPane;
import project.gui.component.preview.PreviewPaneBack;
import project.gui.component.right.RightPane;
import project.gui.component.right.RightPaneBack;
import project.gui.component.top.TopPane;

public class GUIStage extends Stage {
    private static TopPane topPane;
    private static LeftPane leftPane;
    private static GalleryPane galleryPane;
    private static PreviewPane previewPane;
    private static RightPane rightPane;

    private static SplitPane splitPane;
    private static BorderPane mainPane;
    private static Scene mainScene;

    public GUIStage() {
        initializeComponents();
        initializeStageProperties();
    }

    private void initializeComponents() {
        topPane = new TopPane();
        leftPane = new LeftPane();
        galleryPane = new GalleryPane();
        previewPane = new PreviewPane();
        rightPane = new RightPane();

        splitPane = new SplitPane(leftPane, galleryPane, rightPane);
        splitPane.setDividerPositions(0.0, 1.0);
        mainPane = new BorderPane(splitPane, topPane, null, null, null);
        mainScene = new Scene(mainPane);
    }
    private void initializeStageProperties() {
        setTitle("JavaExplorer");
        setMinWidth(800);
        setMinHeight(600);
        setMaximized(true);
        setScene(mainScene);
        setOnCloseRequest(event -> Serialization.writeToDisk());
        show();
    }

    public static void refresh(boolean sortDatabase) {
        if (sortDatabase) ItemDatabase.sort();
        LeftPaneBack.getInstance().reloadContent();
        RightPaneBack.getInstance().reloadContent();
        GalleryPaneBack.getInstance().reloadContent();
        PreviewPaneBack.getInstance().reloadContent();
    }

    public static TopPane getTopPane() {
        return topPane;
    }
    public static LeftPane getLeftPane() {
        return leftPane;
    }
    public static GalleryPane getGalleryPane() {
        return galleryPane;
    }
    public static PreviewPane getPreviewPane() {
        return previewPane;
    }
    public static RightPane getRightPane() {
        return rightPane;
    }
    public static SplitPane getSplitPane() {
        return splitPane;
    }
}
