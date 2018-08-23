package project.gui.event.listener.datacontextmenu;

public abstract class EventListenerContextMenuUtil {
    public static void initialize() {
        EventListenerContextMenuCopy.onAction();
        EventListenerContextMenuDelete.onAction();
    }
}
