package project.frontend.shared;

import javafx.application.Platform;
import javafx.event.Event;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import project.backend.shared.Database;
import project.frontend.components.*;

import java.util.Optional;

public class Frontend {
    private static final BorderPane mainBorderPane = new BorderPane();
    private static final Scene mainScene = new Scene(mainBorderPane);
    private static final TopPaneFront topPane = new TopPaneFront();
    private static final NamePaneFront namePane = new NamePaneFront();
    private static final TagPaneFront tagPane = new TagPaneFront();
    private static final RightPaneFront rightPane = new RightPaneFront();
    private static final GalleryPaneFront galleryPane = new GalleryPaneFront();
    private static final PreviewPaneFront previewPane = new PreviewPaneFront();
    private static final SplitPane splitPane = new SplitPane();
    private static Stage mainStage = null;

    public static void initialize(Stage primaryStage) {
        mainStage = primaryStage;
        mainStage.setMinWidth(800);
        mainStage.setMinHeight(600);
        mainStage.setMaximized(true);
        mainStage.setOnCloseRequest(Frontend::showApplicationExitPrompt);
        mainStage.setScene(mainScene);

        splitPane.getItems().addAll(tagPane, galleryPane, rightPane);
        splitPane.setDividerPositions(0.0, 1.0);

        mainBorderPane.setTop(topPane);
        mainBorderPane.setCenter(splitPane);

        mainStage.show();
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
            Database.writeToDisk();
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

    public static GalleryPaneFront getGalleryPane() {
        return galleryPane;
    }

    public static Scene getMainScene() {
        return mainScene;
    }

    public static NamePaneFront getNamePane() {
        return namePane;
    }

    public static TagPaneFront getTagPane() {
        return tagPane;
    }

    public static BorderPane getMainBorderPane() {
        return mainBorderPane;
    }

    public static PreviewPaneFront getPreviewPane() {
        return previewPane;
    }

    public static RightPaneFront getRightPane() {
        return rightPane;
    }

    public static TopPaneFront getTopPane() {
        return topPane;
    }

    public static Stage getMainStage() {
        return mainStage;
    }
}
