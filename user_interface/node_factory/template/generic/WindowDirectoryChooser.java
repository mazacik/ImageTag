package user_interface.node_factory.template.generic;

import javafx.stage.DirectoryChooser;
import javafx.stage.Window;

import java.io.File;

public class WindowDirectoryChooser {
    private final DirectoryChooser directoryChooser = new DirectoryChooser();
    private Window ownerWindow;

    public WindowDirectoryChooser(Window ownerWindow, String title, String initialDirectory) {
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
