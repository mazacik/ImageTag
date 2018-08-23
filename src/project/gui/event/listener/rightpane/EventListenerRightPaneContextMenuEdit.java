package project.gui.event.listener.rightpane;

import javafx.scene.control.MenuItem;
import project.gui.component.rightpane.RightPane;
import project.gui.event.handler.rightpane.EventHandlerRightPaneContextMenuEdit;

public abstract class EventListenerRightPaneContextMenuEdit {
    public static void initialize() {
        MenuItem menuEdit = RightPane.getContextMenu().getMenuEdit();

        EventListenerRightPaneContextMenuEdit.onAction(menuEdit);
    }

    private static void onAction(MenuItem menuEdit) {
        menuEdit.setOnAction(event -> EventHandlerRightPaneContextMenuEdit.onAction());
    }
}
