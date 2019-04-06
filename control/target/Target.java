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
        int dataCountFilter = tileView.getTilesLive().size();

        int currentTargetPosition;
        if (currentTarget.getMergeID() == 0) {
            currentTargetPosition = tileView.getDataObjects().indexOf(currentTarget);
        } else {
            if (tileView.getExpandedGroups().contains(currentTarget.getMergeID())) {
                currentTargetPosition = tileView.getDataObjects().indexOf(currentTarget);
            } else {
                currentTargetPosition = tileView.getDataObjects().indexOf(currentTarget.getMergeGroup().get(0));
            }
        }
        int newTargetPosition = currentTargetPosition;

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

        this.set(tileView.getDataObjects().get(newTargetPosition));
    }
    public void storePosition() {
        this.storePos = tileView.getDataObjects().indexOf(currentTarget);
    }
    public void restorePosition() {
        if (storePos >= 0 && storePos < tileView.getDataObjects().size()) {
            this.set(tileView.getDataObjects().get(storePos));
        } else {
            this.set(null);
        }
    }

    public DataObject getCurrentTarget() {
        return currentTarget;
    }
}
