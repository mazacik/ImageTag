package project.control;

import javafx.scene.input.KeyCode;
import project.database.object.DataCollection;
import project.database.object.DataObject;
import project.gui.component.GUINode;
import project.gui.component.gallerypane.GalleryPane;

public abstract class FocusControl {
    /* vars */
    private static DataObject currentFocus = null;
    private static DataObject previousFocus = null;

    /* public */
    public static void setFocus(DataObject dataObject) {
        /* store old focus position */
        previousFocus = currentFocus;

        /* apply new focus effect */
        currentFocus = dataObject;
        currentFocus.getGalleryTile().generateEffect();

        /* remove old focus effect */
        if (previousFocus != null) {
            previousFocus.getGalleryTile().generateEffect();
        }

        ReloadControl.reload(GUINode.PREVIEWPANE);
    }
    public static void moveFocusByKeyCode(KeyCode keyCode) {
        DataCollection dataCollectionFiltered = FilterControl.getCollection();
        DataObject currentFocus = FocusControl.getCurrentFocus();
        if (currentFocus == null) {
            currentFocus = FilterControl.getCollection().get(0);
            FocusControl.setFocus(currentFocus);
        }

        int newFocusPosition = dataCollectionFiltered.indexOf(currentFocus);
        if (keyCode.equals(KeyCode.W)) {
            newFocusPosition -= GalleryPane.getColumnCount();
        } else if (keyCode.equals(KeyCode.A)) {
            newFocusPosition -= 1;
        } else if (keyCode.equals(KeyCode.S)) {
            newFocusPosition += GalleryPane.getColumnCount();
        } else if (keyCode.equals(KeyCode.D)) {
            newFocusPosition += 1;
        }

        if (newFocusPosition >= 0 && newFocusPosition < dataCollectionFiltered.size()) {
            FocusControl.setFocus(dataCollectionFiltered.get(newFocusPosition));
            GalleryPane.adjustViewportToCurrentFocus();
        }
    }

    /* get */
    public static DataObject getCurrentFocus() {
        return currentFocus;
    }
}
