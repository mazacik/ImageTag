package project.control;

import javafx.scene.input.KeyCode;
import project.database.element.DataElement;
import project.gui.component.GalleryPane;
import project.gui.component.PreviewPane;
import project.gui.component.RightPane;
import project.gui.component.TopPane;

import java.util.ArrayList;

public abstract class FocusControl {
    /* vars */
    private static DataElement currentFocus = null;
    private static DataElement previousFocus = null;

    /* public */
    public static void setFocus(DataElement dataElement) {
        /* store old focus position */
        if (currentFocus != null)
            previousFocus = currentFocus;

        /* apply new focus effect */
        currentFocus = dataElement;
        currentFocus.getGalleryTile().generateEffect();

        /* remove old focus effect */
        if (previousFocus != null) {
            previousFocus.getGalleryTile().generateEffect();
        }

        ReloadControl.requestReloadOf(TopPane.class, PreviewPane.class, RightPane.class);
    }
    public static void moveFocusByKeyCode(KeyCode keyCode) {
        ArrayList<DataElement> databaseItemsFiltered = FilterControl.getValidDataElements();
        DataElement focusedItem = FocusControl.getCurrentFocus();
        if (focusedItem == null) {
            DataElement firstItem = FilterControl.getValidDataElements().get(0);
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
            GalleryPane.adjustViewportToFocus();
        }
    }

    /* get */
    public static DataElement getCurrentFocus() {
        return currentFocus;
    }
}
