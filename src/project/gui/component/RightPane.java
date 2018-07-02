package project.gui.component;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import project.control.FilterControl;
import project.control.FocusControl;
import project.control.SelectionControl;
import project.database.control.TagElementControl;
import project.database.element.DataElement;
import project.database.element.TagElement;
import project.userinput.gui.UserInputRightPane;

import java.util.ArrayList;

public abstract class RightPane {
    /* components */
    private static BorderPane _this = new BorderPane();

    private static final ComboBox cbGroup = new ComboBox();
    private static final ComboBox cbName = new ComboBox();
    private static final Button btnAdd = new Button("Add");
    private static final Button btnNew = new Button("New");

    private static final MenuItem menuRemoveTag = new MenuItem("Remove");
    private static final ContextMenu listViewContextMenu = new ContextMenu(menuRemoveTag);
    private static final ListView<String> listView = new ListView<>();

    /* initialize */
    public static void initialize() {
        initializeComponents();
        initializeInstance();
        UserInputRightPane.initialize();
    }
    private static void initializeComponents() {
        cbGroup.prefWidthProperty().bind(_this.prefWidthProperty());
        cbGroup.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.toString().isEmpty()) {
                cbName.setDisable(false);
                if (newValue != oldValue) {
                    cbName.setValue(null);
                }
            } else {
                cbName.setValue(null);
                cbName.setDisable(true);
            }
        });

        cbName.prefWidthProperty().bind(_this.prefWidthProperty());
        cbName.setDisable(true);

        btnAdd.setPadding(new Insets(0, 0, 2, 0));
        btnAdd.prefWidthProperty().bind(_this.prefWidthProperty());

        btnNew.prefWidthProperty().bind(_this.prefWidthProperty());

        listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        listView.setContextMenu(listViewContextMenu);
    }
    private static void initializeInstance() {
        _this.setMinWidth(150);
        _this.setPrefWidth(200);
        _this.setMaxWidth(300);

        _this.setCenter(listView);
        _this.setTop(new VBox(2, cbGroup, cbName, btnAdd, btnNew));
    }

    /* public */
    public static void addTagToSelection() {
        Object cbGroupValue = cbGroup.getValue();
        Object cbNameValue = cbName.getValue();
        String group = "";
        String name = "";

        try {
            group = cbGroupValue.toString();
            name = cbNameValue.toString();
        } catch (NullPointerException ignored) {}

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

    public static void reload() {
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

    /* get */
    public static ComboBox getCbGroup() {
        return cbGroup;
    }
    public static ComboBox getCbName() {
        return cbName;
    }
    public static Button getBtnAdd() {
        return btnAdd;
    }
    public static Button getBtnNew() {
        return btnNew;
    }

    public static MenuItem getMenuRemoveTag() {
        return menuRemoveTag;
    }
    public static ListView<String> getListView() {
        return listView;
    }

    public static Region getInstance() {
        return _this;
    }
}
