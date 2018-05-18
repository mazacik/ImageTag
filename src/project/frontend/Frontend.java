package project.frontend;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import project.backend.common.Serialization;
import project.backend.singleton.PreviewPaneBack;
import project.frontend.singleton.*;

public abstract class Frontend {
    private static final BorderPane mainBorderPane = new BorderPane();
    private static final Scene mainScene = new Scene(mainBorderPane);
    private static final SplitPane splitPane = new SplitPane();
    private static Stage mainStage = new Stage();


    public static void initialize() {
        mainStage.setTitle("JavaExplorer");
        mainStage.setMinWidth(800);
        mainStage.setMinHeight(600);
        mainStage.setMaximized(true);
        mainStage.setScene(mainScene);
        mainStage.setOnCloseRequest(event -> Serialization.writeToDisk());

        splitPane.getItems().addAll(LeftPaneFront.getInstance(), GalleryPaneFront.getInstance(), RightPaneFront.getInstance());
        splitPane.setDividerPositions(0.0, 1.0);

        mainBorderPane.setTop(TopPaneFront.getInstance());
        mainBorderPane.setCenter(splitPane);

        mainStage.show();
    }

    public static boolean isPreviewFullscreen() {
        return splitPane.getItems().contains(PreviewPaneFront.getInstance());
    }

    public static void swapImageDisplayMode() {
        GalleryPaneFront galleryPaneFront = GalleryPaneFront.getInstance();
        PreviewPaneFront previewPaneFront = PreviewPaneFront.getInstance();

        ObservableList<Node> splitPaneItems = splitPane.getItems();

        double[] dividerPositions = splitPane.getDividerPositions();
        if (splitPaneItems.contains(galleryPaneFront)) {
            splitPaneItems.set(splitPaneItems.indexOf(galleryPaneFront), previewPaneFront);
            previewPaneFront.setCanvasSize(galleryPaneFront.getWidth(), galleryPaneFront.getHeight());
            PreviewPaneBack.getInstance().draw();
        } else {
            splitPaneItems.set(splitPaneItems.indexOf(previewPaneFront), galleryPaneFront);
        }
        splitPane.setDividerPositions(dividerPositions);
    }

    public static Scene getMainScene() {
        return mainScene;
    }

    public static Stage getMainStage() {
        return mainStage;
    }
}
