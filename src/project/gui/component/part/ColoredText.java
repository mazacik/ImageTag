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
import project.gui.control.GUIStage;

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

                // if source is category level
                if (tagElement == null) {
                    String categoryName = source.getText();
                    if (FilterControl.isGroupWhitelisted(categoryName)) {
                        FilterControl.blacklistGroup(categoryName);
                        coloredText.setColor(Color.RED);
                        for (TreeItem<ColoredText> children : source.getTreeItem().getChildren()) {
                            children.getValue().setColor(Color.RED);
                        }
                    } else if (FilterControl.isGroupBlacklisted(categoryName)) {
                        FilterControl.unlistGroup(categoryName);
                        coloredText.setColor(Color.BLACK);
                        for (TreeItem<ColoredText> children : source.getTreeItem().getChildren()) {
                            children.getValue().setColor(Color.BLACK);
                        }
                    } else {
                        FilterControl.whitelistGroup(categoryName);
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
                FilterControl.refreshValidDataElements();
                GUIStage.getPaneLeft().refreshTreeview();
            }
        });
    }
    public static void setContextMenu(TreeCell<ColoredText> source) {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem menuAdd = new MenuItem("Add");
        menuAdd.setOnAction(event -> TagElementControl.add(TagElementControl.create()));
        MenuItem menuRemove = new MenuItem("Remove");
        menuRemove.setOnAction(event -> TagElementControl.remove(TagElementControl.getTagElement(source)));
        MenuItem menuRename = new MenuItem("Rename");
        menuRename.setOnAction(event -> TagElementControl.edit(TagElementControl.getTagElement(source)));
        contextMenu.getItems().addAll(menuAdd, menuRemove, menuRename);
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
