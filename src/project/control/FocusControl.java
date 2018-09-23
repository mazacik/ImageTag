package project.control;

import javafx.scene.input.KeyCode;
import project.MainUtils;
import project.database.object.DataCollection;
import project.database.object.DataObject;
import project.gui.component.GUINode;

public class FocusControl implements MainUtils {
    private DataObject currentFocus;
    private DataObject previousFocus;

    public FocusControl() {
        currentFocus = null;
        previousFocus = null;
    }

    public void setFocus(DataObject dataObject) {
        /* store old focus position */
        previousFocus = currentFocus;

        /* apply new focus effect */
        currentFocus = dataObject;
        currentFocus.getGalleryTile().generateEffect();

        /* remove old focus effect */
        if (previousFocus != null) {
            previousFocus.getGalleryTile().generateEffect();
        }

        reloadControl.reload(GUINode.PREVIEWPANE);
    }
    public void moveFocusByKeyCode(KeyCode keyCode) {
        DataCollection dataCollectionFiltered = filterControl.getCollection();
        DataObject currentFocus = getCurrentFocus();
        if (currentFocus == null) {
            currentFocus = dataCollectionFiltered.get(0);
            setFocus(currentFocus);
        }

        int newFocusPosition = dataCollectionFiltered.indexOf(currentFocus);
        if (keyCode.equals(KeyCode.W)) {
            newFocusPosition -= galleryPane.getColumnCount();
        } else if (keyCode.equals(KeyCode.A)) {
            newFocusPosition -= 1;
        } else if (keyCode.equals(KeyCode.S)) {
            newFocusPosition += galleryPane.getColumnCount();
        } else if (keyCode.equals(KeyCode.D)) {
            newFocusPosition += 1;
        }

        if (newFocusPosition >= 0 && newFocusPosition < dataCollectionFiltered.size()) {
            setFocus(dataCollectionFiltered.get(newFocusPosition));
            galleryPane.adjustViewportToCurrentFocus();
        }
    }

    public DataObject getCurrentFocus() {
        return currentFocus;
    }
}
