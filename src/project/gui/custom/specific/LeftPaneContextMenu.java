package project.gui.custom.specific;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeCell;
import project.database.control.TagElementControl;
import project.gui.component.leftpane.ColoredText;

public class LeftPaneContextMenu extends ContextMenu {
    private final MenuItem menuEdit = new MenuItem("Edit Tag");
    private final MenuItem menuRemove = new MenuItem("Remove Tag");

    public LeftPaneContextMenu(TreeCell<ColoredText> source) {
        getItems().addAll(menuEdit, menuRemove);

        menuEdit.setOnAction(event -> TagElementControl.edit(TagElementControl.getTagElement(source)));
        menuRemove.setOnAction(event -> TagElementControl.remove(TagElementControl.getTagElement(source)));
    }
}
