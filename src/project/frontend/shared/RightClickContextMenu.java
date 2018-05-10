package project.frontend.shared;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import project.backend.shared.Backend;
import project.backend.shared.Database;

public class RightClickContextMenu extends ContextMenu {
    public RightClickContextMenu() {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem menuRename = new MenuItem("Rename");
        menuRename.setOnAction(event -> Backend.renameFile(Database.getSelectedItem()));
        contextMenu.getItems().add(menuRename);
    }
}
