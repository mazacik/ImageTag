package project.gui.event.handler.rightpane;

import javafx.scene.control.ChoiceBox;
import project.database.control.TagElementControl;
import project.gui.component.rightpane.RightPane;
import project.gui.event.listener.rightpane.EventListenerRightPane;

public abstract class EventHandlerGroupNode {
    public static void cbGroupOnShowing(ChoiceBox cbGroup) {
        Object cbGroupValue = cbGroup.getValue();
        if (cbGroupValue != null) {
            EventListenerRightPane.setGroupText(cbGroupValue.toString());
        }

        cbGroup.getItems().setAll(TagElementControl.getGroups());
    }
    public static void cbGroupOnHidden(ChoiceBox cbGroup) {
        Object cbGroupValue = cbGroup.getValue();
        String cbGroupValueString;
        String cbGroupStringOld = EventListenerRightPane.getGroupText();
        if (cbGroupValue != null && !(cbGroupValueString = cbGroupValue.toString()).isEmpty()) {
            if (cbGroupStringOld != null && !cbGroupStringOld.equals(cbGroupValueString)) {
                RightPane.getCbName().setValue(null);
            }
            EventListenerRightPane.setGroupText(cbGroupValueString);
            RightPane.getCbName().setDisable(false);
        } else {
            cbGroup.setValue(cbGroupStringOld);
        }
    }
}
