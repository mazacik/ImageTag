package control.target;

import control.reload.Reload;
import database.object.DataObject;
import javafx.scene.input.KeyCode;
import system.InstanceRepo;
import user_interface.factory.node.popup.Direction;

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
    public void move(Direction direction) {
        if (currentTarget == null) return;

        int columnCount = tileView.getColumnCount();
        int dataCountFilter = tileView.getVisibleTiles().size();

        int currentTargetPosition;
        if (currentTarget.getMergeID() == 0) {
            currentTargetPosition = tileView.getVisibleDataObjects().indexOf(currentTarget);
        } else {
            if (tileView.getExpandedGroups().contains(currentTarget.getMergeID())) {
                currentTargetPosition = tileView.getVisibleDataObjects().indexOf(currentTarget);
            } else {
                currentTargetPosition = tileView.getVisibleDataObjects().indexOf(currentTarget.getMergeGroup().get(0));
            }
        }
        int newTargetPosition = currentTargetPosition;

        switch (direction) {
            case UP:
                if (currentTargetPosition >= columnCount) newTargetPosition -= columnCount;
                break;
            case LEFT:
                if (newTargetPosition > 0) newTargetPosition -= 1;
                break;
            case DOWN:
                if (currentTargetPosition < dataCountFilter - (columnCount - (tileView.getRowCount() * columnCount - dataCountFilter))) {
                    newTargetPosition += columnCount;
                    if (newTargetPosition > dataCountFilter - 1) {
                        newTargetPosition = dataCountFilter - 1;
                    }
                }
                break;
            case RIGHT:
                if (newTargetPosition < dataCountFilter - 1) newTargetPosition += 1;
                break;
        }

        this.set(tileView.getVisibleDataObjects().get(newTargetPosition));
    }
    public void move(KeyCode keyCode) {
        switch (keyCode) {
            case W:
                move(Direction.UP);
                break;
            case A:
                move(Direction.LEFT);
                break;
            case S:
                move(Direction.DOWN);
                break;
            case D:
                move(Direction.RIGHT);
                break;
        }
    }

    public void storePosition() {
        this.storePos = tileView.getVisibleDataObjects().indexOf(currentTarget);
    }
    public void restorePosition() {
        if (storePos >= 0 && storePos < tileView.getVisibleDataObjects().size()) {
            this.set(tileView.getVisibleDataObjects().get(storePos));
        } else {
            this.set(null);
        }

        if (select.isEmpty()) {
            select.set(tileView.getVisibleDataObjects().get(storePos));
        }
    }

    public DataObject getCurrentTarget() {
        return currentTarget;
    }
}
