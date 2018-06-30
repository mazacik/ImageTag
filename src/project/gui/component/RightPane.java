package project.gui.component;

import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import project.control.FilterControl;
import project.control.FocusControl;
import project.control.SelectionControl;
import project.control.change.ChangeEventControl;
import project.control.change.ChangeEventEnum;
import project.database.control.TagElementControl;
import project.database.element.DataElement;
import project.database.element.TagElement;
import project.gui.control.GUIStage;

import java.util.ArrayList;

public abstract class RightPane extends BorderPane {
    /* components */
    private static BorderPane _this = new BorderPane();

    private static final ListView<String> listView = new ListView<>();
    private static final ComboBox cbGroup = new ComboBox();
    private static final ComboBox cbName = new ComboBox();
    private static final Button btnAdd = new Button("Add");
    private static final Button btnManage = new Button("New");

    /* initialize */
    public static void initialize() {
        initializeComponents();
        initializeProperties();
    }
    private static void initializeComponents() {
        btnAdd.setStyle("-fx-focus-color: transparent;");
        btnAdd.setMinWidth(25);
        btnAdd.prefWidthProperty().bind(_this.prefWidthProperty());
        listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        btnAdd.setMaxWidth(_this.getMaxWidth());
        btnManage.setMaxWidth(_this.getMaxWidth());
        cbGroup.setMaxWidth(_this.getMaxWidth());
        cbName.setMaxWidth(_this.getMaxWidth());

        btnManage.prefWidthProperty().bind(_this.prefWidthProperty());
        btnManage.setOnAction(event -> {
            TagElement newTagElement = TagElementControl.create();
            if (newTagElement == null) return;
            TagElementControl.add(newTagElement);
            cbGroup.setValue(newTagElement.getGroup());
            cbName.setValue(newTagElement.getName());
        });

        cbGroup.prefWidthProperty().bind(_this.prefWidthProperty());
        cbGroup.setOnShown(event -> {
            cbGroup.getItems().clear();
            cbGroup.getItems().addAll(TagElementControl.getGroups());
        });

        cbName.prefWidthProperty().bind(_this.prefWidthProperty());
        cbName.setOnShown(event -> {
            cbName.getItems().clear();
            if (cbGroup.getValue() != null && !cbGroup.getValue().toString().isEmpty()) {
                cbName.getItems().addAll(TagElementControl.getNamesInGroup(cbGroup.getValue().toString()));
            }
        });

        btnAdd.setOnAction(event -> addTagToSelection());

        _this.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                addTagToSelection();
            } else if (event.getCode() == KeyCode.DELETE) {
                FilterControl.removeTagElementSelectionFromDataElementSelection();
            } else if (event.getCode() == KeyCode.ESCAPE) {
                GUIStage.getPaneTop().requestFocus();
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
        _this.setBottom(new VBox(2, cbGroup, cbName, btnAdd, btnManage));

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
    public static Node getInstance() {
        return _this;
    }
}
