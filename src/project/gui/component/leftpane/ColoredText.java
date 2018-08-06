package project.gui.component.leftpane;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeCell;
import javafx.scene.paint.Color;
import project.database.control.TagElementControl;

public class ColoredText {
    /* vars */
    private String text;
    private Color color;

    /* constructors */
    public ColoredText(String text, Color color) {
        this.text = text;
        this.color = color;
    }

    /* public */
    public static void setContextMenu(TreeCell<ColoredText> source) {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem menuEdit = new MenuItem("Edit");
        menuEdit.setOnAction(event -> TagElementControl.edit(TagElementControl.getTagElement(source)));
        MenuItem menuRemove = new MenuItem("Remove");
        menuRemove.setOnAction(event -> TagElementControl.remove(TagElementControl.getTagElement(source)));
        contextMenu.getItems().addAll(menuEdit, menuRemove);
        source.setContextMenu(contextMenu);
    }

    /* get */
    public String getText() {
        return text;
    }
    public Color getColor() {
        return color;
    }

    /* set */
    public void setColor(Color color) {
        this.color = color;
    }
}
