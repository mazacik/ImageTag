package project.gui.event.handler.rightpane;

import javafx.scene.control.ChoiceBox;
import project.control.ReloadControl;
import project.database.control.TagControl;
import project.database.element.TagObject;
import project.gui.component.leftpane.LeftPane;
import project.gui.component.rightpane.RightPane;
import project.gui.custom.specific.TagEditor;

public class EventHandlerButtonNew {
    private static void btnNewOnAction() {
        ChoiceBox cbGroup = RightPane.getCbGroup();
        ChoiceBox cbName = RightPane.getCbName();
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
                ReloadControl.reload(true, LeftPane.class);
            }
        });
    }
}
