package project.backend;


import javafx.application.Platform;
import javafx.stage.DirectoryChooser;
import project.frontend.SharedFrontend;

import java.io.File;

import static project.frontend.ImageDisplayMode.GALLERY;
import static project.frontend.ImageDisplayMode.MAXIMIZED;

public class SharedBackend {
    static int GALLERY_ICON_SIZE = 150;
    static String DIRECTORY_PATH;

    static void initialize() {
        if (SharedBackend.DIRECTORY_PATH == null) {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle("Select working directory");
            directoryChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
            SharedBackend.setDirectoryPath(directoryChooser.showDialog(SharedFrontend.getMainStage()).getAbsolutePath());
            if (SharedBackend.DIRECTORY_PATH == null)
                Platform.exit();
        }
        Database.initilize();
        new DatabaseLoader().start();
    }

    public static void swapImageDisplayMode() {
        if (SharedFrontend.getImageDisplayMode() == GALLERY) {
            SharedFrontend.setImageDisplayMode(MAXIMIZED);
            SharedFrontend.getPreviewPane().setCanvasSize(SharedFrontend.getGalleryPane().getWidth(), SharedFrontend.getGalleryPane().getHeight());
            SharedFrontend.getMainBorderPane().setCenter(SharedFrontend.getPreviewPane());
            SharedFrontend.getPreviewPane().drawPreview();
        } else {
            SharedFrontend.setImageDisplayMode(GALLERY);
            SharedFrontend.getMainBorderPane().setCenter(SharedFrontend.getGalleryPane());
        }
    }

    public static void setDirectoryPath(String directoryPath) {
        DIRECTORY_PATH = directoryPath;
    }
}
