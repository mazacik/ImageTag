package project.frontend.components;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.stage.WindowEvent;
import project.backend.shared.Backend;
import project.backend.shared.Database;
import project.frontend.shared.Frontend;

public class TopPaneFront extends BorderPane {
  private final Menu infoLabel = new Menu();

  public TopPaneFront() {
    Menu menuFile = new Menu("File");
    MenuItem menuSave = new MenuItem("Save");
    menuSave.setOnAction(event -> Database.writeToDisk());
    MenuItem menuExit = new MenuItem("Exit");
    menuExit.setOnAction(
        event ->
            Frontend.getMainStage()
                .fireEvent(
                    new WindowEvent(Frontend.getMainStage(), WindowEvent.WINDOW_CLOSE_REQUEST)));
    menuFile.getItems().addAll(menuSave, menuExit);

    Menu menuActions = new Menu("Actions");
    MenuItem menuUnselectAll = new MenuItem("Unselect All");
    menuUnselectAll.setOnAction(event -> Database.clearSelection());
    MenuItem menuRefresh = new MenuItem("Refresh");
    menuRefresh.setOnAction(event -> Backend.refreshContent());
    menuActions.getItems().addAll(menuRefresh, menuUnselectAll);

    Menu menuFilter = new Menu("Filter");
    MenuItem menuReset = new MenuItem("Reset");
    menuReset.setOnAction(
        event -> {
          Database.getTagsWhitelist().clear();
          Database.getTagsBlacklist().clear();
          Database.filterByTags();
          Backend.refreshContent();
        });
    MenuItem menuUntaggedOnly = new MenuItem("Untagged only");
    menuUntaggedOnly.setOnAction(
        event -> {
          Database.getTagsWhitelist().clear();
          Database.getTagsBlacklist().clear();
          Database.getTagsBlacklist().addAll(Database.getTagDatabase());
          Database.filterByTags();
          Backend.refreshContent();
        });
    menuFilter.getItems().addAll(menuReset, menuUntaggedOnly);

    MenuBar menuBar = new MenuBar();
    menuBar.getMenus().addAll(menuFile, menuActions, menuFilter);
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
