package project.gui.custom.generic;

import javafx.stage.DirectoryChooser;
import javafx.stage.Window;

import java.io.File;

public class DirectoryChooserWindow {
    private final DirectoryChooser directoryChooser = new DirectoryChooser();
    private Window ownerWindow;

    public DirectoryChooserWindow(Window ownerWindow, String title, String initialDirectory) {
        this.ownerWindow = ownerWindow;
        directoryChooser.setTitle(title);
        directoryChooser.setInitialDirectory(new File(initialDirectory));
    }

    public String getResultValue() {
        File selectedDirectory = directoryChooser.showDialog(ownerWindow);
        try {
            return selectedDirectory.getAbsolutePath();
        } catch (Exception e) {
            return "";
        }
    }
}
