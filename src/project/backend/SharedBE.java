package project.backend;


import javafx.application.Platform;
import javafx.scene.control.TextInputDialog;
import javafx.stage.DirectoryChooser;
import project.frontend.SharedFE;

import java.io.File;
import java.util.Optional;

import static project.frontend.ImageDisplayMode.GALLERY;
import static project.frontend.ImageDisplayMode.MAXIMIZED;

public class SharedBE {
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
        SharedFE.refreshContent();
    }

    static void initialize() {
        if (SharedBE.DIRECTORY_PATH == null) {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle("Select working directory");
            directoryChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
            File selectedDirectory = directoryChooser.showDialog(SharedFE.getMainStage());
            if (selectedDirectory == null) {
                Platform.exit();
                return;
            }
            SharedBE.setDirectoryPath(selectedDirectory.getAbsolutePath());
        }
        Database.initilize();
        new DatabaseLoader().start();
    }

    public static void swapImageDisplayMode() {
        if (SharedFE.getImageDisplayMode() == GALLERY) {
            SharedFE.setImageDisplayMode(MAXIMIZED);
            SharedFE.getPreviewPane().setCanvasSize(SharedFE.getGalleryPane().getWidth(), SharedFE.getGalleryPane().getHeight());
            SharedFE.getMainBorderPane().setCenter(SharedFE.getPreviewPane());
            SharedFE.getPreviewPane().drawPreview();
        } else {
            SharedFE.setImageDisplayMode(GALLERY);
            SharedFE.getMainBorderPane().setCenter(SharedFE.getGalleryPane());
        }
    }

    public static void setDirectoryPath(String directoryPath) {
        DIRECTORY_PATH = directoryPath;
    }
}
