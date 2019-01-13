package control.target;

import control.reload.Reload;
import database.object.DataObject;
import javafx.scene.input.KeyCode;
import utils.MainUtil;

public class Target implements MainUtil {
    private DataObject currentTarget;
    private DataObject previousTarget;
    private int storePos = -1;

    public Target() {
        currentTarget = null;
        previousTarget = null;
    }
    public void set(DataObject dataObject) {
        /* store old target position */
        previousTarget = currentTarget;

        /* apply new target effect */
        currentTarget = dataObject;
        currentTarget.getBaseTile().generateEffect();

        /* remove old target effect */
        if (previousTarget != null) {
            previousTarget.getBaseTile().generateEffect();
        }

        tileView.adjustViewportToCurrentFocus();
        reload.notifyChangeIn(Reload.Control.TARGET);
    }
    public void move(KeyCode keyCode) {
        int columnCount = tileView.getColumnCount();
        int dataCountFilter = filter.size();
        int currentFocusPosition = filter.indexOf(currentTarget);

        int newFocusPosition = 0;
        if (currentTarget != null) newFocusPosition = filter.indexOf(currentTarget);

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
        this.storePos = filter.indexOf(currentTarget);
    }
    public void restorePosition() {
        if (storePos >= 0 && storePos < filter.size()) {
            set(filter.get(storePos));
        } else {
            set(null);
        }
    }

    public DataObject getCurrentTarget() {
        return currentTarget;
    }
}
