package project.gui.event.handler.gallerypane;

import javafx.scene.input.MouseEvent;
import project.control.FocusControl;
import project.control.SelectionControl;
import project.database.element.DataObject;
import project.gui.GUIInstance;
import project.gui.component.gallerypane.GalleryTile;

public abstract class EventHandlerGalleryTile {
    public static void onLeftClick(GalleryTile sender) {
        DataObject dataObject = sender.getParentDataObject();
        FocusControl.setFocus(dataObject);
        SelectionControl.swapSelectionStateOf(dataObject);
    }
    public static void onRightClick(GalleryTile sender, MouseEvent event) {
        DataObject dataObject = sender.getParentDataObject();
        FocusControl.setFocus(dataObject);
        SelectionControl.addDataElement(dataObject);

        GUIInstance.getDataObjectContextMenu().show(sender, event);
    }
}
