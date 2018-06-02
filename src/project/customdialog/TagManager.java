package project.customdialog;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import project.common.Filter;
import project.component.gallery.GalleryPaneBack;
import project.component.left.part.ColoredText;
import project.database.Database;
import project.database.DatabaseItem;

import java.util.ArrayList;
import java.util.Comparator;
//todo: formatting
public class TagManager extends Stage {
    private final ListView<ColoredText> listView = new ListView<>();
    private BorderPane tagManagerPane = new BorderPane();
    private Scene tagManagerScene = new Scene(tagManagerPane);
    private TextField addTextField = new TextField();

    public TagManager() {
        setAlwaysOnTop(true);
        setMinWidth(480);
        setMinHeight(640);
        centerOnScreen();
        setScene(tagManagerScene);
        setCellFactory();
        tagManagerScene.setOnKeyPressed(event -> {
            if (event.getCode().equals(KeyCode.ENTER))
                addTag();
        });
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
            Filter.applyTagFilters();
            GalleryPaneBack.getInstance().reloadContent();
        });
        tagManagerPane.setCenter(listView);
        tagManagerPane.setBottom(addPane);
        addTextField.requestFocus();

        show();
    }

    private void addTag() {
        String newTag = addTextField.getText();
        if (!newTag.isEmpty()) {
            if (!Database.getDatabaseTags().contains(newTag)) {
                Filter.addTagToSelectedItems(newTag);
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

        ArrayList<String> sharedTags = Filter.getIntersectingTagsOfSelectedItems();
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
                        for (DatabaseItem databaseItem : Database.getDatabaseItemsSelected())
                            if (!databaseItem.getTags().contains(selectedTag))
                                databaseItem.getTags().add(selectedTag);
                        listView.getItems().set(listView.getSelectionModel().getSelectedIndex(), new ColoredText(selectedTag, Color.GREEN));
                    } else {
                        for (DatabaseItem databaseItem : Database.getDatabaseItemsSelected())
                            databaseItem.getTags().remove(selectedTag);
                        listView.getItems().set(listView.getSelectionModel().getSelectedIndex(), new ColoredText(selectedTag, Color.BLACK));
                    }
                }
            }
        });
    }
}
