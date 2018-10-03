package project.gui.event.rightpane;

import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import project.database.object.TagObject;
import project.gui.component.GUINode;
import project.gui.custom.specific.TagEditor;
import project.utils.MainUtil;

public class RightPaneEvent implements MainUtil {
    private String cbNameText;
    private String cbGroupText;

    public RightPaneEvent() {
        onMouseClick();
        onKeyPress();

        onAction_contextMenu();

        ChoiceBox cbGroup = rightPane.getCbGroup();
        ChoiceBox cbName = rightPane.getCbName();

        onShowing_cbGroup(cbGroup);
        onHidden_cbGroup(cbGroup);

        onShowing_cbName(cbName);
        onHidden_cbName(cbName);

        Button btnAdd = rightPane.getBtnAdd();
        btnAdd.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                btnAdd.fire();
            }
        });
        btnAdd.setOnAction(event -> {
            rightPane.addTagToSelection();
            reload.queue(true, GUINode.RIGHTPANE);
        });

        rightPane.getBtnNew().setOnAction(event -> {
            TagObject newTagObject = TagEditor.createTag();
            if (newTagObject != null) {
                mainTags.add(newTagObject);
                cbGroup.getItems().setAll(mainTags.getGroups());
                cbGroup.getSelectionModel().select(newTagObject.getGroup());
                Object value = cbGroup.getValue();
                String group = value.toString();
                cbName.getItems().setAll(mainTags.getNames(group));
                cbName.getSelectionModel().select(newTagObject.getName());
                cbName.setDisable(false);
                btnAdd.fire();
                //reload.queue(true, GUINode.LEFTPANE);
            }
        });
    }

    private void onMouseClick() {
        rightPane.getListView().setOnMouseClicked(event -> {
            switch (event.getButton()) {
                case PRIMARY:
                    onLeftClick();
                    break;
                case SECONDARY:
                    onRightClick(event);
                    break;
                default:
                    break;
            }
        });
    }
    private void onLeftClick() {
        rightPane.requestFocus();
        rightPane.getContextMenu().hide();
    }
    private void onRightClick(MouseEvent event) {
        selection.add(focus.getCurrentFocus());
        rightPane.getContextMenu().show(rightPane, event.getScreenX(), event.getScreenY());
    }

    private void onKeyPress() {
        rightPane.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case ESCAPE:
                    topPane.requestFocus();
                    break;
                case DELETE:
                    rightPane.getContextMenu().getMenuRemove().fire();
                    break;
                default:
                    break;
            }
        });
    }
    private void onAction_contextMenu() {
        rightPane.getContextMenu().getMenuEdit().setOnAction(event -> mainTags.edit(mainTags.getTagObject(rightPane.getListView().getSelectionModel().getSelectedItem())));
        rightPane.getContextMenu().getMenuRemove().setOnAction(event -> {
            selection.removeTagObject();
            mainTags.remove(mainTags.getTagObject(rightPane.getListView().getSelectionModel().getSelectedItem()));
            reload.queue(true, GUINode.RIGHTPANE);
        });
    }

    private void onShowing_cbGroup(ChoiceBox cbGroup) {
        cbGroup.setOnShowing(event -> {
            rightPane.requestFocus();
            Object cbGroupValue = cbGroup.getValue();
            if (cbGroupValue != null) {
                cbGroupText = cbGroupValue.toString();
            }

            cbGroup.getItems().setAll(mainTags.getGroups());
        });
    }
    private void onHidden_cbGroup(ChoiceBox cbGroup) {
        cbGroup.setOnHidden(event -> {
            cbGroup.requestFocus();
            Object cbGroupValue = cbGroup.getValue();
            String cbGroupValueString;
            String cbGroupStringOld = cbGroupText;
            if (cbGroupValue != null && !(cbGroupValueString = cbGroupValue.toString()).isEmpty()) {
                if (cbGroupStringOld != null && !cbGroupStringOld.equals(cbGroupValueString)) {
                    rightPane.getCbName().setValue(null);
                }
                cbGroupText = cbGroupValueString;
                rightPane.getCbName().setDisable(false);
            } else {
                cbGroup.setValue(cbGroupStringOld);
            }
        });
    }

    private void onShowing_cbName(ChoiceBox cbName) {
        cbName.setOnShowing(event -> {
            rightPane.requestFocus();
            Object cbNameValue = cbName.getValue();
            if (cbNameValue != null) {
                cbNameText = cbNameValue.toString();
            }

            String groupText = cbGroupText;
            if (groupText != null) {
                cbName.getItems().setAll(mainTags.getNames(groupText));
            }
        });
    }
    private void onHidden_cbName(ChoiceBox cbName) {
        cbName.setOnHidden(event -> {
            cbName.requestFocus();
            Object cbNameValue = cbName.getValue();
            if (cbNameValue != null && !cbNameValue.toString().isEmpty()) {
                cbNameText = cbNameValue.toString();
            } else {
                cbName.setValue(cbNameText);
            }
        });
    }
}
