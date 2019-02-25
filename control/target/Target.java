package control.target;

import control.reload.Reload;
import database.object.DataObject;
import javafx.scene.input.KeyCode;
import system.InstanceRepo;

public class Target implements InstanceRepo {
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

        tileView.adjustViewportToCurrentTarget();
        reload.notifyChangeIn(Reload.Control.TARGET);
    }
    public void move(KeyCode keyCode) {
        int columnCount = tileView.getColumnCount();
        int dataCountFilter = filter.size();
        int currentTargetPosition = filter.indexOf(currentTarget);

        int newTargetPosition = 0;
        if (currentTarget != null) newTargetPosition = filter.indexOf(currentTarget);

        switch (keyCode) {
            case W:
                if (currentTargetPosition >= columnCount) newTargetPosition -= columnCount;
                break;
            case A:
                if (newTargetPosition > 0) newTargetPosition -= 1;
                break;
            case S:
                if (currentTargetPosition < dataCountFilter - (columnCount - (tileView.getRowCount() * columnCount - dataCountFilter))) {
                    newTargetPosition += columnCount;
                    if (newTargetPosition > dataCountFilter - 1) {
                        newTargetPosition = dataCountFilter - 1;
                    }
                }
                break;
            case D:
                if (newTargetPosition < dataCountFilter - 1) newTargetPosition += 1;
                break;
        }

        this.set(filter.get(newTargetPosition));
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
