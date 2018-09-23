package project.gui.custom.specific;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeCell;
import project.MainUtils;
import project.gui.component.leftpane.ColoredText;

public class LeftPaneContextMenu extends ContextMenu implements MainUtils {
    private final MenuItem menuEdit = new MenuItem("Edit Tag");
    private final MenuItem menuRemove = new MenuItem("Remove Tag");

    public LeftPaneContextMenu(TreeCell<ColoredText> source) {
        getItems().addAll(menuEdit, menuRemove);

        menuEdit.setOnAction(event -> tagControl.edit(tagControl.getTagObject(source)));
        menuRemove.setOnAction(event -> tagControl.remove(tagControl.getTagObject(source)));
    }
}
