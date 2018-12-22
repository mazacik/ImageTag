package gui.template.specific;

import gui.node.side.CustomTreeCell;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeCell;
import utils.MainUtil;

public class LeftPaneContextMenu extends ContextMenu implements MainUtil {
    private final MenuItem menuEdit = new MenuItem("Edit Tag");
    private final MenuItem menuRemove = new MenuItem("Remove Tag");

    public LeftPaneContextMenu(TreeCell<CustomTreeCell> source) {
        getItems().addAll(menuEdit, menuRemove);

        menuEdit.setOnAction(event -> mainListInfo.edit(mainListInfo.getTagObject(source)));
        menuRemove.setOnAction(event -> mainListInfo.remove(mainListInfo.getTagObject(source)));
    }
}
