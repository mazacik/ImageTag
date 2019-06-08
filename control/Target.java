package control;

import database.object.DataObject;
import javafx.scene.input.KeyCode;
import lifecycle.InstanceManager;
import utils.enums.Direction;

public class Target {
    private DataObject currentTarget;
    private DataObject previousTarget;

    public Target() {
        currentTarget = null;
        previousTarget = null;
        storePos = -1;
    }

    public void set(DataObject dataObject) {
        if (dataObject != null) {
            /* store old target position */
            previousTarget = currentTarget;

            /* refresh new target effect */
            currentTarget = dataObject;
            currentTarget.generateTileEffect();

            /* remove old target effect */
            if (previousTarget != null)
                previousTarget.generateTileEffect();

            InstanceManager.getGalleryPane().adjustViewportToCurrentTarget();
            InstanceManager.getReload().flag(Reload.Control.TARGET);
        }
    }
    public void move(Direction direction) {
        if (currentTarget == null) return;

        int columnCount = InstanceManager.getGalleryPane().getColumnCount();
        int dataCountFilter = InstanceManager.getGalleryPane().getVisibleTiles().size();

        int currentTargetPosition;
        if (currentTarget.getMergeID() == 0) {
            currentTargetPosition = InstanceManager.getGalleryPane().getVisibleDataObjects().indexOf(currentTarget);
        } else {
            if (InstanceManager.getGalleryPane().getExpandedGroups().contains(currentTarget.getMergeID())) {
                currentTargetPosition = InstanceManager.getGalleryPane().getVisibleDataObjects().indexOf(currentTarget);
            } else {
                currentTargetPosition = InstanceManager.getGalleryPane().getVisibleDataObjects().indexOf(currentTarget.getMergeGroup().get(0));
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
                if (currentTargetPosition < dataCountFilter - (columnCount - (InstanceManager.getGalleryPane().getRowCount() * columnCount - dataCountFilter))) {
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

        this.set(InstanceManager.getGalleryPane().getVisibleDataObjects().get(newTargetPosition));
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

    private int storePos;
    public void storePosition() {
        this.storePos = InstanceManager.getGalleryPane().getVisibleDataObjects().indexOf(currentTarget);
    }
    public void restorePosition() {
        if (storePos >= 0 && storePos < InstanceManager.getGalleryPane().getVisibleDataObjects().size()) {
            this.set(InstanceManager.getGalleryPane().getVisibleDataObjects().get(storePos));
        } else {
            this.set(null);
        }

        if (InstanceManager.getSelect().isEmpty()) {
            InstanceManager.getSelect().set(InstanceManager.getGalleryPane().getVisibleDataObjects().get(storePos));
        }
    }

    public DataObject getCurrentTarget() {
        return currentTarget;
    }
}
