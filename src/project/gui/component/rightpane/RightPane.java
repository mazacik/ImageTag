package project.gui.component.rightpane;

import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import project.control.FocusControl;
import project.control.ReloadControl;
import project.control.SelectionControl;
import project.database.control.TagControl;
import project.database.element.DataObject;
import project.database.element.TagObject;
import project.gui.custom.specific.RightPaneContextMenu;
import project.gui.event.listener.rightpane.EventListenerRightPane;

import java.util.ArrayList;

public abstract class RightPane {
    /* components */
    private static BorderPane _this = new BorderPane();

    private static final ChoiceBox cbGroup = new ChoiceBox();
    private static final ChoiceBox cbName = new ChoiceBox();
    private static final Button btnAdd = new Button("Add");
    private static final Button btnNew = new Button("New");

    private static final RightPaneContextMenu contextMenu = new RightPaneContextMenu();
    private static final ListView<String> listView = new ListView<>();

    /* initialize */
    public static void initialize() {
        initializeComponents();
        initializeInstance();
        EventListenerRightPane.initialize();
    }
    private static void initializeComponents() {
        cbGroup.prefWidthProperty().bind(_this.prefWidthProperty());
        cbGroup.maxWidthProperty().bind(_this.maxWidthProperty());
        cbName.prefWidthProperty().bind(_this.prefWidthProperty());
        cbName.maxWidthProperty().bind(_this.maxWidthProperty());
        btnAdd.prefWidthProperty().bind(_this.prefWidthProperty());
        btnAdd.maxWidthProperty().bind(_this.maxWidthProperty());
        btnNew.prefWidthProperty().bind(_this.prefWidthProperty());
        btnNew.maxWidthProperty().bind(_this.maxWidthProperty());

        cbName.setDisable(true);

        listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        listView.setContextMenu(contextMenu);
    }
    private static void initializeInstance() {
        _this.setMinWidth(200);
        _this.setPrefWidth(250);
        _this.setMaxWidth(300);

        _this.setTop(new VBox(2, cbGroup, cbName, btnAdd, btnNew));
        _this.setCenter(listView);
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
            TagObject tagObject = TagControl.getTagObject(group, name);
            if (SelectionControl.isSelectionEmpty()) {
                DataObject currentFocusedItem = FocusControl.getCurrentFocus();
                if (currentFocusedItem != null) {
                    currentFocusedItem.getTagCollection().add(tagObject);
                }
            } else {
                SelectionControl.addTagObjectToDataObjectSelection(tagObject);
            }
            ReloadControl.reload(RightPane.class);
        }
    }
    public static void reload() {
        ArrayList<String> sharedTags = new ArrayList<>();
        if (SelectionControl.isSelectionEmpty() || SelectionControl.isSelectionSingleElement()) {
            DataObject currentFocusedItem = FocusControl.getCurrentFocus();
            if (currentFocusedItem != null) {
                for (TagObject tagObject : currentFocusedItem.getTagCollection()) {
                    sharedTags.add(tagObject.getGroupAndName());
                }
            }
        } else {
            for (TagObject tagObject : SelectionControl.getIntersectingTags()) {
                sharedTags.add(tagObject.getGroupAndName());
            }
        }
        listView.getItems().setAll(sharedTags);
    }

    /* get */
    public static ChoiceBox getCbGroup() {
        return cbGroup;
    }
    public static ChoiceBox getCbName() {
        return cbName;
    }
    public static Button getBtnAdd() {
        return btnAdd;
    }
    public static Button getBtnNew() {
        return btnNew;
    }

    public static ListView<String> getListView() {
        return listView;
    }

    public static Region getInstance() {
        return _this;
    }
    public static RightPaneContextMenu getContextMenu() {
        return contextMenu;
    }
}
