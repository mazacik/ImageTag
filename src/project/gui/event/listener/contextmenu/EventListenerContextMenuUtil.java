package project.gui.event.listener.contextmenu;

public abstract class EventListenerContextMenuUtil {
    public static void initialize() {
        EventListenerContextMenuCopy.onAction();
        EventListenerContextMenuDelete.onAction();
    }
}
