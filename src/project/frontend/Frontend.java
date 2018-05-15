package project.frontend;

import javafx.application.Platform;
import javafx.event.Event;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import project.backend.common.Serialization;
import project.backend.singleton.PreviewPaneBack;
import project.frontend.singleton.*;

import java.util.Optional;

public abstract class Frontend {
    private static final BorderPane mainBorderPane = new BorderPane();
    private static final Scene mainScene = new Scene(mainBorderPane);
    private static final SplitPane splitPane = new SplitPane();
    private static Stage mainStage = null;

    public static void initialize(Stage primaryStage) {
        mainStage = primaryStage;
        mainStage.setMinWidth(800);
        mainStage.setMinHeight(600);
        mainStage.setMaximized(true);
        mainStage.setOnCloseRequest(Frontend::showApplicationExitPrompt);
        mainStage.setScene(mainScene);

        splitPane.getItems().addAll(LeftPaneFront.getInstance(), GalleryPaneFront.getInstance(), RightPaneFront.getInstance());
        splitPane.setDividerPositions(0.0, 1.0);

        mainBorderPane.setTop(TopPaneFront.getInstance());
        mainBorderPane.setCenter(splitPane);

        mainStage.show();
    }

    public static boolean isPreviewFullscreen() {
        return Frontend.getSplitPane().getItems().contains(PreviewPaneFront.getInstance());
    }

    public static void swapImageDisplayMode() {
        double[] dividerPositions = getSplitPane().getDividerPositions();
        if (getSplitPane().getItems().contains(GalleryPaneFront.getInstance())) {
            getSplitPane().getItems().set(1, PreviewPaneFront.getInstance());
            PreviewPaneFront.getInstance().setCanvasSize(GalleryPaneFront.getInstance().getWidth(), GalleryPaneFront.getInstance().getHeight());
            PreviewPaneBack.getInstance().draw();
        } else {
            getSplitPane().getItems().set(1, GalleryPaneFront.getInstance());
        }
        getSplitPane().setDividerPositions(dividerPositions);
    }

    private static void showApplicationExitPrompt(Event event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Exit application?");
        alert.setHeaderText(null);
        alert.setGraphic(null);
        alert.setContentText("All unsaved changes will be lost!");

        ButtonType buttonTypeSave = new ButtonType("Save and exit");
        ButtonType buttonTypeExit = new ButtonType("Exit without saving");
        ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(buttonTypeSave, buttonTypeExit, buttonTypeCancel);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == buttonTypeSave) {
            Serialization.writeToDisk();
            Platform.exit();
        } else if (result.get() == buttonTypeExit) {
            Platform.exit();
        } else if (result.get() == buttonTypeCancel) {
            event.consume();
        }
    }

    public static SplitPane getSplitPane() {
        return splitPane;
    }

    public static Scene getMainScene() {
        return mainScene;
    }

    public static BorderPane getMainBorderPane() {
        return mainBorderPane;
    }

    public static Stage getMainStage() {
        return mainStage;
    }
}
