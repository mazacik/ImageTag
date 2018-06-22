package project.gui.stage;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import project.database.Selection;
import project.database.TagDatabase;
import project.database.part.DatabaseItem;
import project.database.part.TagItem;
import project.gui.GUIStage;

import java.util.ArrayList;

public class TagManager extends Stage {
    /* components */
    private final BorderPane paneTagManager = new BorderPane();
    private final Scene sceneTagManager = new Scene(paneTagManager);

    private final TreeView<String> treeView = new TreeView(new TreeItem());

    private final ComboBox cbCategory = new ComboBox();
    private final ComboBox cbName = new ComboBox();

    private final Button btnTagAdd = new Button("Add");
    private final Button btnTagNew = new Button("New");

    /* constructors */
    public TagManager() {
        initializeComponents();
        initializeProperties();
    }

    /* initialize methods */
    private void initializeComponents() {
        treeView.setPadding(new Insets(5));
        treeView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        if (Selection.isEmpty()) {
            DatabaseItem currentFocusedItem = GUIStage.getPaneGallery().getCurrentFocusedItem();
            if (currentFocusedItem != null) {
                for (TagItem tagItem : currentFocusedItem.getTags()) {
                    treeView.getRoot().getChildren().add(new TreeItem(tagItem.getCategoryAndName()));
                }
            }
        } else {
            ArrayList<TagItem> sharedTags = Selection.getSharedTags();
            for (TagItem tagItem : sharedTags) {
                treeView.getRoot().getChildren().add(new TreeItem(tagItem.getCategoryAndName()));
            }
        }

        btnTagAdd.prefWidthProperty().bind(widthProperty());
        btnTagNew.prefWidthProperty().bind(widthProperty());
        cbCategory.prefWidthProperty().bind(widthProperty());
        cbName.prefWidthProperty().bind(widthProperty());

        cbCategory.requestFocus();
        cbCategory.setOnShown(event -> {
            cbCategory.getItems().clear();
            cbCategory.getItems().addAll(TagDatabase.getCategories());
        });
        cbName.setOnShown(event -> {
            cbName.getItems().clear();
            Object cbCategoryValue = cbCategory.getValue();
            if (cbCategoryValue != null) {
                cbName.getItems().addAll(TagDatabase.getItemsInCategory(cbCategoryValue.toString()));
            }
        });

        btnTagAdd.setOnAction(event -> addTag());
        btnTagNew.setOnAction(event -> TagDatabase.createTag());

        paneTagManager.setCenter(treeView);
        paneTagManager.setBottom(new VBox(2, cbCategory, cbName, btnTagAdd, btnTagNew));
    }
    private void initializeProperties() {
        setTitle("Tag Manager");
        setAlwaysOnTop(true);
        setMinWidth(320);
        setMinHeight(640);
        setScene(sceneTagManager);
        setOnCloseRequest(event -> TagDatabase.applyFilters());
        centerOnScreen();
        show();
    }

    /* private methods */
    private void addTag() {
        Object cbCategoryValue = cbCategory.getValue();
        Object cbNameValue = cbName.getValue();

        if (cbCategoryValue != null && cbNameValue != null) {
            TagItem tagItem = TagDatabase.getTagItem(cbCategoryValue.toString(), cbNameValue.toString());
            if (tagItem != null) {
                TagDatabase.addTagToItemSelection(tagItem);
                treeView.getRoot().getChildren().add(new TreeItem(tagItem.getCategoryAndName()));
                //treeView.getRoot().getChildren().sort(Comparator.comparing(TagItem::getCategoryAndName));
            }
        }
    }
}
