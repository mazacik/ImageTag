package project.gui.component;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import project.control.FilterControl;
import project.control.FocusControl;
import project.control.SelectionControl;
import project.control.change.ChangeEventControl;
import project.control.change.ChangeEventEnum;
import project.database.control.TagElementControl;
import project.database.element.DataElement;
import project.database.element.TagElement;

import java.util.ArrayList;

public abstract class RightPane {
    /* components */
    private static BorderPane _this = new BorderPane();

    private static final ListView<String> listView = new ListView<>();
    private static final ComboBox cbGroup = new ComboBox();
    private static final ComboBox cbName = new ComboBox();
    private static final Button btnAdd = new Button("Add");
    private static final Button btnNew = new Button("New");

    /* initialize */
    public static void initialize() {
        initializeComponents();
        initializeProperties();
    }
    private static void initializeComponents() {
        btnAdd.setStyle("-fx-focus-color: transparent;");
        btnAdd.setMinWidth(25);
        btnAdd.setPadding(new Insets(0, 0, 2, 0));
        btnAdd.prefWidthProperty().bind(_this.prefWidthProperty());
        listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        btnAdd.setMaxWidth(_this.getMaxWidth());
        btnNew.setMaxWidth(_this.getMaxWidth());
        cbGroup.setMaxWidth(_this.getMaxWidth());
        cbName.setMaxWidth(_this.getMaxWidth());

        btnNew.prefWidthProperty().bind(_this.prefWidthProperty());
        btnNew.setOnAction(event -> {
            TagElement newTagElement = TagElementControl.create();
            if (newTagElement != null) {
                TagElementControl.add(newTagElement);
                cbGroup.getSelectionModel().select(newTagElement.getGroup());
                cbName.getSelectionModel().select(newTagElement.getName());
            }
        });


        cbGroup.prefWidthProperty().bind(_this.prefWidthProperty());
        cbGroup.setVisibleRowCount(20);
        cbGroup.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                cbName.setDisable(false);
                if (newValue != oldValue) {
                    cbName.getItems().clear();
                }
            } else {
                cbName.setDisable(true);
            }
        });
        cbGroup.setOnShowing(event -> cbGroup.getItems().setAll(TagElementControl.getGroups()));

        cbName.prefWidthProperty().bind(_this.prefWidthProperty());
        cbName.setDisable(true);
        cbName.setVisibleRowCount(20);
        cbName.setOnShowing(event -> {
            Object value = cbGroup.getValue();
            String group = value.toString();
            cbName.getItems().setAll(TagElementControl.getNamesInGroup(group));
        });

        btnAdd.setOnAction(event -> addTagToSelection());
        btnAdd.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                btnAdd.fire();
            }
        });

        listView.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.DELETE) {
                FilterControl.removeTagElementSelectionFromDataElementSelection();
            }
        });

        _this.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                TopPane.getInstance().requestFocus();
            }
        });

        ContextMenu listContextMenu = new ContextMenu();
        MenuItem menuRemoveTag = new MenuItem("Remove");
        menuRemoveTag.setOnAction(event -> FilterControl.removeTagElementSelectionFromDataElementSelection());
        listContextMenu.getItems().add(menuRemoveTag);
        listView.setContextMenu(listContextMenu);
    }
    private static void initializeProperties() {
        _this.setMinWidth(150);
        _this.setPrefWidth(200);
        _this.setMaxWidth(300);

        _this.setCenter(listView);
        _this.setTop(new VBox(2, cbGroup, cbName, btnAdd, btnNew));

        ChangeEventControl.subscribe(RightPane.class, ChangeEventEnum.FOCUS, ChangeEventEnum.SELECTION);
    }

    /* public */
    public static void refreshComponent() {
        ArrayList<String> sharedTags = new ArrayList<>();
        if (SelectionControl.isSelectionEmpty()) {
            DataElement currentFocusedItem = FocusControl.getCurrentFocus();
            if (currentFocusedItem != null) {
                for (TagElement tagElement : currentFocusedItem.getTagElements()) {
                    sharedTags.add(tagElement.getGroupAndName());
                }
            }
        } else {
            for (TagElement tagElement : SelectionControl.getIntersectingTags()) {
                sharedTags.add(tagElement.getGroupAndName());
            }
        }
        listView.getItems().setAll(sharedTags);
    }

    /* private */
    private static void addTagToSelection() {
        Object cbGroupValue = cbGroup.getValue();
        Object cbNameValue = cbName.getValue();
        String group = "";
        String name = "";

        if (cbGroupValue != null) {
            group = cbGroupValue.toString();
        }
        if (cbNameValue != null) {
            name = cbNameValue.toString();
        }

        if (!group.isEmpty() && !name.isEmpty()) {
            TagElement tagElement = TagElementControl.getTagElement(group, name);
            if (SelectionControl.isSelectionEmpty()) {
                DataElement currentFocusedItem = FocusControl.getCurrentFocus();
                if (currentFocusedItem != null) {
                    currentFocusedItem.getTagElements().add(tagElement);
                }
            } else {
                FilterControl.addTagElementToDataElementSelection(tagElement);
            }
        }
    }

    /* get */
    public static ListView<String> getListView() {
        return listView;
    }
    public static Region getInstance() {
        return _this;
    }
}
