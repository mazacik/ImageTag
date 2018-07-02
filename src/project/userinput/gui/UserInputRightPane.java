package project.userinput.gui;

import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.input.KeyCode;
import project.control.FilterControl;
import project.control.ReloadControl;
import project.database.control.TagElementControl;
import project.database.element.TagElement;
import project.gui.component.LeftPane;
import project.gui.component.RightPane;
import project.gui.component.TopPane;

public abstract class UserInputRightPane {
    public static void initialize() {
        RightPane.getInstance().setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                TopPane.getInstance().requestFocus();
            }
        });

        setOnShowing_cbGroup();
        setOnShowing_cbName();
        setOnAction_btnAdd();
        setOnAction_btnNew();

        setContextMenu_listView();
    }

    private static void setOnShowing_cbGroup() {
        ComboBox cbGroup = RightPane.getCbGroup();
        cbGroup.setOnShowing(event -> {
            cbGroup.getItems().setAll(TagElementControl.getGroups());
        });
    }
    private static void setOnShowing_cbName() {
        ComboBox cbGroup = RightPane.getCbGroup();
        ComboBox cbName = RightPane.getCbName();
        cbName.setOnShowing(event -> {
            Object value = cbGroup.getValue();
            String group = value.toString();
            cbName.getItems().setAll(TagElementControl.getNamesInGroup(group));
            cbName.setVisibleRowCount(cbName.getItems().size());
        });
    }

    private static void setOnAction_btnAdd() {
        Button btnAdd = RightPane.getBtnAdd();
        btnAdd.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                btnAdd.fire();
            }
        });
        btnAdd.setOnAction(event -> RightPane.addTagToSelection());
        ReloadControl.requestReloadOf(true, RightPane.class);
    }
    private static void setOnAction_btnNew() {
        ComboBox cbGroup = RightPane.getCbGroup();
        ComboBox cbName = RightPane.getCbName();
        RightPane.getBtnNew().setOnAction(event -> {
            TagElement newTagElement = TagElementControl.create();
            if (newTagElement != null) {
                TagElementControl.add(newTagElement);
                cbGroup.getItems().setAll(TagElementControl.getGroups());
                cbGroup.getSelectionModel().select(newTagElement.getGroup());
                Object value = cbGroup.getValue();
                String group = value.toString();
                cbName.getItems().setAll(TagElementControl.getNamesInGroup(group));
                cbName.getSelectionModel().select(newTagElement.getName());
                ReloadControl.requestReloadOf(true, LeftPane.class);
            }
        });
    }

    private static void setContextMenu_listView() {
        RightPane.getListView().setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.DELETE) {
                RightPane.getMenuRemoveTag().fire();
            }
        });
        RightPane.getMenuRemoveTag().setOnAction(event -> {
            FilterControl.removeTagElementSelectionFromDataElementSelection();
            ReloadControl.requestReloadOf(true, RightPane.class);
        });
    }
}
