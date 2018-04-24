package project.frontend;

import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import project.backend.Database;
import project.backend.DatabaseItem;

public class RightPane extends BorderPane {
    private static final HBox addPane = new HBox();
    private static final Button addButton = new Button("+");
    private static final TextField addTextField = new TextField();
    private static final ListView<String> listView = new ListView<>();

    RightPane() {
        setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) addTag();
            else if (event.getCode() == KeyCode.DELETE) removeTag();
        });
        ContextMenu listContextMenu = new ContextMenu();
        MenuItem menuRemoveTag = new MenuItem("Remove");
        menuRemoveTag.setOnAction(event -> removeTag());
        listContextMenu.getItems().add(menuRemoveTag);
        listView.setContextMenu(listContextMenu);
        listView.setOnContextMenuRequested(event -> listContextMenu.show(listView, event.getScreenX(), event.getScreenY()));
        setPrefWidth(200);
        addButton.setStyle("-fx-focus-color: transparent;");
        addButton.setOnAction(event -> addTag());
        addPane.setAlignment(Pos.CENTER);
        addPane.setSpacing(10);
        addPane.getChildren().addAll(addTextField, addButton);
        setCenter(listView);
        setBottom(addPane);
    }

    private void removeTag() {
        if (!listView.getSelectionModel().getSelectedIndices().isEmpty()) {
            String tag = listView.getSelectionModel().getSelectedItem();
            if (SharedFE.getLeftPane().getDisplayMode() == LeftPaneDisplayMode.TAGS)
                SharedFE.getLeftPane().refreshContent();
            for (DatabaseItem databaseItem : Database.getSelectedItems())
                databaseItem.getTags().remove(tag);
            refreshContent();
        }
    }

    private void addTag() {
        String newTag = addTextField.getText();
        addTextField.clear();
        if (!newTag.isEmpty()) {
            if (!Database.getTagDatabase().contains(newTag)) {
                Database.getTagDatabase().add(newTag);
                if (SharedFE.getLeftPane().getDisplayMode() == LeftPaneDisplayMode.TAGS)
                    SharedFE.getLeftPane().refreshContent();
            }
            for (DatabaseItem databaseItem : Database.getSelectedItems())
                if (!databaseItem.getTags().contains(newTag))
                    databaseItem.getTags().add(newTag);
            refreshContent();
        }
    }

    private void refreshContent() {
        SharedFE.getRightPane().getListView().getItems().setAll(Database.getSelectedItemsSharedTags());
    }

    public ListView<String> getListView() {
        return listView;
    }
}
