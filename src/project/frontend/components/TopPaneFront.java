package project.frontend.components;

import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.WindowEvent;
import project.backend.common.Filter;
import project.backend.common.Selection;
import project.backend.common.Serialization;
import project.backend.Backend;
import project.backend.database.Database;
import project.backend.database.DatabaseItem;
import project.backend.Utility;
import project.frontend.shared.Frontend;

import java.util.Optional;

public class TopPaneFront extends BorderPane {
    private final Menu infoLabel = new Menu();

    private final Menu menuFile = new Menu("File");
    private final MenuItem menuSave = new MenuItem("Save");
    private final MenuItem menuRefresh = new MenuItem("Refresh");
    private final SeparatorMenuItem separatorFileMenu1 = new SeparatorMenuItem();
    private final MenuItem menuExit = new MenuItem("Exit");

    private final Menu menuSelection = new Menu("Selection");
    private final MenuItem menuSelectAll = new MenuItem("Select All");
    private final MenuItem menuClearSelection = new MenuItem("Clear Selection");

    private final Menu menuFilter = new Menu("Filter");
    private final MenuItem menuUntaggedOnly = new MenuItem("Untagged");
    private final MenuItem menuLessThanXTags = new MenuItem("Less Than X Tags");
    private final SeparatorMenuItem separatorFilterMenu1 = new SeparatorMenuItem();
    private final MenuItem menuReset = new MenuItem("Reset");

    public TopPaneFront() {
        menuSave.setOnAction(event -> Serialization.writeToDisk());
        menuRefresh.setOnAction(event -> {
            Filter.filterByTags();
            Backend.reloadContent();
        });
        menuExit.setOnAction(event -> Frontend.getMainStage().fireEvent(new WindowEvent(Frontend.getMainStage(), WindowEvent.WINDOW_CLOSE_REQUEST)));
        menuFile.getItems().addAll(menuSave, menuRefresh, separatorFileMenu1, menuExit);

        menuSelectAll.setOnAction(event -> Database.addToSelection(Database.getDatabaseItemsFiltered()));
        menuClearSelection.setOnAction(event -> Selection.clear());
        menuSelection.getItems().addAll(menuSelectAll, menuClearSelection);

        menuUntaggedOnly.setOnAction(event -> {
            Database.getDatabaseTagsWhitelist().clear();
            Database.getDatabaseTagsBlacklist().clear();
            Database.getDatabaseTagsBlacklist().addAll(Database.getDatabaseTags());
            Filter.filterByTags();
            Backend.reloadContent();
        });
        menuLessThanXTags.setOnAction(event -> {
            int maxTags = showNumberInputDialog("Filter Settings", "Maximum number of tags:");
            if (maxTags == 0) return;
            Database.getDatabaseTagsWhitelist().clear();
            Database.getDatabaseTagsBlacklist().clear();
            Database.getDatabaseItemsFiltered().clear();
            for (DatabaseItem databaseItem : Database.getDatabaseItems())
                if (databaseItem.getTags().size() <= maxTags)
                    Database.getDatabaseItemsFiltered().add(databaseItem);
            Backend.reloadContent();
        });
        menuReset.setOnAction(event -> {
            Database.getDatabaseTagsWhitelist().clear();
            Database.getDatabaseTagsBlacklist().clear();
            Filter.filterByTags();
            Backend.reloadContent();
        });
        menuFilter.getItems().addAll(menuUntaggedOnly, menuLessThanXTags, separatorFilterMenu1, menuReset);

        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().addAll(menuFile, menuSelection, menuFilter);
        setCenter(menuBar);

        MenuBar infoLabelArea = new MenuBar();
        infoLabelArea.getMenus().add(infoLabel);
        infoLabelArea.setOnMouseEntered(event -> Frontend.swapImageDisplayMode());
        infoLabelArea.setOnMouseExited(event -> Frontend.swapImageDisplayMode());
        setRight(infoLabelArea);
    }

    private Integer showNumberInputDialog(String title, String contentText) {
        TextInputDialog textInputDialog = new TextInputDialog("1");
        textInputDialog.setTitle(title);
        textInputDialog.setHeaderText(null);
        textInputDialog.setGraphic(null);
        textInputDialog.setContentText(contentText);

        Optional<String> resultValue = textInputDialog.showAndWait();
        if (!resultValue.isPresent()) return 0;
        String resultString = resultValue.get();
        if (!Utility.isInteger(resultString)) return 0;
        return Integer.valueOf(resultString);
    }

    public Menu getInfoLabel() {
        return infoLabel;
    }
}
