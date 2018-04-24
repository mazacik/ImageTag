package project.backend;


import javafx.application.Platform;
import javafx.scene.control.TextInputDialog;
import javafx.stage.DirectoryChooser;
import project.backend.components.*;
import project.frontend.shared.Frontend;

import java.io.File;
import java.util.Optional;

import static project.frontend.shared.ImageDisplayMode.GALLERY;
import static project.frontend.shared.ImageDisplayMode.MAXIMIZED;

public class Backend {
    private static final TopPaneBack topPaneBack = new TopPaneBack();
    private static final LeftPaneBack leftPaneBack = new LeftPaneBack();
    private static final RightPaneBack rightPaneBack = new RightPaneBack();
    private static final GalleryPaneBack galleryPaneBack = new GalleryPaneBack();
    private static final PreviewPaneBack previewPaneBack = new PreviewPaneBack();

    static int GALLERY_ICON_SIZE = 150;
    static String DIRECTORY_PATH;

    public static void renameFile(DatabaseItem databaseItem) {
        if (databaseItem == null) return;
        TextInputDialog renamePrompt = new TextInputDialog();
        renamePrompt.setTitle("Rename file");
        renamePrompt.setHeaderText(null);
        renamePrompt.setGraphic(null);
        renamePrompt.setContentText("New name: ");
        Optional<String> result = renamePrompt.showAndWait();
        String oldName = databaseItem.getSimpleName();
        String newName = databaseItem.getSimpleName(); // nullpointer prevention
        if (result.isPresent())
            newName = result.get() + "." + databaseItem.getExtension();
        databaseItem.setSimpleName(newName);
        databaseItem.setFullPath(DIRECTORY_PATH + "/" + newName);
        databaseItem.getColoredText().setText(newName);
        new File(DIRECTORY_PATH + "/" + oldName).renameTo(new File(DIRECTORY_PATH + "/" + newName));
        new File(DIRECTORY_PATH + "/imagecache/" + oldName).renameTo(new File(DIRECTORY_PATH + "/imagecache/" + newName));
        Frontend.refreshContent();
    }

    static void initialize() {
        if (Backend.DIRECTORY_PATH == null) {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle("Select working directory");
            directoryChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
            File selectedDirectory = directoryChooser.showDialog(Frontend.getMainStage());
            if (selectedDirectory == null) {
                Platform.exit();
                return;
            }
            Backend.setDirectoryPath(selectedDirectory.getAbsolutePath());
        }
        Database.initilize();
        new DatabaseLoader().start();
    }

    public static void swapImageDisplayMode() {
        if (Frontend.getImageDisplayMode() == GALLERY) {
            Frontend.setImageDisplayMode(MAXIMIZED);
            Frontend.getPreviewPaneFront().setCanvasSize(Frontend.getGalleryPaneFront().getWidth(), Frontend.getGalleryPaneFront().getHeight());
            Frontend.getMainBorderPane().setCenter(Frontend.getPreviewPaneFront());
            Backend.getPreviewPaneBack().drawPreview();
        } else {
            Frontend.setImageDisplayMode(GALLERY);
            Frontend.getMainBorderPane().setCenter(Frontend.getGalleryPaneFront());
        }
    }

    public static TopPaneBack getTopPaneBack() {
        return topPaneBack;
    }

    public static LeftPaneBack getLeftPaneBack() {
        return leftPaneBack;
    }

    public static RightPaneBack getRightPaneBack() {
        return rightPaneBack;
    }

    public static GalleryPaneBack getGalleryPaneBack() {
        return galleryPaneBack;
    }

    public static PreviewPaneBack getPreviewPaneBack() {
        return previewPaneBack;
    }

    private static void setDirectoryPath(String directoryPath) {
        DIRECTORY_PATH = directoryPath;
    }
}
