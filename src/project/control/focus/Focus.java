package project.control.focus;

import javafx.scene.input.KeyCode;
import project.database.object.DataObject;
import project.gui.component.GUINode;
import project.utils.MainUtil;

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

        galleryPane.adjustViewportToCurrentFocus();
        reload.queue(GUINode.PREVIEWPANE, GUINode.RIGHTPANE);
    }
    public void move(KeyCode keyCode) {
        int newFocusPosition = 0;
        if (currentFocus != null) {
            newFocusPosition = filter.indexOf(currentFocus);
        }

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
        }
    }

    private int storePos = -1;
    public void storePosition() {
        this.storePos = filter.indexOf(currentFocus);
    }
    public void restorePosition() {
        if (storePos >= 0 && storePos < filter.size()) {
            set(filter.get(storePos));
        } else {
            set(null);
        }
    }

    public DataObject getCurrentFocus() {
        return currentFocus;
    }
}
