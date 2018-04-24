package project.gui_components;

import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import project.backend.Database;
import project.backend.DatabaseItem;
import project.backend.Main;

import java.util.List;

public class LeftPane extends BorderPane {
    /* references */
    private static final List<String> whitelist = Database.getTagsWhitelist();
    private static final List<String> blacklist = Database.getTagsBlacklist();
    /* components */
    private static final HBox buttonPane = new HBox();
    private static final Button leftButton = new Button();
    private static final Button rightButton = new Button();
    private static final ListView<ColoredText> listView = new ListView<>();
    /* variables */
    private static LeftPaneDisplayMode displayMode = LeftPaneDisplayMode.NAMES;

    public LeftPane() {
        initializeBackend();
        initializeFrontend();
    }

    private void initializeFrontend() {
        setPrefWidth(200);

        leftButton.setText("Names");
        leftButton.setStyle("-fx-focus-color: transparent;");
        leftButton.setPrefWidth(75);

        rightButton.setText("Tags");
        rightButton.setStyle("-fx-focus-color: transparent;");
        rightButton.setPrefWidth(75);

        buttonPane.getChildren().addAll(leftButton, rightButton);

        setCenter(listView);
        setBottom(buttonPane);
    }

    private void initializeBackend() {
        listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        listView.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(ColoredText coloredText, boolean empty) {
                super.updateItem(coloredText, empty);
                if (coloredText == null) {
                    setText(null);
                    setTextFill(null);
                } else {
                    setText(coloredText.getText());
                    setTextFill(coloredText.getColor());
                }
            }
        });
        listView.setOnMouseClicked(event -> {
            if (displayMode == LeftPaneDisplayMode.NAMES && listView.getSelectionModel().getSelectedItem() != null) {
                DatabaseItem selectedItem = listView.getSelectionModel().getSelectedItem().getOwner();
                Database.setLastSelectedItem(selectedItem);
                if (Database.getSelectedItems().contains(selectedItem))
                    Database.removeIndexFromSelection(selectedItem);
                else
                    Database.addToSelection(selectedItem);
            } else if (displayMode == LeftPaneDisplayMode.TAGS && listView.getSelectionModel().getSelectedItem() != null) {
                String tag = listView.getSelectionModel().getSelectedItem().getText();
                if (whitelist.contains(tag)) {
                    whitelist.remove(tag);
                    blacklist.add(tag);
                    listView.getItems().set(listView.getSelectionModel().getSelectedIndex(), new ColoredText(tag, Color.RED));
                } else if (blacklist.contains(tag)) {
                    blacklist.remove(tag);
                    listView.getItems().set(listView.getSelectionModel().getSelectedIndex(), new ColoredText(tag, Color.BLACK));
                } else {
                    whitelist.add(tag);
                    listView.getItems().set(listView.getSelectionModel().getSelectedIndex(), new ColoredText(tag, Color.GREEN));
                }
                Database.filter();
                Main.getGalleryPane().refreshContent();
            }
        });

        leftButton.setOnMouseClicked(event -> {
            displayMode = LeftPaneDisplayMode.NAMES;
            refreshContent();
        });

        rightButton.setOnMouseClicked(event -> {
            displayMode = LeftPaneDisplayMode.TAGS;
            refreshContent();
        });
    }

    public void refreshContent() {
        listView.getItems().clear();
        if (displayMode == LeftPaneDisplayMode.TAGS)
            for (String tag : Database.getTagDatabase())
                listView.getItems().add(new ColoredText(tag, Color.BLACK));
        else
            for (DatabaseItem databaseItem : Database.getFilteredItems())
                listView.getItems().add(databaseItem.getColoredText());
    }

    public LeftPaneDisplayMode getDisplayMode() {
        return displayMode;
    }

    public ListView<ColoredText> getListView() {
        return listView;
    }
}