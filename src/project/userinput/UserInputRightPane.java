package project.userinput;

import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.input.KeyCode;
import project.control.FilterControl;
import project.control.ReloadControl;
import project.database.control.TagElementControl;
import project.database.element.TagElement;
import project.gui.component.LeftPane.LeftPane;
import project.gui.component.RightPane.RightPane;
import project.gui.component.TopPane.TopPane;

public abstract class UserInputRightPane {
    /* vars */
    private static String cbGroupText;
    private static String cbNameText;

    public static void initialize() {
        RightPane.getInstance().setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                TopPane.getInstance().requestFocus();
            }
        });

        setOnShowing_cbGroup();
        setOnShowing_cbName();

        setOnHidden_cbGroup();
        setOnHidden_cbName();

        setOnAction_btnAdd();
        setOnAction_btnNew();

        setContextMenu_listView();
    }

    private static void setOnShowing_cbGroup() {
        ChoiceBox cbGroup = RightPane.getCbGroup();
        cbGroup.setOnShowing(event -> {
            Object cbGroupValue = cbGroup.getValue();
            if (cbGroupValue != null) {
                cbGroupText = cbGroupValue.toString();
            }

            cbGroup.getItems().setAll(TagElementControl.getGroups());
        });
    }
    private static void setOnShowing_cbName() {
        ChoiceBox cbName = RightPane.getCbName();
        cbName.setOnShowing(event -> {
            Object cbNameValue = cbName.getValue();
            if (cbNameValue != null) {
                cbNameText = cbNameValue.toString();
            }

            if (cbGroupText != null) {
                cbName.getItems().setAll(TagElementControl.getNamesInGroup(cbGroupText));
            }
        });
    }

    private static void setOnHidden_cbGroup() {
        ChoiceBox cbGroup = RightPane.getCbGroup();
        cbGroup.setOnHidden(event -> {
            Object cbGroupValue = cbGroup.getValue();
            String cbGroupValueString;
            if (cbGroupValue != null && !(cbGroupValueString = cbGroupValue.toString()).isEmpty()) {
                if (cbGroupText != null && !cbGroupText.equals(cbGroupValueString)) {
                    RightPane.getCbName().setValue(null);
                }
                cbGroupText = cbGroupValueString;
                RightPane.getCbName().setDisable(false);
            } else {
                cbGroup.setValue(cbGroupText);
            }
        });
    }
    private static void setOnHidden_cbName() {
        ChoiceBox cbName = RightPane.getCbName();
        cbName.setOnHidden(event -> {
            Object cbNameValue = cbName.getValue();
            if (cbNameValue != null && !cbNameValue.toString().isEmpty()) {
                cbNameText = cbNameValue.toString();
            } else {
                cbName.setValue(cbNameText);
            }
        });
    }

    private static void setOnAction_btnAdd() {
        Button btnAdd = RightPane.getBtnAdd();
        btnAdd.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                btnAdd.fire();
            }
        });
        btnAdd.setOnAction(event -> {
            RightPane.addTagToSelection();
            ReloadControl.requestComponentReload(true, RightPane.class);
        });
    }
    private static void setOnAction_btnNew() {
        ChoiceBox cbGroup = RightPane.getCbGroup();
        ChoiceBox cbName = RightPane.getCbName();
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
                ReloadControl.requestComponentReload(true, LeftPane.class);
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
            ReloadControl.requestComponentReload(true, RightPane.class);
        });
    }
}
