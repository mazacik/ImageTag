package project.frontend.components;

import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.WindowEvent;
import project.backend.shared.Backend;
import project.backend.shared.Database;
import project.backend.shared.DatabaseItem;
import project.backend.shared.Utility;
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
        menuSave.setOnAction(event -> Database.writeToDisk());
        menuRefresh.setOnAction(event -> {
            Database.filterByTags();
            Backend.reloadContent();
        });
        menuExit.setOnAction(event -> Frontend.getMainStage().fireEvent(new WindowEvent(Frontend.getMainStage(), WindowEvent.WINDOW_CLOSE_REQUEST)));
        menuFile.getItems().addAll(menuSave, menuRefresh, separatorFileMenu1, menuExit);

        menuSelectAll.setOnAction(event -> Database.addToSelection(Database.getFilteredItems()));
        menuClearSelection.setOnAction(event -> Database.clearSelection());
        menuSelection.getItems().addAll(menuSelectAll, menuClearSelection);

        menuUntaggedOnly.setOnAction(event -> {
            Database.getTagsWhitelist().clear();
            Database.getTagsBlacklist().clear();
            Database.getTagsBlacklist().addAll(Database.getTagDatabase());
            Database.filterByTags();
            Backend.reloadContent();
        });
        menuLessThanXTags.setOnAction(event -> {
            int maxTags = showNumberInputDialog("Filter Settings", "Maximum number of tags:");
            if (maxTags == 0) return;
            Database.getTagsWhitelist().clear();
            Database.getTagsBlacklist().clear();
            Database.getFilteredItems().clear();
            for (DatabaseItem databaseItem : Database.getItemDatabase())
                if (databaseItem.getTags().size() <= maxTags)
                    Database.getFilteredItems().add(databaseItem);
            Backend.reloadContent();
        });
        menuReset.setOnAction(event -> {
            Database.getTagsWhitelist().clear();
            Database.getTagsBlacklist().clear();
            Database.filterByTags();
            Backend.reloadContent();
        });
        menuFilter.getItems().addAll(menuUntaggedOnly, menuLessThanXTags, separatorFilterMenu1, menuReset);

        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().addAll(menuFile, menuSelection, menuFilter);
        setCenter(menuBar);

        MenuBar infoLabelArea = new MenuBar();
        infoLabelArea.getMenus().add(infoLabel);
        infoLabelArea.setOnMouseEntered(event -> Backend.swapImageDisplayMode());
        infoLabelArea.setOnMouseExited(event -> Backend.swapImageDisplayMode());
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
