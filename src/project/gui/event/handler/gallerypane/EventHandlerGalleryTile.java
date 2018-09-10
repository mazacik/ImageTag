package project.gui.event.handler.gallerypane;

import javafx.scene.input.MouseEvent;
import project.control.FocusControl;
import project.control.SelectionControl;
import project.database.object.DataObject;
import project.gui.GUIInstance;
import project.gui.component.gallerypane.GalleryTile;
import project.gui.custom.generic.DataObjectContextMenu;

public abstract class EventHandlerGalleryTile {
    private static final DataObjectContextMenu contextMenu = GUIInstance.getDataObjectContextMenu();

    public static void onLeftClick(GalleryTile sender) {
        DataObject dataObject = sender.getParentDataObject();
        FocusControl.setFocus(dataObject);
        SelectionControl.swapSelectionStateOf(dataObject);
        contextMenu.hide();
    }
    public static void onRightClick(GalleryTile sender, MouseEvent event) {
        DataObject dataObject = sender.getParentDataObject();
        FocusControl.setFocus(dataObject);
        SelectionControl.addDataObject(dataObject);
        contextMenu.show(sender, event);
    }
}
