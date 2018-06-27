package project.gui.component;

import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import project.database.Selection;
import project.database.TagDatabase;
import project.database.part.DatabaseItem;
import project.database.part.TagItem;
import project.gui.ChangeEventControl;
import project.gui.ChangeEventEnum;
import project.gui.ChangeEventListener;
import project.gui.GUIStage;
import project.gui.stage.TagManager;

import java.util.ArrayList;

public class PaneRight extends BorderPane implements ChangeEventListener {
    /* components */
    private final ListView<String> listView = new ListView<>();
    private final ComboBox cbCategory = new ComboBox();
    private final ComboBox cbName = new ComboBox();
    private final Button btnAdd = new Button("Add");
    private final Button btnManage = new Button("Manage");

    /* constructors */
    public PaneRight() {
        initializeComponents();
        initializeProperties();
    }

    /* initialize methods */
    private void initializeComponents() {
        btnAdd.setStyle("-fx-focus-color: transparent;");
        btnAdd.setMinWidth(25);
        btnAdd.prefWidthProperty().bind(prefWidthProperty());
        listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        btnAdd.setMaxWidth(getMaxWidth());
        btnManage.setMaxWidth(getMaxWidth());
        cbCategory.setMaxWidth(getMaxWidth());
        cbName.setMaxWidth(getMaxWidth());

        btnManage.prefWidthProperty().bind(prefWidthProperty());
        btnManage.setOnAction(event -> new TagManager());

        cbCategory.prefWidthProperty().bind(prefWidthProperty());
        cbCategory.setOnShown(event -> {
            cbCategory.getItems().clear();
            cbCategory.getItems().addAll(TagDatabase.getCategories());
        });

        cbName.prefWidthProperty().bind(prefWidthProperty());
        cbName.setOnShown(event -> {
            cbName.getItems().clear();
            if (cbCategory.getValue() != null && !cbCategory.getValue().toString().isEmpty()) {
                cbName.getItems().addAll(TagDatabase.getItemsInCategory(cbCategory.getValue().toString()));
            }
        });

        btnAdd.setOnAction(event -> addTagToItemSelection());

        setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                addTagToItemSelection();
            } else if (event.getCode() == KeyCode.DELETE) {
                TagDatabase.removeSelectedTagsFromItemSelection();
            } else if (event.getCode() == KeyCode.ESCAPE) {
                GUIStage.getPaneTop().requestFocus();
            }
        });

        ContextMenu listContextMenu = new ContextMenu();
        MenuItem menuRemoveTag = new MenuItem("Remove");
        menuRemoveTag.setOnAction(event -> TagDatabase.removeSelectedTagsFromItemSelection());
        listContextMenu.getItems().add(menuRemoveTag);
        listView.setContextMenu(listContextMenu);
    }
    private void initializeProperties() {
        setMinWidth(150);
        setPrefWidth(200);
        setMaxWidth(300);

        setCenter(listView);
        setBottom(new VBox(2, cbCategory, cbName, btnAdd, btnManage));

        ChangeEventControl.subscribe(this, ChangeEventEnum.FOCUS);
    }

    /* public methods */
    public void refreshComponent() {
        ArrayList<String> sharedTags = new ArrayList<>();
        if (Selection.isEmpty()) {
            DatabaseItem currentFocusedItem = GUIStage.getPaneGallery().getCurrentFocusedItem();
            if (currentFocusedItem != null) {
                for (TagItem tagItem : currentFocusedItem.getTags()) {
                    sharedTags.add(tagItem.getGroupAndName());
                }
            }
        } else {
            for (TagItem tagItem : Selection.getSharedTags()) {
                sharedTags.add(tagItem.getGroupAndName());
            }
        }
        listView.getItems().setAll(sharedTags);
    }

    /* private methods */
    private void addTagToItemSelection() {
        Object categoryComboBoxValue = cbCategory.getValue();
        Object nameComboBoxValue = cbName.getValue();
        String category = "";
        String name = "";

        if (categoryComboBoxValue != null) {
            category = categoryComboBoxValue.toString();
        }
        if (nameComboBoxValue != null) {
            name = nameComboBoxValue.toString();
        }
        if (!category.isEmpty() && !name.isEmpty()) {
            TagItem tagItem = TagDatabase.getTagItem(category, name);
            if (Selection.isEmpty()) {
                DatabaseItem currentFocusedItem = GUIStage.getPaneGallery().getCurrentFocusedItem();
                if (currentFocusedItem != null) {
                    currentFocusedItem.getTags().add(tagItem);
                }
            } else {
                TagDatabase.addTagToItemSelection(tagItem);
            }
        }
    }

    /* getters */
    public ListView<String> getListView() {
        return listView;
    }
}
