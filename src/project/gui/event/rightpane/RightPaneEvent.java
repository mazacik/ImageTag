package project.gui.event.rightpane;

import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import project.database.object.TagObject;
import project.gui.component.GUINode;
import project.gui.component.rightpane.SuggestMode;
import project.gui.custom.specific.TagEditor;
import project.utils.MainUtil;

public class RightPaneEvent implements MainUtil {
    private final ChoiceBox cbGroup;
    private final ChoiceBox cbName;

    private String cbNameText;
    private String cbGroupText;

    public RightPaneEvent() {
        cbGroup = rightPane.getCbGroup();
        cbName = rightPane.getCbName();

        this.initBtnCommon();
        this.initBtnRecent();
        this.initBtnAdd();
        this.initBtnNew();

        suggestionListView_onLeftClick();
        intersectionListView_onMouseClick();
        onKeyPress();

        onAction_contextMenu();


        onShowing_cbGroup(cbGroup);
        onHidden_cbGroup(cbGroup);

        onShowing_cbName(cbName);
        onHidden_cbName(cbName);
    }

    private void initBtnCommon() {
        Button btnCommon = rightPane.getBtnCommon();
        btnCommon.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                btnCommon.fire();
            }
        });
        btnCommon.setOnAction(event -> {
            rightPane.setSuggestMode(SuggestMode.COMMON);
            reload.queue(GUINode.RIGHTPANE);
            reload.doReload();
        });
    }
    private void initBtnRecent() {
        Button btnRecent = rightPane.getBtnRecent();
        btnRecent.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                btnRecent.fire();
            }
        });
        btnRecent.setOnAction(event -> {
            rightPane.setSuggestMode(SuggestMode.RECENT);
            reload.queue(GUINode.RIGHTPANE);
            reload.doReload();
        });
    }
    private void initBtnAdd() {
        Button btnAdd = rightPane.getBtnAdd();
        btnAdd.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                btnAdd.fire();
            }
        });
        btnAdd.setOnAction(event -> {
            rightPane.addTagToSelection();
            reload.queue(GUINode.RIGHTPANE);
            reload.doReload();
        });
    }
    private void initBtnNew() {
        Button btnNew = rightPane.getBtnNew();
        btnNew.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                btnNew.fire();
            }
        });
        btnNew.setOnAction(event -> {
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
            }
        });
    }

    private void suggestionListView_onLeftClick() {
        rightPane.getSuggestionListView().setOnMouseClicked(event -> {
            if (event.getClickCount() % 2 == 0) {
                String groupAndName = rightPane.getSuggestionListView().getSelectionModel().getSelectedItem();
                if (!groupAndName.isEmpty()) {
                    selection.addTagObject(mainTags.getTagObject(groupAndName));
                    reload.queue(GUINode.RIGHTPANE);
                    reload.doReload();
                }
            }
        });
    }

    private void intersectionListView_onMouseClick() {
        rightPane.getIntersectionListView().setOnMouseClicked(event -> {
            switch (event.getButton()) {
                case PRIMARY:
                    intersectionListView_onLeftClick();
                    break;
                case SECONDARY:
                    intersectionListView_onRightClick(event);
                    break;
                default:
                    break;
            }
        });
    }
    private void intersectionListView_onLeftClick() {
        rightPane.getContextMenu().hide();
    }
    private void intersectionListView_onRightClick(MouseEvent event) {
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
        rightPane.getContextMenu().getMenuEdit().setOnAction(event -> mainTags.edit(mainTags.getTagObject(rightPane.getIntersectionListView().getSelectionModel().getSelectedItem())));
        rightPane.getContextMenu().getMenuRemove().setOnAction(event -> {
            //todo update common tags list
            selection.removeTagObject();
            //todo multiple tags at once vvv
            mainTags.remove(mainTags.getTagObject(rightPane.getIntersectionListView().getSelectionModel().getSelectedItem()));
            reload.queue(GUINode.RIGHTPANE);
            reload.doReload();
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
