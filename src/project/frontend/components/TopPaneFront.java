package project.frontend.components;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.stage.WindowEvent;
import project.backend.Backend;
import project.backend.Database;
import project.frontend.shared.Frontend;

public class TopPaneFront extends BorderPane {
    private final Menu infoLabel = new Menu();

    public TopPaneFront() {
        MenuBar menuBar = new MenuBar();
        Menu menuFile = new Menu("File");
        MenuItem menuSave = new MenuItem("Save");
        menuSave.setOnAction(event -> Database.writeToDisk());
        MenuItem menuExit = new MenuItem("Exit");
        menuExit.setOnAction(event -> Frontend.getMainStage().fireEvent(new WindowEvent(Frontend.getMainStage(), WindowEvent.WINDOW_CLOSE_REQUEST)));
        menuFile.getItems().addAll(menuSave, menuExit);

        Menu menuActions = new Menu("Actions");
        MenuItem menuUnselectAll = new MenuItem("Unselect All");
        menuUnselectAll.setOnAction(event -> Database.clearSelection());
        MenuItem menuRefresh = new MenuItem("Refresh");
        menuRefresh.setOnAction(event -> Frontend.refreshContent());
        menuActions.getItems().addAll(menuRefresh, menuUnselectAll);

        menuBar.getMenus().addAll(menuFile, menuActions);
        setCenter(menuBar);

        MenuBar infoLabelArea = new MenuBar();
        infoLabelArea.getMenus().add(infoLabel);
        infoLabelArea.setOnMouseEntered(event -> Backend.swapImageDisplayMode());
        infoLabelArea.setOnMouseExited(event -> Backend.swapImageDisplayMode());
        setRight(infoLabelArea);
    }

    public Menu getInfoLabel() {
        return infoLabel;
    }
}
