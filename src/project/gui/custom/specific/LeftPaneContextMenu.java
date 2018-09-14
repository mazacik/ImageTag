package project.gui.custom.specific;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeCell;
import project.database.control.TagControl;

public class LeftPaneContextMenu extends ContextMenu {
    private final MenuItem menuEdit = new MenuItem("Edit Tag");
    private final MenuItem menuRemove = new MenuItem("Remove Tag");

    public LeftPaneContextMenu(TreeCell<ColoredText> source) {
        getItems().addAll(menuEdit, menuRemove);

        menuEdit.setOnAction(event -> TagControl.edit(TagControl.getTagObject(source)));
        menuRemove.setOnAction(event -> TagControl.remove(TagControl.getTagObject(source)));
    }
}
