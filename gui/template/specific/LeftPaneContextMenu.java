package gui.template.specific;

import gui.node.ColorText;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeCell;
import utils.MainUtil;

public class LeftPaneContextMenu extends ContextMenu implements MainUtil {
    private final MenuItem menuEdit = new MenuItem("Edit Tag");
    private final MenuItem menuRemove = new MenuItem("Remove Tag");

    public LeftPaneContextMenu(TreeCell<ColorText> source) {
        getItems().addAll(menuEdit, menuRemove);

        menuEdit.setOnAction(event -> mainTags.edit(mainTags.getTagObject(source)));
        menuRemove.setOnAction(event -> mainTags.remove(mainTags.getTagObject(source)));
    }
}
