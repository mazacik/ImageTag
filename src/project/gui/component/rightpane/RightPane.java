package project.gui.component.rightpane;

import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import project.MainUtils;
import project.database.object.DataObject;
import project.database.object.TagObject;
import project.gui.component.GUINode;
import project.gui.custom.specific.RightPaneContextMenu;

import java.util.ArrayList;

public class RightPane extends BorderPane implements MainUtils {
    private final ChoiceBox cbGroup;
    private final ChoiceBox cbName;
    private final Button btnAdd;
    private final Button btnNew;

    private final RightPaneContextMenu contextMenu;
    private final ListView<String> listView;

    public RightPane() {
        cbGroup = new ChoiceBox();
        cbName = new ChoiceBox();
        btnAdd = new Button("Add");
        btnNew = new Button("New");

        contextMenu = new RightPaneContextMenu();
        listView = new ListView<>();

        this.setDefaultValuesChildren();
        this.setDefaultValues();
    }
    private void setDefaultValuesChildren() {
        cbGroup.prefWidthProperty().bind(this.prefWidthProperty());
        cbGroup.maxWidthProperty().bind(this.maxWidthProperty());
        cbName.prefWidthProperty().bind(this.prefWidthProperty());
        cbName.maxWidthProperty().bind(this.maxWidthProperty());
        btnAdd.prefWidthProperty().bind(this.prefWidthProperty());
        btnAdd.maxWidthProperty().bind(this.maxWidthProperty());
        btnNew.prefWidthProperty().bind(this.prefWidthProperty());
        btnNew.maxWidthProperty().bind(this.maxWidthProperty());

        cbName.setDisable(true);

        listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        listView.setContextMenu(contextMenu);
    }
    private void setDefaultValues() {
        this.setMinWidth(200);
        this.setPrefWidth(250);
        this.setMaxWidth(300);

        this.setTop(new VBox(2, cbGroup, cbName, btnAdd, btnNew));
        this.setCenter(listView);
    }

    public void addTagToSelection() {
        Object cbGroupValue = cbGroup.getValue();
        Object cbNameValue = cbName.getValue();
        String group = "";
        String name = "";

        try {
            group = cbGroupValue.toString();
            name = cbNameValue.toString();
        } catch (NullPointerException ignored) {}

        if (!group.isEmpty() && !name.isEmpty()) {
            TagObject tagObject = tagControl.getTagObject(group, name);
            if (selectionControl.isSelectionEmpty()) {
                DataObject currentFocusedItem = focusControl.getCurrentFocus();
                if (currentFocusedItem != null) {
                    currentFocusedItem.getTagCollection().add(tagObject);
                }
            } else {
                selectionControl.addTagObject(tagObject);
            }
            reloadControl.reload(GUINode.RIGHTPANE);
        }
    }
    public void reload() {
        ArrayList<String> sharedTags = new ArrayList<>();
        if (selectionControl.isSelectionEmpty() || selectionControl.isSelectionSingleObject()) {
            DataObject currentFocusedItem = focusControl.getCurrentFocus();
            if (currentFocusedItem != null) {
                for (TagObject tagObject : currentFocusedItem.getTagCollection()) {
                    sharedTags.add(tagObject.getGroupAndName());
                }
            }
        } else {
            for (TagObject tagObject : selectionControl.getIntersectingTags()) {
                sharedTags.add(tagObject.getGroupAndName());
            }
        }
        listView.getItems().setAll(sharedTags);
    }

    public ChoiceBox getCbGroup() {
        return cbGroup;
    }
    public ChoiceBox getCbName() {
        return cbName;
    }
    public Button getBtnAdd() {
        return btnAdd;
    }
    public Button getBtnNew() {
        return btnNew;
    }

    public ListView<String> getListView() {
        return listView;
    }

    public RightPaneContextMenu getContextMenu() {
        return contextMenu;
    }
}
