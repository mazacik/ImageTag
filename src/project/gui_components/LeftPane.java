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

enum ListDisplayMode {NAMES, TAGS}

class ColoredText {

    private final String text;
    private final Color color;

    ColoredText(String text, Color color) {
        this.text = text;
        this.color = color;
    }

    Color getColor() {
        return color;
    }

    String getText() {
        return text;
    }
}

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
    private static ListDisplayMode listDisplayMode = ListDisplayMode.NAMES;

    public LeftPane() {
        setPrefWidth(200);
        listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        listView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(ColoredText item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null) {
                    setText(null);
                    setTextFill(null);
                } else {
                    setText(item.getText());
                    setTextFill(item.getColor());
                }
            }
        });

        listView.setOnMouseClicked(event -> {
            if (listDisplayMode == ListDisplayMode.NAMES) {
                int selectedIndex = listView.getSelectionModel().getSelectedIndex();
                if (Database.getSelectedIndexes().contains(selectedIndex))
                    Database.removeIndexFromSelection(selectedIndex);
                else
                    Database.addToSelection(selectedIndex);
            }
        });

        listView.setOnMouseClicked(event -> {
            if (listDisplayMode == ListDisplayMode.TAGS && listView.getSelectionModel().getSelectedItem() != null) {
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

        leftButton.setText("Names");
        leftButton.setStyle("-fx-focus-color: transparent;");
        leftButton.setPrefWidth(75);
        leftButton.setOnMouseClicked(event -> {
            listDisplayMode = ListDisplayMode.NAMES;
            refreshContent();
        });

        rightButton.setText("Tags");
        rightButton.setStyle("-fx-focus-color: transparent;");
        rightButton.setPrefWidth(75);
        rightButton.setOnMouseClicked(event -> {
            listDisplayMode = ListDisplayMode.TAGS;
            refreshContent();
        });

        buttonPane.getChildren().addAll(leftButton, rightButton);
        setCenter(listView);
        setBottom(buttonPane);
    }

    public void refreshContent() {
        listView.getItems().clear();
        if (listDisplayMode == ListDisplayMode.TAGS)
            for (String tag : Database.getTagDatabase())
                listView.getItems().add(new ColoredText(tag, Color.BLACK));
        else
            for (DatabaseItem item : Database.getItemDatabaseFiltered())
                listView.getItems().add(new ColoredText(item.getSimpleName(), Color.BLACK));
    }

    ListDisplayMode getDisplayMode() {
        return listDisplayMode;
    }

    public ListView<ColoredText> getListView() {
        return listView;
    }
}