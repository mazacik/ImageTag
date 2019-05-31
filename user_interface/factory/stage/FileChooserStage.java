package user_interface.factory.stage;

import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;

public class FileChooserStage {
    private final FileChooser fileChooser = new FileChooser();
    private Window ownerWindow;

    public FileChooserStage(Window ownerWindow, String title, String initialDirectory) {
        this.ownerWindow = ownerWindow;
        fileChooser.setTitle(title);
        fileChooser.setInitialDirectory(new File(initialDirectory));
    }

    public String getResultValue() {
        File selectedDirectory = fileChooser.showOpenDialog(ownerWindow);
        try {
            return selectedDirectory.getAbsolutePath();
        } catch (Exception e) {
            return "";
        }
    }
}
