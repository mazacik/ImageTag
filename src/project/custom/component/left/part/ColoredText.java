package project.custom.component.left.part;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListCell;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import project.common.Database;
import project.common.Filter;
import project.custom.component.gallery.GalleryPaneBack;
import project.custom.component.left.LeftPaneFront;

import java.util.List;

public class ColoredText {
    /* imports */
    private static final List<String> whitelist = Database.getDatabaseTagsWhitelist();
    private static final List<String> blacklist = Database.getDatabaseTagsBlacklist();

    /* variables */
    private String text;
    private Color color;

    /* constructors */
    public ColoredText(String text, Color color) {
        this.text = text;
        this.color = color;
    }

    /* event methods */
    public static void setOnMouseClick(ListCell<ColoredText> source, ColoredText coloredText) {
        source.setOnMouseClicked(event -> {
            if (event.getButton().equals(MouseButton.PRIMARY) && coloredText != null) {
                String tag = coloredText.getText();
                if (whitelist.contains(tag)) {
                    whitelist.remove(tag);
                    blacklist.add(tag);
                    coloredText.setColor(Color.RED);
                } else if (blacklist.contains(tag)) {
                    blacklist.remove(tag);
                    coloredText.setColor(Color.BLACK);
                } else {
                    whitelist.add(tag);
                    coloredText.setColor(Color.GREEN);
                }
                LeftPaneFront.getInstance().getListView().refresh();
                Filter.applyTagFilters();
                GalleryPaneBack.getInstance().reloadContent();
            }
        });
    }

    public static void setOnContextMenuRequest(ListCell<ColoredText> source, ColoredText coloredText) {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem menuAdd = new MenuItem("Add");
        menuAdd.setOnAction(event -> Filter.addTagToDatabase());
        MenuItem menuRemove = new MenuItem("Remove");
        menuRemove.setOnAction(event -> Filter.removeTagFromDatabase(coloredText.getText()));
        MenuItem menuRename = new MenuItem("Rename");
        menuRename.setOnAction(event -> Filter.renameTag(coloredText.getText()));
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
