package project.gui.component;

import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import project.backend.Selection;
import project.database.TagDatabase;
import project.database.part.TagItem;
import project.gui.ChangeEvent;
import project.gui.ChangeNotificationHelper;
import project.gui.GUIController;
import project.gui.GUIStage;
import project.gui.stage.TagManager;

import java.util.ArrayList;

public class RightPane extends BorderPane implements ChangeNotificationHelper {
    /* change listeners */
    private final ArrayList<ChangeNotificationHelper> changeListeners = new ArrayList<>();

    /* components */
    private final ListView<String> listView = new ListView<>();
    private final ComboBox cbCategory = new ComboBox();
    private final ComboBox cbName = new ComboBox();
    private final Button btnAdd = new Button("Add");
    private final Button btnManage = new Button("Manage");

    /* constructors */
    public RightPane() {
        /* design */
        setMinWidth(150);
        setPrefWidth(200);
        setMaxWidth(300);

        GUIController.subscribe(this, ChangeEvent.FOCUS);

        /* component initialization */
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

        setOnKeyPress();
        setBtnAddOnAction();
        setListviewContextMenu();

        VBox addPane = new VBox();
        addPane.setAlignment(Pos.CENTER);
        addPane.setSpacing(2);
        addPane.getChildren().addAll(cbCategory, cbName, btnAdd, btnManage);

        /* scene initialization */
        setCenter(listView);
        setBottom(addPane);
    }

    /* public methods */
    public void refresh() {
        ArrayList<String> sharedTags = new ArrayList<>();
        for (TagItem tagItem : Selection.getSharedTags())
            sharedTags.add(tagItem.getCategoryAndName());
        listView.getItems().setAll(sharedTags);
    }

    /* event methods */
    private void setOnKeyPress() {
        setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER)
                addTag();
            else if (event.getCode() == KeyCode.DELETE)
                TagDatabase.removeSelectedTagsFromItemSelection();
            else if (event.getCode() == KeyCode.ESCAPE)
                GUIStage.getTopPane().requestFocus();
        });
    }
    private void setBtnAddOnAction() {
        btnAdd.setOnAction(event -> addTag());
    }
    private void setListviewContextMenu() {
        ContextMenu listContextMenu = new ContextMenu();
        MenuItem menuRemoveTag = new MenuItem("Remove");
        menuRemoveTag.setOnAction(event -> TagDatabase.removeSelectedTagsFromItemSelection());
        listContextMenu.getItems().add(menuRemoveTag);
        listView.setContextMenu(listContextMenu);
    }

    /* private methods */
    private void addTag() {
        Object categoryComboBoxValue = cbCategory.getValue();
        Object nameComboBoxValue = cbName.getValue();
        String category = "";
        String name = "";

        if (categoryComboBoxValue != null)
            category = categoryComboBoxValue.toString();
        if (nameComboBoxValue != null)
            name = nameComboBoxValue.toString();

        if (!category.isEmpty() && !name.isEmpty()) {
            TagDatabase.addTagToItemSelection(TagDatabase.getTagItem(category, name));
        }
    }

    /* getters */
    public ListView<String> getListView() {
        return listView;
    }
    public ArrayList<ChangeNotificationHelper> getChangeListeners() {
        return changeListeners;
    }
}
