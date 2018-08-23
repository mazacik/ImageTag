package project.gui.event.listener.rightpane;

import javafx.scene.control.MenuItem;
import project.gui.component.rightpane.RightPane;
import project.gui.event.handler.rightpane.EventHandlerRightPaneContextMenuRemove;

public abstract class EventListenerRightPaneContextMenuRemove {
    public static void initialize() {
        MenuItem menuRemove = RightPane.getContextMenu().getMenuRemove();

        EventListenerRightPaneContextMenuRemove.onAction(menuRemove);
    }

    private static void onAction(MenuItem menuRemove) {
        menuRemove.setOnAction(event -> EventHandlerRightPaneContextMenuRemove.onAction());
    }
}
