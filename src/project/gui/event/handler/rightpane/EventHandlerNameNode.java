package project.gui.event.handler.rightpane;

import javafx.scene.control.ChoiceBox;
import project.database.control.TagElementControl;
import project.gui.event.listener.rightpane.EventListenerRightPane;

public abstract class EventHandlerNameNode {
    public static void cbNameOnShowing(ChoiceBox cbName) {
        Object cbNameValue = cbName.getValue();
        if (cbNameValue != null) {
            EventListenerRightPane.setNameText(cbNameValue.toString());
        }

        String groupText = EventListenerRightPane.getGroupText();
        if (groupText != null) {
            cbName.getItems().setAll(TagElementControl.getNamesInGroup(groupText));
        }
    }
    public static void cbNameOnHidden(ChoiceBox cbName) {
        Object cbNameValue = cbName.getValue();
        if (cbNameValue != null && !cbNameValue.toString().isEmpty()) {
            EventListenerRightPane.setNameText(cbNameValue.toString());
        } else {
            cbName.setValue(EventListenerRightPane.getNameText());
        }
    }
}
