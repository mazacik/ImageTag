package project.gui.event.listener.rightpane;

import javafx.scene.control.ChoiceBox;
import project.gui.component.rightpane.RightPane;
import project.gui.event.handler.rightpane.EventHandlerNameNode;

public abstract class EventListenerNameNode {
    public static void initialize() {
        ChoiceBox cbName = RightPane.getCbName();

        EventListenerNameNode.cbNameOnShowing(cbName);
        EventListenerNameNode.cbNameOnHidden(cbName);
    }

    private static void cbNameOnShowing(ChoiceBox cbName) {
        cbName.setOnShowing(event -> EventHandlerNameNode.cbNameOnShowing(cbName));
    }
    private static void cbNameOnHidden(ChoiceBox cbName) {
        cbName.setOnHidden(event -> EventHandlerNameNode.cbNameOnHidden(cbName));
    }
}
