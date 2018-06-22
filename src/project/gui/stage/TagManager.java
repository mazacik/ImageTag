package project.gui.stage;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import project.backend.Selection;
import project.database.TagDatabase;
import project.database.part.TagItem;

import java.util.ArrayList;

public class TagManager extends Stage {
    /* components */
    private final BorderPane tagManagerPane = new BorderPane();
    private final Scene tagManagerScene = new Scene(tagManagerPane);

    private final TreeView<String> treeView = new TreeView(new TreeItem());
    private final VBox addPane = new VBox(2);

    private final ComboBox cbCategory = new ComboBox();
    private final ComboBox cbName = new ComboBox();

    private final Button btnAdd = new Button("Add");
    private final Button btnNew = new Button("New");

    /* constructors */
    public TagManager() {
        /* frontend */
        setTitle("Tag Manager");
        setAlwaysOnTop(true);
        setMinWidth(320);
        setMinHeight(640);
        centerOnScreen();

        /* initialization */
        setScene(tagManagerScene);
        initializeTreeView();

        cbCategory.prefWidthProperty().bind(widthProperty());
        cbName.prefWidthProperty().bind(widthProperty());
        btnAdd.prefWidthProperty().bind(widthProperty());
        btnNew.prefWidthProperty().bind(widthProperty());

        addPane.getChildren().addAll(cbCategory, cbName, btnAdd, btnNew);

        /* action listeners */
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

        btnAdd.setOnAction(event -> addTag());
        btnNew.setOnAction(event -> TagDatabase.createTag());

        setOnCloseRequest(event -> TagDatabase.applyFilters());

        tagManagerPane.setCenter(treeView);
        tagManagerPane.setBottom(addPane);

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

    /* builder methods */
    private void initializeTreeView() {
        treeView.setPadding(new Insets(5));
        treeView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        ArrayList<TagItem> sharedTags = Selection.getSharedTags();
        for (TagItem tagItem : sharedTags)
            treeView.getRoot().getChildren().add(new TreeItem(tagItem.getCategoryAndName()));
    }
}
