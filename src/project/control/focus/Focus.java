package project.control.focus;

import javafx.scene.input.KeyCode;
import project.MainUtil;
import project.database.object.DataObject;
import project.gui.component.GUINode;

public class Focus implements MainUtil {
    private DataObject currentFocus;
    private DataObject previousFocus;

    public Focus() {
        currentFocus = null;
        previousFocus = null;
    }

    public void set(DataObject dataObject) {
        /* store old focus position */
        previousFocus = currentFocus;

        /* apply new focus effect */
        currentFocus = dataObject;
        currentFocus.getGalleryTile().generateEffect();

        /* remove old focus effect */
        if (previousFocus != null) {
            previousFocus.getGalleryTile().generateEffect();
        }

        reload.queue(GUINode.PREVIEWPANE);
    }
    public void moveFocusByKeyCode(KeyCode keyCode) {
        DataObject currentFocus = getCurrentFocus();
        if (currentFocus == null) {
            currentFocus = filter.get(0);
            set(currentFocus);
        }

        int newFocusPosition = filter.indexOf(currentFocus);
        if (keyCode.equals(KeyCode.W)) {
            newFocusPosition -= galleryPane.getColumnCount();
        } else if (keyCode.equals(KeyCode.A)) {
            newFocusPosition -= 1;
        } else if (keyCode.equals(KeyCode.S)) {
            newFocusPosition += galleryPane.getColumnCount();
        } else if (keyCode.equals(KeyCode.D)) {
            newFocusPosition += 1;
        }

        if (newFocusPosition >= 0 && newFocusPosition < filter.size()) {
            set(filter.get(newFocusPosition));
            galleryPane.adjustViewportToCurrentFocus();
        }
    }

    public DataObject getCurrentFocus() {
        return currentFocus;
    }
}
