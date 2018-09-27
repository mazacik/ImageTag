package project.gui.custom.specific;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeCell;
import project.gui.component.leftpane.ColoredText;
import project.utils.MainUtil;

public class LeftPaneContextMenu extends ContextMenu implements MainUtil {
    private final MenuItem menuEdit = new MenuItem("Edit Tag");
    private final MenuItem menuRemove = new MenuItem("Remove Tag");

    public LeftPaneContextMenu(TreeCell<ColoredText> source) {
        getItems().addAll(menuEdit, menuRemove);

        menuEdit.setOnAction(event -> mainTags.edit(mainTags.getTagObject(source)));
        menuRemove.setOnAction(event -> mainTags.remove(mainTags.getTagObject(source)));
    }
}
