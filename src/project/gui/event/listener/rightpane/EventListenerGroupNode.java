package project.gui.event.listener.rightpane;

import javafx.scene.control.ChoiceBox;
import project.gui.component.rightpane.RightPane;
import project.gui.event.handler.rightpane.EventHandlerGroupNode;

public abstract class EventListenerGroupNode {
    public static void initialize() {
        ChoiceBox cbGroup = RightPane.getCbGroup();

        EventListenerGroupNode.cbGroupOnShowing(cbGroup);
        EventListenerGroupNode.cbGroupOnHidden(cbGroup);
    }

    private static void cbGroupOnShowing(ChoiceBox cbGroup) {
        cbGroup.setOnShowing(event -> EventHandlerGroupNode.cbGroupOnShowing(cbGroup));
    }
    private static void cbGroupOnHidden(ChoiceBox cbGroup) {
        cbGroup.setOnHidden(event -> EventHandlerGroupNode.cbGroupOnHidden(cbGroup));
    }
}
