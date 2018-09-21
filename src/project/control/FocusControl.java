package project.control;

import javafx.scene.input.KeyCode;
import project.database.object.DataCollection;
import project.database.object.DataObject;
import project.gui.component.GUINode;
import project.gui.component.gallerypane.GalleryPane;

public class FocusControl {
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

        Control.getReloadControl().reload(GUINode.PREVIEWPANE);
    }
    public void moveFocusByKeyCode(KeyCode keyCode) {
        DataCollection dataCollectionFiltered = Control.getFilterControl().getCollection();
        DataObject currentFocus = getCurrentFocus();
        if (currentFocus == null) {
            currentFocus = dataCollectionFiltered.get(0);
            setFocus(currentFocus);
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
            setFocus(dataCollectionFiltered.get(newFocusPosition));
            GalleryPane.adjustViewportToCurrentFocus();
        }
    }

    public DataObject getCurrentFocus() {
        return currentFocus;
    }
}
