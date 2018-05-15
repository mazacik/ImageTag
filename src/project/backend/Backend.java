package project.backend;

import javafx.application.Platform;
import javafx.stage.DirectoryChooser;
import project.backend.common.Keybinds;
import project.backend.listener.Listener;
import project.backend.database.Database;
import project.backend.database.DatabaseLoader;
import project.backend.singleton.GalleryPaneBack;
import project.backend.singleton.LeftPaneBack;
import project.backend.singleton.PreviewPaneBack;
import project.frontend.Frontend;

import java.io.File;

public abstract class Backend {
    public static String DIRECTORY_PATH = "C:/abc/dnnsfw";

    public static void initialize() {
        if (DIRECTORY_PATH == null || DIRECTORY_PATH.isEmpty())
            Backend.setDirectoryPath(showDirectoryChooser());

        Keybinds.initialize();
        Listener.getInstance(); /* singleton initialization over lazy construction */
        new DatabaseLoader().start();
    }

    public static void reloadContent(boolean sortDatabase) {
        if (sortDatabase)
            Database.sort();
        LeftPaneBack.getInstance().reloadContent();
        PreviewPaneBack.getInstance().draw();
        GalleryPaneBack.getInstance().reloadContent();
    }

    private static String showDirectoryChooser() {
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
        DIRECTORY_PATH = directoryPath;
    }
}
