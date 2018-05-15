package project.backend.listener;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListCell;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import project.backend.common.Filter;
import project.backend.common.Selection;
import project.backend.database.Database;
import project.backend.database.DatabaseItem;
import project.backend.singleton.GalleryPaneBack;
import project.frontend.component.ColoredText;
import project.frontend.singleton.LeftPaneFront;

import java.util.List;

public abstract class ListenerTemplate {
    private static final List<String> whitelist = Database.getDatabaseTagsWhitelist();
    private static final List<String> blacklist = Database.getDatabaseTagsBlacklist();


    public static void setColoredTextMouseClick(ListCell<ColoredText> source, ColoredText coloredText) {
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
                Filter.filterByTags();
                GalleryPaneBack.getInstance().reloadContent();
            }
        });
    }

    public static void setGalleryTileMouseClick(MouseEvent event, DatabaseItem databaseItem) {
        if (event.getButton().equals(MouseButton.PRIMARY))
            Selection.setFocusedItem(databaseItem, true);
    }

    public static void setColoredTextContextMenu(ListCell<ColoredText> source, ColoredText coloredText) {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem menuRename = new MenuItem("Rename");
        menuRename.setOnAction(event -> Filter.renameTag(coloredText.getText()));
        contextMenu.getItems().addAll(menuRename);
        source.setContextMenu(contextMenu);
    }
}
