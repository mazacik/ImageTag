package project.component.top;

import javafx.scene.control.MenuItem;
import javafx.stage.WindowEvent;
import project.GUIController;
import project.common.Filter;
import project.common.Selection;
import project.customdialog.generic.NumberInputWindow;
import project.database.Database;
import project.database.DatabaseItem;
import project.database.Serialization;

public class TopPaneListener {
    /* lazy singleton */
    private static TopPaneListener instance;
    public static TopPaneListener getInstance() {
        if (instance == null) instance = new TopPaneListener();
        return instance;
    }

    /* imports */
    private final MenuItem menuSave = TopPaneFront.getInstance().getMenuSave();
    private final MenuItem menuRefresh = TopPaneFront.getInstance().getMenuRefresh();
    private final MenuItem menuExit = TopPaneFront.getInstance().getMenuExit();

    private final MenuItem menuSelectAll = TopPaneFront.getInstance().getMenuSelectAll();
    private final MenuItem menuClearSelection = TopPaneFront.getInstance().getMenuClearSelection();

    private final MenuItem menuUntaggedOnly = TopPaneFront.getInstance().getMenuUntaggedOnly();
    private final MenuItem menuLessThanXTags = TopPaneFront.getInstance().getMenuLessThanXTags();
    private final MenuItem menuReset = TopPaneFront.getInstance().getMenuReset();

    /* constructors */
    private TopPaneListener() {
        setOnAction();
    }

    /* event methods */
    private void setOnAction() {
        menuSave.setOnAction(event -> Serialization.writeToDisk());
        menuRefresh.setOnAction(event -> {
            Filter.filterByTags();
            GUIController.getInstance().reloadComponentData(true);
        });
        menuExit.setOnAction(event -> GUIController.getInstance().fireEvent(new WindowEvent(GUIController.getInstance(), WindowEvent.WINDOW_CLOSE_REQUEST)));

        menuSelectAll.setOnAction(event -> Selection.getInstance().add(Database.getDatabaseItemsFiltered()));
        menuClearSelection.setOnAction(event -> Selection.getInstance().clear());

        menuUntaggedOnly.setOnAction(event -> {
            Database.getDatabaseTagsWhitelist().clear();
            Database.getDatabaseTagsBlacklist().clear();
            Database.getDatabaseTagsBlacklist().addAll(Database.getDatabaseTags());
            Filter.filterByTags();
            GUIController.getInstance().reloadComponentData(true);
        });
        menuLessThanXTags.setOnAction(event -> {
            int maxTags = new NumberInputWindow("Filter Settings", "Maximum number of tags:").getResultValue();
            if (maxTags == 0) return;
            Database.getDatabaseTagsWhitelist().clear();
            Database.getDatabaseTagsBlacklist().clear();
            Database.getDatabaseItemsFiltered().clear();
            for (DatabaseItem databaseItem : Database.getDatabaseItems())
                if (databaseItem.getTags().size() <= maxTags)
                    Database.getDatabaseItemsFiltered().add(databaseItem);
            GUIController.getInstance().reloadComponentData(true);
        });
        menuReset.setOnAction(event -> {
            Database.getDatabaseTagsWhitelist().clear();
            Database.getDatabaseTagsBlacklist().clear();
            Filter.filterByTags();
            GUIController.getInstance().reloadComponentData(true);
        });
    }
}
