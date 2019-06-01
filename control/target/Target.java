package control.target;

import control.reload.Reload;
import database.object.DataObject;
import javafx.scene.input.KeyCode;
import lifecycle.InstanceManager;
import system.Direction;
import user_interface.singleton.center.BaseTile;

public class Target {
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

        /* refresh new target effect */
        currentTarget = dataObject;
        BaseTile baseTile = currentTarget.getBaseTile();
        if (baseTile != null) baseTile.generateEffect();

        /* remove old target effect */
        if (previousTarget != null) {
            baseTile = previousTarget.getBaseTile();
            if (baseTile != null) baseTile.generateEffect();
        }

        InstanceManager.getTileView().adjustViewportToCurrentTarget();
        InstanceManager.getReload().notifyChangeIn(Reload.Control.TARGET);
    }
    public void move(Direction direction) {
        if (currentTarget == null) return;

        int columnCount = InstanceManager.getTileView().getColumnCount();
        int dataCountFilter = InstanceManager.getTileView().getVisibleTiles().size();

        int currentTargetPosition;
        if (currentTarget.getMergeID() == 0) {
            currentTargetPosition = InstanceManager.getTileView().getVisibleDataObjects().indexOf(currentTarget);
        } else {
            if (InstanceManager.getTileView().getExpandedGroups().contains(currentTarget.getMergeID())) {
                currentTargetPosition = InstanceManager.getTileView().getVisibleDataObjects().indexOf(currentTarget);
            } else {
                currentTargetPosition = InstanceManager.getTileView().getVisibleDataObjects().indexOf(currentTarget.getMergeGroup().get(0));
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
                if (currentTargetPosition < dataCountFilter - (columnCount - (InstanceManager.getTileView().getRowCount() * columnCount - dataCountFilter))) {
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

        this.set(InstanceManager.getTileView().getVisibleDataObjects().get(newTargetPosition));
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
        this.storePos = InstanceManager.getTileView().getVisibleDataObjects().indexOf(currentTarget);
    }
    public void restorePosition() {
        if (storePos >= 0 && storePos < InstanceManager.getTileView().getVisibleDataObjects().size()) {
            this.set(InstanceManager.getTileView().getVisibleDataObjects().get(storePos));
        } else {
            this.set(null);
        }

        if (InstanceManager.getSelect().isEmpty()) {
            InstanceManager.getSelect().set(InstanceManager.getTileView().getVisibleDataObjects().get(storePos));
        }
    }

    public DataObject getCurrentTarget() {
        return currentTarget;
    }
}
