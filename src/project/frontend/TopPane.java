package project.frontend;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.WindowEvent;
import project.backend.Database;
import project.backend.SharedBackend;

public class TopPane extends BorderPane {
    private static final Menu infoLabel = new Menu();

    TopPane() {
        MenuBar menuBar = new MenuBar();
        Menu menuFile = new Menu("File");
        MenuItem menuOpen = new MenuItem("Open");
        menuOpen.setOnAction(event -> SharedBackend.setDirectoryPath(new FileChooser().showOpenDialog(SharedFrontend.getMainStage()).getAbsolutePath()));
        MenuItem menuSave = new MenuItem("Save");
        menuSave.setOnAction(event -> Database.writeToDisk());
        MenuItem menuExit = new MenuItem("Exit");
        menuExit.setOnAction(event -> SharedFrontend.getMainStage().fireEvent(new WindowEvent(SharedFrontend.getMainStage(), WindowEvent.WINDOW_CLOSE_REQUEST)));
        menuFile.getItems().addAll(menuOpen, menuSave, menuExit);
        menuBar.getMenus().addAll(menuFile);
        setCenter(menuBar);

        MenuBar infoLabelArea = new MenuBar();
        infoLabelArea.getMenus().add(infoLabel);
        setRight(infoLabelArea);
    }

    public Menu getInfoLabel() {
        return infoLabel;
    }
}
