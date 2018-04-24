package project.gui_components;

import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import project.backend.Database;
import project.backend.DatabaseItem;
import project.backend.Main;

public class RightPane extends BorderPane {
    private static final HBox addPane = new HBox();
    private static final Button addButton = new Button("+");
    private static final TextField addTextField = new TextField();
    private static final ListView<String> listView = new ListView<>();

    public RightPane() {
        setPrefWidth(200);
        addButton.setStyle("-fx-focus-color: transparent;");
        addButton.setOnAction(event -> {
            String newTag = addTextField.getText();
            addTextField.clear();
            if (!newTag.isEmpty()) {
                if (!Database.getTagDatabase().contains(newTag)) {
                    Database.getTagDatabase().add(newTag);
                    if (Main.getLeftPane().getDisplayMode() == LeftPaneDisplayMode.TAGS)
                        Main.getLeftPane().refreshContent();
                }
                for (DatabaseItem databaseItem : Database.getSelectedItems())
                    if (!databaseItem.getTags().contains(newTag))
                        databaseItem.getTags().add(newTag);
                Main.getRightPane().refreshContent();
            }
        });
        addPane.getChildren().addAll(addTextField, addButton);
        setCenter(listView);
        setBottom(addPane);
    }

    private void refreshContent() {
        Main.getRightPane().getListView().getItems().setAll(Database.getSelectedItemsSharedTags());
    }

    public ListView<String> getListView() {
        return listView;
    }
}
