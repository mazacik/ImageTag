package project.gui.component.previewpane;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;

public class RightClickMenu extends ContextMenu {
    private static MenuItem menuCopy = new MenuItem("Copy Name");
    private static MenuItem menuDelete = new MenuItem("Delete Selection");

    public RightClickMenu() {
        getItems().addAll(menuCopy, menuDelete);
    }
    public static MenuItem getMenuCopy() {
        return menuCopy;
    }
    public static MenuItem getMenuDelete() {
        return menuDelete;
    }
}
