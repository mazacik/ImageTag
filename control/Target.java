package control;

import database.list.CustomList;
import database.list.DataObjectList;
import database.object.DataObject;
import javafx.scene.input.KeyCode;
import main.InstanceManager;
import userinterface.main.center.GalleryPane;
import utils.enums.Direction;

public class Target {
	public Target() {
	
	}
	
	private DataObject currentTarget = null;
	public DataObject getCurrentTarget() {
		if (currentTarget == null && !InstanceManager.getObjectListMain().isEmpty()) {
			currentTarget = InstanceManager.getObjectListMain().get(0);
		}
		return currentTarget;
	}
	
	public void set(DataObject dataObject) {
		if (dataObject == null || dataObject == currentTarget) return;
		
		DataObject helper = currentTarget;
		currentTarget = dataObject;
		currentTarget.generateTileEffect();
		if (helper != null) helper.generateTileEffect();
		
		InstanceManager.getGalleryPane().adjustViewportToCurrentTarget();
		InstanceManager.getReload().notify(Reload.Control.TARGET);
		InstanceManager.getLogger().debug("Target set to " + dataObject.getName());
	}
	public void move(Direction direction) {
		if (currentTarget == null) return;
		
		GalleryPane galleryPane = InstanceManager.getGalleryPane();
		int columnCount = galleryPane.getColumnCount();
		int visibleTilesCount = galleryPane.getVisibleTiles().size();
		
		int currentTargetIndex;
		if (currentTarget.getMergeID() == 0) {
			currentTargetIndex = galleryPane.getVisibleDataObjects().indexOf(currentTarget);
		} else {
			if (galleryPane.getExpandedGroups().contains(currentTarget.getMergeID())) {
				currentTargetIndex = galleryPane.getVisibleDataObjects().indexOf(currentTarget);
			} else {
				currentTargetIndex = galleryPane.getVisibleDataObjects().indexOf(currentTarget.getMergeGroup().getFirst());
			}
		}
		int newTargetIndex = currentTargetIndex;
		switch (direction) {
			case UP:
				if (currentTargetIndex >= columnCount) newTargetIndex -= columnCount;
				break;
			case LEFT:
				if (newTargetIndex > 0) newTargetIndex -= 1;
				break;
			case DOWN:
				if (newTargetIndex + columnCount <= visibleTilesCount - 1) newTargetIndex += columnCount;
				break;
			case RIGHT:
				if (newTargetIndex < visibleTilesCount - 1) newTargetIndex += 1;
				break;
		}
		this.set(galleryPane.getVisibleDataObjects().get(newTargetIndex));
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
	
	private int storePos = -1;
	public void storePosition() {
		CustomList<Integer> expandedMergeGroups = InstanceManager.getGalleryPane().getExpandedGroups();
		CustomList<DataObject> visibleDataObjects = InstanceManager.getGalleryPane().getVisibleDataObjects();
		
		if (currentTarget.getMergeID() == 0) {
			storePos = visibleDataObjects.indexOf(currentTarget);
		} else {
			if (expandedMergeGroups.contains(currentTarget.getMergeID())) {
				storePos = visibleDataObjects.indexOf(currentTarget);
			} else {
				storePos = visibleDataObjects.indexOf(currentTarget.getMergeGroup().getFirst());
			}
		}
	}
	public void restorePosition() {
		if (storePos < 0) return;
		DataObjectList visibleObjects = InstanceManager.getGalleryPane().getVisibleDataObjects();
		if (!visibleObjects.isEmpty()) {
			if (storePos <= visibleObjects.size() - 1) this.set(visibleObjects.get(storePos));
			else this.set(visibleObjects.getLast());
		}
	}
}
