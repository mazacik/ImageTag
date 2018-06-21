package project.database.part;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeCell;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import project.backend.Filter;
import project.database.TagDatabase;
import project.gui.GUIStage;

import java.util.ArrayList;

public class ColoredText {
    /* imports */
    private static final ArrayList<TagItem> whitelist = TagDatabase.getDatabaseTagsWhitelist();
    private static final ArrayList<TagItem> blacklist = TagDatabase.getDatabaseTagsBlacklist();

    /* variables */
    private TagItem tagItem;
    private Color color;

    /* constructors */
    public ColoredText(TagItem tagItem, Color color) {
        this.tagItem = tagItem;
        this.color = color;
    }

    /* event methods */
    public static void setOnMouseClick(TreeCell<ColoredText> source, ColoredText coloredText) {
        source.setOnMouseClicked(event -> {
            if (event.getButton().equals(MouseButton.PRIMARY) && coloredText != null) {
                TagItem tagItem = coloredText.getTagItem();
                if (whitelist.contains(tagItem)) {
                    whitelist.remove(tagItem);
                    blacklist.add(tagItem);
                    coloredText.setColor(Color.RED);
                } else if (blacklist.contains(tagItem)) {
                    blacklist.remove(tagItem);
                    coloredText.setColor(Color.BLACK);
                } else {
                    whitelist.add(tagItem);
                    coloredText.setColor(Color.GREEN);
                }
                GUIStage.getLeftPane().getTreeView().refresh();
                Filter.applyTagFilters();
                GalleryPaneBack.getInstance().reloadContent();
            }
        });
    }

    public static void setOnContextMenuRequest(TreeCell<ColoredText> source, ColoredText coloredText) {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem menuAdd = new MenuItem("Add");
        menuAdd.setOnAction(event -> Filter.addTagToDatabase());
        MenuItem menuRemove = new MenuItem("Remove");
        menuRemove.setOnAction(event -> Filter.removeTagFromDatabase(coloredText.getTagItem()));
        MenuItem menuRename = new MenuItem("Rename");
        menuRename.setOnAction(event -> Filter.renameTag(coloredText.getTagItem()));
        contextMenu.getItems().addAll(menuAdd, menuRemove, menuRename);
        source.setContextMenu(contextMenu);
    }

    /* getters */
    public TagItem getTagItem() {
        return tagItem;
    }

    public Color getColor() {
        return color;
    }

    /* setters */
    public void setColor(Color color) {
        this.color = color;
    }
}
