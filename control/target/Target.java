package control.target;

import control.reload.Reload;
import database.object.DataObject;
import javafx.scene.input.KeyCode;
import utils.MainUtil;

public class Target implements MainUtil {
    private DataObject currentFocus;
    private DataObject previousFocus;
    private int storePos = -1;

    public Target() {
        currentFocus = null;
        previousFocus = null;
    }
    public void set(DataObject dataObject) {
        /* store old target position */
        previousFocus = currentFocus;

        /* apply new target effect */
        currentFocus = dataObject;
        currentFocus.getBaseTile().generateEffect();

        /* remove old target effect */
        if (previousFocus != null) {
            previousFocus.getBaseTile().generateEffect();
        }

        tileView.adjustViewportToCurrentFocus();
        reload.notifyChangeIn(Reload.Control.FOCUS);
    }
    public void move(KeyCode keyCode) {
        int columnCount = tileView.getColumnCount();
        int dataCountFilter = filter.size();
        int currentFocusPosition = filter.indexOf(currentFocus);

        int newFocusPosition = 0;
        if (currentFocus != null) newFocusPosition = filter.indexOf(currentFocus);

        switch (keyCode) {
            case W:
                if (currentFocusPosition >= columnCount) newFocusPosition -= columnCount;
                break;
            case A:
                if (newFocusPosition > 0) newFocusPosition -= 1;
                break;
            case S:
                if (currentFocusPosition < dataCountFilter - (columnCount - (tileView.getRowCount() * columnCount - dataCountFilter))) {
                    newFocusPosition += columnCount;
                    if (newFocusPosition > dataCountFilter - 1) {
                        newFocusPosition = dataCountFilter - 1;
                    }
                }
                break;
            case D:
                if (newFocusPosition < dataCountFilter - 1) newFocusPosition += 1;
                break;
        }

        this.set(filter.get(newFocusPosition));
    }
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
