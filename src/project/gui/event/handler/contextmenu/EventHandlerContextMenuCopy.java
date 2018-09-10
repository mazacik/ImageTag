package project.gui.event.handler.contextmenu;

import project.control.FocusControl;
import project.control.SelectionControl;
import project.control.Utils;
import project.database.object.DataObject;
import project.gui.GUIUtils;

public abstract class EventHandlerContextMenuCopy {
    public static void onAction() {
        DataObject dataObject;

        if (!GUIUtils.isPreviewFullscreen()) {
            dataObject = SelectionControl.getCollection().get(0);
        } else {
            dataObject = FocusControl.getCurrentFocus();
        }

        Utils.setClipboardContent(dataObject.getName());
    }
}