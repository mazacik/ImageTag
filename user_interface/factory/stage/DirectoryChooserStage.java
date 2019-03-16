package user_interface.factory.stage;

import javafx.stage.DirectoryChooser;
import javafx.stage.Window;

import java.io.File;

public class DirectoryChooserStage {
    private final DirectoryChooser directoryChooser = new DirectoryChooser();
    private Window ownerWindow;

    public DirectoryChooserStage(Window ownerWindow, String title, String initialDirectory) {
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
