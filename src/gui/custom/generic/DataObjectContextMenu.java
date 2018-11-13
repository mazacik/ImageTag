package gui.custom.generic;

import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseEvent;

public class DataObjectContextMenu extends ContextMenu {
    private final MenuItem menuCopy = new MenuItem("Copy Filename");
    private final MenuItem menuDelete = new MenuItem("Delete File");

    public DataObjectContextMenu() {
        this.getItems().addAll(menuCopy, menuDelete);
    }

    public void show(Node anchor, double screenX, double screenY) {
        super.show(anchor, screenX, screenY);
    }
    public void show(Node anchor, MouseEvent event) {
        super.show(anchor, event.getScreenX(), event.getScreenY());
    }

    public MenuItem getMenuCopy() {
        return menuCopy;
    }
    public MenuItem getMenuDelete() {
        return menuDelete;
    }
}
