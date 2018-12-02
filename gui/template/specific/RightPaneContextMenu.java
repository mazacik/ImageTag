package gui.template.specific;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;

public class RightPaneContextMenu extends ContextMenu {
    private final MenuItem menuEdit = new MenuItem("Edit Tag");
    private final MenuItem menuRemove = new MenuItem("Remove Tag");

    public RightPaneContextMenu() {
        getItems().addAll(menuEdit, menuRemove);
    }

    public MenuItem getMenuEdit() {
        return menuEdit;
    }
    public MenuItem getMenuRemove() {
        return menuRemove;
    }
}
