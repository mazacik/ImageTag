package project.frontend.shared;

import javafx.event.Event;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import project.backend.shared.Backend;
import project.backend.shared.Database;
import project.backend.shared.DatabaseItem;
import project.frontend.components.*;

import java.util.Comparator;
import java.util.Optional;
import java.util.Random;

import static project.frontend.shared.ImageDisplayMode.GALLERY;

public class Frontend {
    private static final BorderPane mainBorderPane = new BorderPane();
    private static final Scene mainScene = new Scene(mainBorderPane);
    private static final TopPaneFront topPane = new TopPaneFront();
    private static final LeftPaneFront leftPane = new LeftPaneFront();
    private static final RightPaneFront rightPane = new RightPaneFront();
    private static final GalleryPaneFront galleryPane = new GalleryPaneFront();
    private static final PreviewPaneFront previewPane = new PreviewPaneFront();
    private static Stage mainStage = null;
    private static ImageDisplayMode imageDisplayMode = GALLERY;

    public static void refreshContent() {
        Database.getFilteredItems().sort(Comparator.comparing(DatabaseItem::getSimpleName));
        Database.getSelectedItems().sort(Comparator.comparing(DatabaseItem::getSimpleName));
        Database.getItemDatabase().sort(Comparator.comparing(DatabaseItem::getSimpleName));
        Backend.getLeftPane().refreshContent();
        Backend.getGalleryPane().refreshContent();
    }

    public static void initialize(Stage primaryStage) {
        mainStage = primaryStage;
        mainStage.setMinWidth(800);
        mainStage.setMinHeight(600);
        mainStage.setMaximized(true);
        mainStage.setOnCloseRequest(Frontend::showApplicationExitPrompt);
        mainStage.setScene(mainScene);

        mainScene.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case R:
                    Database.clearSelection();
                    Database.addToSelection(Database.getFilteredItems().get(new Random().nextInt(Database.getFilteredItems().size())));
                    break;
                case F12:
                    Backend.swapImageDisplayMode();
                    break;
                default:
                    break;
            }
        });

        mainBorderPane.setTop(topPane);
        mainBorderPane.setLeft(leftPane);
        mainBorderPane.setCenter(galleryPane);
        mainBorderPane.setRight(rightPane);

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
        } else if (result.get() == buttonTypeCancel) {
            event.consume();
        }
    }

    public static BorderPane getMainBorderPane() {
        return mainBorderPane;
    }

    public static PreviewPaneFront getPreviewPane() {
        return previewPane;
    }

    public static ImageDisplayMode getImageDisplayMode() {
        return imageDisplayMode;
    }

    public static GalleryPaneFront getGalleryPane() {
        return galleryPane;
    }

    public static LeftPaneFront getLeftPane() {
        return leftPane;
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

    public static void setImageDisplayMode(ImageDisplayMode imageDisplayMode) {
        Frontend.imageDisplayMode = imageDisplayMode;
    }
}
