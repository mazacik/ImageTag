package project.gui.component;

import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.BorderPane;
import javafx.stage.WindowEvent;
import project.common.Settings;
import project.database.ItemDatabase;
import project.database.Selection;
import project.database.TagDatabase;
import project.database.loader.Serialization;
import project.database.part.DatabaseItem;
import project.gui.ChangeEventControl;
import project.gui.ChangeEventEnum;
import project.gui.ChangeEventListener;
import project.gui.GUIStage;
import project.gui.stage.generic.NumberInputWindow;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class PaneTop extends BorderPane implements ChangeEventListener {
    /* components */
    private final MenuBar infoLabelMenuBar = new MenuBar();
    private final Menu infoLabelMenu = new Menu();

    private final Menu menuFile = new Menu("File");
    private final MenuItem menuSave = new MenuItem("Save");
    private final MenuItem menuExit = new MenuItem("Exit");

    private final Menu menuSelection = new Menu("Selection");
    private final MenuItem menuSelectAll = new MenuItem("Select All");
    private final MenuItem menuClearSelection = new MenuItem("Clear Selection");

    private final Menu menuFilter = new Menu("Filter");
    private final MenuItem menuUntaggedOnly = new MenuItem("Untagged");
    private final MenuItem menuLessThanXTags = new MenuItem("Less Than X Tags");
    private final MenuItem menuReset = new MenuItem("Reset");

    /* constructors */
    public PaneTop() {
        initializeComponents();
        initializeProperties();
    }

    /* initialize methods */
    private void initializeComponents() {
        menuFile.getItems().addAll(menuSave, new SeparatorMenuItem(), menuExit);
        menuSelection.getItems().addAll(menuSelectAll, menuClearSelection);
        menuFilter.getItems().addAll(menuUntaggedOnly, menuLessThanXTags, new SeparatorMenuItem(), menuReset);

        infoLabelMenuBar.getMenus().add(infoLabelMenu);

        setOnAction();
        setInfoLabelContextMenu();
    }
    private void initializeProperties() {
        setCenter(new MenuBar(menuFile, menuSelection, menuFilter));
        setRight(infoLabelMenuBar);

        ChangeEventControl.subscribe(this, ChangeEventEnum.FOCUS);
    }

    /* public methods */
    public void refreshComponent() {
        DatabaseItem currentFocusedItem = GUIStage.getPaneGallery().getCurrentFocusedItem();
        if (currentFocusedItem != null) {
            infoLabelMenu.setText(currentFocusedItem.getName());
        }
    }

    /* event methods */
    private void setOnAction() {
        menuSave.setOnAction(event -> Serialization.writeToDisk());
        menuExit.setOnAction(event -> fireEvent(new WindowEvent(null, WindowEvent.WINDOW_CLOSE_REQUEST)));

        menuSelectAll.setOnAction(event -> Selection.add(ItemDatabase.getDatabaseItemsFiltered()));
        menuClearSelection.setOnAction(event -> Selection.clear());

        menuUntaggedOnly.setOnAction(event -> {
            TagDatabase.getDatabaseTagsWhitelist().clear();
            TagDatabase.getDatabaseTagsBlacklist().clear();
            TagDatabase.getDatabaseTagsBlacklist().addAll(TagDatabase.getDatabaseTags());
            TagDatabase.applyFilters();
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
            ChangeEventControl.requestReload();
        });
        menuReset.setOnAction(event -> {
            TagDatabase.getDatabaseTagsWhitelist().clear();
            TagDatabase.getDatabaseTagsBlacklist().clear();
            TagDatabase.applyFilters();
            ChangeEventControl.requestReload();
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
                    GUIStage.getPaneGallery().focusTile(ItemDatabase.getDatabaseItemsFiltered().get(index));

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
