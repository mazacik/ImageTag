package project.database.part;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import project.database.TagDatabase;
import project.gui.GUIStage;

public class ColoredText {
    /* variables */
    private String text;
    private Color color;

    /* constructors */
    public ColoredText(String text, Color color) {
        this.text = text;
        this.color = color;
    }

    /* event methods */
    public static void setOnMouseClick(TreeCell<ColoredText> source) {
        source.setOnMouseClicked(event -> {
            if (event.getButton().equals(MouseButton.PRIMARY)) {
                TagItem tagItem = TagDatabase.getTagItem(source);
                ColoredText coloredText = source.getTreeItem().getValue();

                // if source is category level
                if (tagItem == null) {
                    String categoryName = source.getText();
                    if (TagDatabase.isCategoryWhitelisted(categoryName)) {
                        TagDatabase.blacklistCategory(categoryName);
                        coloredText.setColor(Color.RED);
                        for (TreeItem<ColoredText> children : source.getTreeItem().getChildren()) {
                            children.getValue().setColor(Color.RED);
                        }
                    } else if (TagDatabase.isCategoryBlacklisted(categoryName)) {
                        TagDatabase.unmarkCategory(categoryName);
                        coloredText.setColor(Color.BLACK);
                        for (TreeItem<ColoredText> children : source.getTreeItem().getChildren()) {
                            children.getValue().setColor(Color.BLACK);
                        }
                    } else {
                        TagDatabase.whitelistCategory(categoryName);
                        coloredText.setColor(Color.GREEN);
                        for (TreeItem<ColoredText> children : source.getTreeItem().getChildren()) {
                            children.getValue().setColor(Color.GREEN);
                        }
                    }
                } else {
                    if (TagDatabase.isItemWhitelisted(tagItem)) {
                        TagDatabase.blacklistItem(tagItem);
                        coloredText.setColor(Color.RED);
                    } else if (TagDatabase.isItemBlacklisted(tagItem)) {
                        TagDatabase.unmarkItem(tagItem);
                        coloredText.setColor(Color.BLACK);
                    } else {
                        TagDatabase.whitelistItem(tagItem);
                        coloredText.setColor(Color.GREEN);
                    }
                }
                TagDatabase.applyFilters();
                GUIStage.getPaneLeft().refreshTreeview();
            }
        });
    }
    public static void setContextMenu(TreeCell<ColoredText> source) {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem menuAdd = new MenuItem("Add");
        menuAdd.setOnAction(event -> TagDatabase.createTag());
        MenuItem menuRemove = new MenuItem("Remove");
        menuRemove.setOnAction(event -> TagDatabase.removeTag(TagDatabase.getTagItem(source)));
        MenuItem menuRename = new MenuItem("Rename");
        menuRename.setOnAction(event -> TagDatabase.editTag(TagDatabase.getTagItem(source)));
        contextMenu.getItems().addAll(menuAdd, menuRemove, menuRename);
        source.setContextMenu(contextMenu);
    }

    /* getters */
    public String getText() {
        return text;
    }
    public Color getColor() {
        return color;
    }

    /* setters */
    public void setColor(Color color) {
        this.color = color;
    }
}
