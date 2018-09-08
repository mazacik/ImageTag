package project.control;

import javafx.scene.input.KeyCode;
import project.database.element.DataObject;
import project.gui.component.gallerypane.GalleryPane;
import project.gui.component.previewpane.PreviewPane;
import project.gui.component.rightpane.RightPane;
import project.gui.component.toppane.TopPane;

import java.util.ArrayList;

public abstract class FocusControl {
    /* vars */
    private static DataObject currentFocus = null;
    private static DataObject previousFocus = null;

    /* public */
    public static void setFocus(DataObject dataObject) {
        /* store old focus position */
        if (currentFocus != null)
            previousFocus = currentFocus;

        /* apply new focus effect */
        currentFocus = dataObject;
        currentFocus.getGalleryTile().generateEffect();

        /* remove old focus effect */
        if (previousFocus != null) {
            previousFocus.getGalleryTile().generateEffect();
        }

        ReloadControl.reload(TopPane.class, PreviewPane.class, RightPane.class);
    }
    public static void moveFocusByKeyCode(KeyCode keyCode) {
        ArrayList<DataObject> databaseItemsFiltered = FilterControl.getCollection();
        DataObject focusedItem = FocusControl.getCurrentFocus();
        if (focusedItem == null) {
            DataObject firstItem = FilterControl.getCollection().get(0);
            FocusControl.setFocus(firstItem);
            focusedItem = firstItem;
        }

        int newFocusPosition = databaseItemsFiltered.indexOf(focusedItem);
        if (keyCode.equals(KeyCode.W)) {
            newFocusPosition -= GalleryPane.getColumnCount();
        } else if (keyCode.equals(KeyCode.A)) {
            newFocusPosition -= 1;
        } else if (keyCode.equals(KeyCode.S)) {
            newFocusPosition += GalleryPane.getColumnCount();
        } else if (keyCode.equals(KeyCode.D)) {
            newFocusPosition += 1;
        }

        if (newFocusPosition >= 0 && newFocusPosition < databaseItemsFiltered.size()) {
            FocusControl.setFocus(databaseItemsFiltered.get(newFocusPosition));
            GalleryPane.adjustViewportToCurrentFocus();
        }
    }

    /* get */
    public static DataObject getCurrentFocus() {
        return currentFocus;
    }
}
