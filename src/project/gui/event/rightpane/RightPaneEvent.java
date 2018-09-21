package project.gui.event.rightpane;

import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import project.control.MainControl;
import project.control.TagControl;
import project.database.object.TagObject;
import project.gui.component.GUINode;
import project.gui.component.rightpane.RightPane;
import project.gui.component.toppane.TopPane;
import project.gui.custom.specific.TagEditor;

public abstract class RightPaneEvent {
    private static String cbNameText;
    private static String cbGroupText;

    public static void initialize() {
        onMouseClick();
        onKeyPress();

        onAction_contextMenu();

        ChoiceBox cbGroup = RightPane.getCbGroup();
        ChoiceBox cbName = RightPane.getCbName();

        onShowing_cbGroup(cbGroup);
        onHidden_cbGroup(cbGroup);

        onShowing_cbName(cbName);
        onHidden_cbName(cbName);

        Button btnAdd = RightPane.getBtnAdd();
        btnAdd.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                btnAdd.fire();
            }
        });
        btnAdd.setOnAction(event -> {
            RightPane.addTagToSelection();
            MainControl.getReloadControl().reload(true, GUINode.RIGHTPANE);
        });

        RightPane.getBtnNew().setOnAction(event -> {
            TagObject newTagObject = TagEditor.createTag();
            if (newTagObject != null) {
                TagControl.add(newTagObject);
                cbGroup.getItems().setAll(TagControl.getGroups());
                cbGroup.getSelectionModel().select(newTagObject.getGroup());
                Object value = cbGroup.getValue();
                String group = value.toString();
                cbName.getItems().setAll(TagControl.getNames(group));
                cbName.getSelectionModel().select(newTagObject.getName());
                MainControl.getReloadControl().reload(true, GUINode.LEFTPANE);
            }
        });
    }

    private static void onMouseClick() {
        RightPane.getListView().setOnMouseClicked(event -> {
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
    private static void onLeftClick() {
        RightPane.getInstance().requestFocus();
        RightPane.getContextMenu().hide();
    }
    private static void onRightClick(MouseEvent event) {
        MainControl.getSelectionControl().addDataObject(MainControl.getFocusControl().getCurrentFocus());
        RightPane.getContextMenu().show(RightPane.getInstance(), event.getScreenX(), event.getScreenY());
    }

    private static void onKeyPress() {
        RightPane.getInstance().setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case ESCAPE:
                    TopPane.getInstance().requestFocus();
                    break;
                case DELETE:
                    RightPane.getContextMenu().getMenuRemove().fire();
                    break;
                default:
                    break;
            }
        });
    }
    private static void onAction_contextMenu() {
        RightPane.getContextMenu().getMenuEdit().setOnAction(event -> {
            TagControl.edit(TagControl.getTagObject(RightPane.getListView().getSelectionModel().getSelectedItem()));
        });

        RightPane.getContextMenu().getMenuRemove().setOnAction(event -> {
            MainControl.getSelectionControl().removeTagObjectSelection();
            TagControl.remove(TagControl.getTagObject(RightPane.getListView().getSelectionModel().getSelectedItem()));
            MainControl.getReloadControl().reload(true, GUINode.RIGHTPANE);
        });
    }

    private static void onShowing_cbGroup(ChoiceBox cbGroup) {
        cbGroup.setOnShowing(event -> {
            Object cbGroupValue = cbGroup.getValue();
            if (cbGroupValue != null) {
                cbGroupText = cbGroupValue.toString();
            }

            cbGroup.getItems().setAll(TagControl.getGroups());
        });
    }
    private static void onHidden_cbGroup(ChoiceBox cbGroup) {
        cbGroup.setOnHidden(event -> {
            Object cbGroupValue = cbGroup.getValue();
            String cbGroupValueString;
            String cbGroupStringOld = cbGroupText;
            if (cbGroupValue != null && !(cbGroupValueString = cbGroupValue.toString()).isEmpty()) {
                if (cbGroupStringOld != null && !cbGroupStringOld.equals(cbGroupValueString)) {
                    RightPane.getCbName().setValue(null);
                }
                cbGroupText = cbGroupValueString;
                RightPane.getCbName().setDisable(false);
            } else {
                cbGroup.setValue(cbGroupStringOld);
            }
        });
    }

    private static void onShowing_cbName(ChoiceBox cbName) {
        cbName.setOnShowing(event -> {
            Object cbNameValue = cbName.getValue();
            if (cbNameValue != null) {
                cbNameText = cbNameValue.toString();
            }

            String groupText = cbGroupText;
            if (groupText != null) {
                cbName.getItems().setAll(TagControl.getNames(groupText));
            }
        });
    }
    private static void onHidden_cbName(ChoiceBox cbName) {
        cbName.setOnHidden(event -> {
            Object cbNameValue = cbName.getValue();
            if (cbNameValue != null && !cbNameValue.toString().isEmpty()) {
                cbNameText = cbNameValue.toString();
            } else {
                cbName.setValue(cbNameText);
            }
        });
    }
}
