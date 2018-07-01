package project.gui.component.part;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import project.control.FilterControl;
import project.database.control.TagElementControl;
import project.database.element.TagElement;
import project.gui.component.LeftPane;

public class ColoredText {
    /* vars */
    private String text;
    private Color color;

    /* constructors */
    public ColoredText(String text, Color color) {
        this.text = text;
        this.color = color;
    }

    /* event */
    public static void setOnMouseClick(TreeCell<ColoredText> source) {
        source.setOnMouseClicked(event -> {
            if (event.getButton().equals(MouseButton.PRIMARY)) {
                TagElement tagElement = TagElementControl.getTagElement(source);
                ColoredText coloredText;
                try {
                    coloredText = source.getTreeItem().getValue();
                } catch (NullPointerException e) {
                    return;
                }

                // if source is group level
                if (tagElement == null) {
                    String groupName = source.getText();
                    if (FilterControl.isGroupWhitelisted(groupName)) {
                        FilterControl.blacklistGroup(groupName);
                        coloredText.setColor(Color.RED);
                        for (TreeItem<ColoredText> children : source.getTreeItem().getChildren()) {
                            children.getValue().setColor(Color.RED);
                        }
                    } else if (FilterControl.isGroupBlacklisted(groupName)) {
                        FilterControl.unlistGroup(groupName);
                        coloredText.setColor(Color.BLACK);
                        for (TreeItem<ColoredText> children : source.getTreeItem().getChildren()) {
                            children.getValue().setColor(Color.BLACK);
                        }
                    } else {
                        FilterControl.whitelistGroup(groupName);
                        coloredText.setColor(Color.GREEN);
                        for (TreeItem<ColoredText> children : source.getTreeItem().getChildren()) {
                            children.getValue().setColor(Color.GREEN);
                        }
                    }
                } else {
                    if (FilterControl.isTagElementWhitelisted(tagElement)) {
                        FilterControl.blacklistTagElement(tagElement);
                        coloredText.setColor(Color.RED);
                    } else if (FilterControl.isTagElementBlacklisted(tagElement)) {
                        FilterControl.unlistTagElement(tagElement);
                        coloredText.setColor(Color.BLACK);
                    } else {
                        FilterControl.whitelistTagElement(tagElement);
                        coloredText.setColor(Color.GREEN);
                    }
                }
                FilterControl.revalidateDataElements();
                LeftPane.refreshTreeview();
            }
        });
    }
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
