package project.gui.component.top;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.stage.WindowEvent;
import project.backend.Filter;
import project.backend.Selection;
import project.backend.Settings;
import project.database.ItemDatabase;
import project.database.TagDatabase;
import project.database.loader.Serialization;
import project.database.part.DatabaseItem;
import project.gui.GUIController;
import project.gui.component.gallery.GalleryPane;
import project.gui.stage.generic.NumberInputWindow;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class TopPaneListener {
    /* lazy singleton */
    private static TopPaneListener instance;
    public static TopPaneListener getInstance() {
        if (instance == null) instance = new TopPaneListener();
        return instance;
    }

    /* imports */
    private final TopPane topPane = TopPane.getInstance();
    private final MenuBar infoLabelMenuBar = topPane.getInfoLabelMenuBar();
    private final Menu infoLabelMenu = topPane.getInfoLabelMenu();

    private final MenuItem menuSave = topPane.getMenuSave();
    private final MenuItem menuRefresh = topPane.getMenuRefresh();
    private final MenuItem menuExit = topPane.getMenuExit();

    private final MenuItem menuSelectAll = topPane.getMenuSelectAll();
    private final MenuItem menuClearSelection = topPane.getMenuClearSelection();

    private final MenuItem menuUntaggedOnly = topPane.getMenuUntaggedOnly();
    private final MenuItem menuLessThanXTags = topPane.getMenuLessThanXTags();
    private final MenuItem menuReset = topPane.getMenuReset();

    /* constructors */
    private TopPaneListener() {
        setOnAction();
        setInfoLabelContextMenu();
    }

    /* event methods */
    private void setOnAction() {
        menuSave.setOnAction(event -> Serialization.writeToDisk());
        menuRefresh.setOnAction(event -> {
            Filter.applyTagFilters();
            GUIController.getInstance().reloadComponentData(true);
        });
        menuExit.setOnAction(event -> GUIController.getInstance().fireEvent(new WindowEvent(GUIController.getInstance(), WindowEvent.WINDOW_CLOSE_REQUEST)));

        menuSelectAll.setOnAction(event -> Selection.add(ItemDatabase.getDatabaseItemsFiltered()));
        menuClearSelection.setOnAction(event -> Selection.clear());

        menuUntaggedOnly.setOnAction(event -> {
            TagDatabase.getDatabaseTagsWhitelist().clear();
            TagDatabase.getDatabaseTagsBlacklist().clear();
            TagDatabase.getDatabaseTagsBlacklist().addAll(TagDatabase.getDatabaseTags());
            Filter.applyTagFilters();
            GUIController.getInstance().reloadComponentData(true);
        });
        menuLessThanXTags.setOnAction(event -> {
            int maxTags = new NumberInputWindow("Filter Settings", "Maximum number of tags:").getResultValue();
            if (maxTags == 0) return;
            TagDatabase.getDatabaseTagsWhitelist().clear();
            TagDatabase.getDatabaseTagsBlacklist().clear();
            ItemDatabase.getDatabaseItemsFiltered().clear();
            for (DatabaseItem databaseItem : ItemDatabase.getDatabaseItems())
                if (databaseItem.getTags().size() <= maxTags)
                    ItemDatabase.getDatabaseItemsFiltered().add(databaseItem);
            GUIController.getInstance().reloadComponentData(true);
        });
        menuReset.setOnAction(event -> {
            TagDatabase.getDatabaseTagsWhitelist().clear();
            TagDatabase.getDatabaseTagsBlacklist().clear();
            Filter.applyTagFilters();
            GUIController.getInstance().reloadComponentData(true);
        });
    }

    private void setInfoLabelContextMenu() {
        ContextMenu contextMenu = new ContextMenu();

        MenuItem menuDelete = new MenuItem("Delete");
        menuDelete.setOnAction(event -> {
            String fileName = infoLabelMenu.getText();
            try {
                Files.delete(Paths.get(Settings.getMainDirectoryPath() + "\\" + fileName));
            } catch (IOException e) {
                e.printStackTrace();
            }
            for (DatabaseItem databaseItem : ItemDatabase.getDatabaseItems()) {
                if (databaseItem.getName().equals(fileName)) {
                    int index = ItemDatabase.getDatabaseItemsFiltered().indexOf(databaseItem);
                    ItemDatabase.getDatabaseItems().remove(databaseItem);
                    ItemDatabase.getDatabaseItemsFiltered().remove(databaseItem);
                    ItemDatabase.getDatabaseItemsSelected().remove(databaseItem);
                    if (ItemDatabase.getDatabaseItemsFiltered().get(index) == null)
                        index--;
                    GalleryPane.getInstance().focusTile(ItemDatabase.getDatabaseItemsFiltered().get(index));

                    break;
                }
            }
        });

        MenuItem menuCopy = new MenuItem("Copy");
        menuCopy.setOnAction(event -> {
            final Clipboard clipboard = Clipboard.getSystemClipboard();
            final ClipboardContent content = new ClipboardContent();
            content.putString(infoLabelMenu.getText());
            clipboard.setContent(content);
        });

        contextMenu.getItems().addAll(menuCopy, menuDelete);
        infoLabelMenuBar.setContextMenu(contextMenu);
    }
}
