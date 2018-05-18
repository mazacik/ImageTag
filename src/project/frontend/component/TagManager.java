package project.frontend.component;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import project.backend.common.Filter;
import project.backend.singleton.GalleryPaneBack;
import project.backend.database.Database;
import project.backend.database.DatabaseItem;

import java.util.ArrayList;
import java.util.Comparator;

public class TagManager extends Stage {
    private final ListView<ColoredText> listView = new ListView<>();
    private BorderPane tagManagerPane = new BorderPane();
    private Scene tagManagerScene = new Scene(tagManagerPane);
    private TextField addTextField = new TextField();

    public TagManager() {
        setAlwaysOnTop(true);
        setMinWidth(480);
        setMinHeight(640);
        setScene(tagManagerScene);
        setCellFactory();
        Button addButton = new Button("+");
        addButton.setStyle("-fx-focus-color: transparent;");
        addButton.setMinWidth(25);
        addButton.setOnAction(event -> addTag());
        HBox addPane = new HBox();
        addPane.setAlignment(Pos.CENTER);
        addPane.setSpacing(5);
        addPane.getChildren().addAll(addTextField, addButton);
        initializeListView();
        setOnCloseRequest(event -> {
            Filter.filterByTags();
            GalleryPaneBack.getInstance().reloadContent();
        });
        tagManagerPane.setCenter(listView);
        tagManagerPane.setBottom(addPane);
        addButton.requestFocus();

        show();
    }

    private void addTag() {
        if (!addTextField.getText().isEmpty()) {
            String newTag = addTextField.getText();

            if (!Database.getDatabaseTags().contains(newTag)) {
                Filter.addTagSelectedItems(newTag);
                listView.getItems().add(new ColoredText(newTag, Color.GREEN));
                listView.getItems().sort(Comparator.comparing(ColoredText::getText));
            }
            addTextField.clear();
        }
    }

    private void setCellFactory() {
        listView.setCellFactory(lv -> new ListCell<>() {
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
    }

    private void initializeListView() {
        listView.setPadding(new Insets(5));
        listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        ArrayList<String> sharedTags = Filter.getSelectedItemsSharedTags();
        ArrayList<String> theOtherTags = new ArrayList<>();
        for (String tag : Database.getDatabaseTags())
            if (!sharedTags.contains(tag))
                theOtherTags.add(tag);

        for (String tag : sharedTags)
            listView.getItems().add(new ColoredText(tag, Color.GREEN));
        for (String tag : theOtherTags)
            listView.getItems().add(new ColoredText(tag, Color.BLACK));
        listView.getItems().sort(Comparator.comparing(ColoredText::getText));

        listView.setOnMouseClicked(event -> {
            if (event.getButton().equals(MouseButton.PRIMARY)) {
                ColoredText coloredText = listView.getSelectionModel().getSelectedItem();
                if (coloredText != null) {
                    String selectedTag = coloredText.getText();
                    if (coloredText.getColor().equals(Color.BLACK)) {
                        for (DatabaseItem databaseItem : Database.getDatabaseItemsFiltered())
                            if (!databaseItem.getTags().contains(selectedTag))
                                databaseItem.getTags().add(selectedTag);
                        listView.getItems().set(listView.getSelectionModel().getSelectedIndex(), new ColoredText(selectedTag, Color.GREEN));
                    } else {
                        for (DatabaseItem databaseItem : Database.getDatabaseItemsFiltered())
                            databaseItem.getTags().remove(selectedTag);
                        listView.getItems().set(listView.getSelectionModel().getSelectedIndex(), new ColoredText(selectedTag, Color.BLACK));
                    }
                }
            }
        });
    }

    public ListView<ColoredText> getListView() {
        return listView;
    }
}
