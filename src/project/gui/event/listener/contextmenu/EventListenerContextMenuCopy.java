package project.gui.event.listener.contextmenu;

import project.gui.GUIInstance;
import project.gui.event.handler.contextmenu.EventHandlerContextMenuCopy;

public abstract class EventListenerContextMenuCopy {
    public static void onAction() {
        GUIInstance.getDataObjectContextMenu().getMenuCopy().setOnAction(event -> EventHandlerContextMenuCopy.onAction());
    }
}
