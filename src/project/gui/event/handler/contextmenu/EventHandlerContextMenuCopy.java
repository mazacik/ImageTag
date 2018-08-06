package project.gui.event.handler.contextmenu;

import project.control.FocusControl;
import project.control.SelectionControl;
import project.control.Utils;
import project.database.element.DataObject;

public abstract class EventHandlerContextMenuCopy {
    public static void onAction() {
        DataObject dataObject;
        if (SelectionControl.isSelectionSingleElement()) {
            dataObject = SelectionControl.getDataObjects().get(0);
        } else {
            dataObject = FocusControl.getCurrentFocus();
        }
        Utils.setClipboardContent(dataObject.getName());
    }
}