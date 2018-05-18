package project.backend.listener;

import javafx.scene.control.MenuItem;
import javafx.stage.WindowEvent;
import project.backend.Backend;
import project.backend.common.Filter;
import project.backend.common.Selection;
import project.backend.common.Serialization;
import project.backend.database.Database;
import project.backend.database.DatabaseItem;
import project.frontend.Frontend;
import project.frontend.common.NumberInputWindow;
import project.frontend.singleton.TopPaneFront;

public class TopPaneListener {
    private static final TopPaneListener instance = new TopPaneListener();

    private final MenuItem menuSave = TopPaneFront.getInstance().getMenuSave();
    private final MenuItem menuRefresh = TopPaneFront.getInstance().getMenuRefresh();
    private final MenuItem menuExit = TopPaneFront.getInstance().getMenuExit();

    private final MenuItem menuSelectAll = TopPaneFront.getInstance().getMenuSelectAll();
    private final MenuItem menuClearSelection = TopPaneFront.getInstance().getMenuClearSelection();

    private final MenuItem menuUntaggedOnly = TopPaneFront.getInstance().getMenuUntaggedOnly();
    private final MenuItem menuLessThanXTags = TopPaneFront.getInstance().getMenuLessThanXTags();
    private final MenuItem menuReset = TopPaneFront.getInstance().getMenuReset();


    private TopPaneListener() {
        setActionListeners();
    }

    public void setActionListeners() {
        menuSave.setOnAction(event -> Serialization.writeToDisk());
        menuRefresh.setOnAction(event -> {
            Filter.filterByTags();
            Backend.reloadContent(true);
        });
        menuExit.setOnAction(event -> Frontend.getMainStage().fireEvent(new WindowEvent(Frontend.getMainStage(), WindowEvent.WINDOW_CLOSE_REQUEST)));

        menuSelectAll.setOnAction(event -> Selection.add(Database.getDatabaseItemsFiltered()));
        menuClearSelection.setOnAction(event -> Selection.clear());

        menuUntaggedOnly.setOnAction(event -> {
            Database.getDatabaseTagsWhitelist().clear();
            Database.getDatabaseTagsBlacklist().clear();
            Database.getDatabaseTagsBlacklist().addAll(Database.getDatabaseTags());
            Filter.filterByTags();
            Backend.reloadContent(true);
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
            Backend.reloadContent(true);
        });
        menuReset.setOnAction(event -> {
            Database.getDatabaseTagsWhitelist().clear();
            Database.getDatabaseTagsBlacklist().clear();
            Filter.filterByTags();
            Backend.reloadContent(true);
        });
    }

    public static TopPaneListener getInstance() {
        return instance;
    }
}
