package project.gui.event.listener;

import project.gui.event.listener.datacontextmenu.EventListenerContextMenuUtil;
import project.gui.event.listener.gallerypane.EventListenerGalleryPane;

public abstract class EventListenerUtil {
    public static void initialize() {
        EventListenerGalleryPane.initialize();

        EventListenerContextMenuUtil.initialize();
    }
}
