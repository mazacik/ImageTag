package project.backend;

import javafx.application.Platform;
import javafx.stage.DirectoryChooser;
import project.Main;
import project.backend.common.Keybinds;
import project.backend.components.GalleryPaneBack;
import project.backend.components.LeftPaneBack;
import project.backend.components.PreviewPaneBack;
import project.backend.database.Database;
import project.backend.database.DatabaseItem;
import project.frontend.shared.Frontend;

import java.io.File;
import java.util.Comparator;

public class Backend {
    public static void initialize() {
        if (Main.DIRECTORY_PATH == null || Main.DIRECTORY_PATH.isEmpty())
            Backend.setDirectoryPath(showDirectoryPicker());

        Keybinds.initialize();
        Database.initialize();
    }

    public static void reloadContent() {
        Database.sort(); //todo: after moving lines below, just change everything from Backend.reloadContent() to Database.sort()
        LeftPaneBack.reloadContent(); //todo: move
        if (Frontend.isPreviewFullscreen()) //todo: move
            PreviewPaneBack.draw();
        else
            GalleryPaneBack.reloadContent();
    }

    private static String showDirectoryPicker() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select working directory");
        directoryChooser.setInitialDirectory(new File("C:/"));
        File selectedDirectory = directoryChooser.showDialog(Frontend.getMainStage());
        if (selectedDirectory == null) {
            Platform.exit();
            System.exit(0);
        }
        return selectedDirectory.getAbsolutePath();
    }

    private static void setDirectoryPath(String directoryPath) {
        Main.DIRECTORY_PATH = directoryPath;
    }
}
