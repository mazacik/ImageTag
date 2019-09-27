package application.controller;

import application.database.list.CustomList;
import application.database.list.DataObjectList;
import application.database.object.DataObject;
import application.gui.panes.center.GalleryPane;
import application.main.Instances;
import application.misc.enums.Direction;
import javafx.scene.input.KeyCode;

import java.util.logging.Logger;

public class Target {
	public Target() {
	
	}
	
	private DataObject currentTarget = null;
	public DataObject getCurrentTarget() {
		if (currentTarget == null && !Instances.getObjectListMain().isEmpty()) {
			currentTarget = Instances.getObjectListMain().get(0);
		}
		return currentTarget;
	}
	
	public void set(DataObject dataObject) {
		if (dataObject == null || dataObject == currentTarget) return;
		
		Instances.getReload().requestTileEffect(currentTarget);
		Instances.getReload().requestTileEffect(dataObject);
		
		currentTarget = dataObject;
		
		Instances.getGalleryPane().adjustViewportToTarget();
		Instances.getReload().notify(Reload.Control.TARGET);
		Logger.getGlobal().info(dataObject.getName());
	}
	public void move(Direction direction) {
		if (currentTarget == null) return;
		
		GalleryPane galleryPane = Instances.getGalleryPane();
		DataObjectList dataObjects = galleryPane.getDataObjectsOfTiles();
		
		if (dataObjects.isEmpty()) return;
		
		int currentTargetIndex;
		if (currentTarget.getJointID() == 0) {
			currentTargetIndex = dataObjects.indexOf(currentTarget);
		} else {
			if (galleryPane.getExpandedGroups().contains(currentTarget.getJointID())) {
				currentTargetIndex = dataObjects.indexOf(currentTarget);
			} else {
				DataObject groupFirst = currentTarget.getJointObjects().getFirst();
				if (dataObjects.contains(groupFirst)) {
					currentTargetIndex = dataObjects.indexOf(groupFirst);
				} else {
					currentTargetIndex = dataObjects.indexOf(currentTarget);
				}
			}
		}
		
		int columnCount = galleryPane.getColumnCount();
		
		int newTargetIndex = currentTargetIndex;
		switch (direction) {
			case UP:
				newTargetIndex -= columnCount;
				break;
			case LEFT:
				newTargetIndex -= 1;
				break;
			case DOWN:
				newTargetIndex += columnCount;
				break;
			case RIGHT:
				newTargetIndex += 1;
				break;
		}
		
		if (newTargetIndex < 0) newTargetIndex = 0;
		if (newTargetIndex >= dataObjects.size()) newTargetIndex = dataObjects.size() - 1;
		
		this.set(dataObjects.get(newTargetIndex));
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
	
	private DataObject storeObject = null;
	private int storePos = -1;
	public void storePosition() {
		CustomList<Integer> expandedJointObjects = Instances.getGalleryPane().getExpandedGroups();
		CustomList<DataObject> visibleDataObjects = Instances.getGalleryPane().getDataObjectsOfTiles();
		
		if (currentTarget.getJointID() == 0) {
			storeObject = currentTarget;
			storePos = visibleDataObjects.indexOf(currentTarget);
		} else {
			if (expandedJointObjects.contains(currentTarget.getJointID())) {
				storeObject = currentTarget;
				storePos = visibleDataObjects.indexOf(currentTarget);
			} else {
				storeObject = currentTarget.getJointObjects().getFirst();
				storePos = visibleDataObjects.indexOf(storeObject);
			}
		}
	}
	public DataObject restorePosition() {
		DataObjectList visibleObjects = Instances.getGalleryPane().getDataObjectsOfTiles();
		if (!visibleObjects.isEmpty()) {
			if (storeObject != null && visibleObjects.contains(storeObject)) {
				this.set(storeObject);
				return storeObject;
			} else if (storePos >= 0) {
				DataObject newTarget;
				
				if (storePos <= visibleObjects.size() - 1) newTarget = visibleObjects.get(storePos);
				else newTarget = visibleObjects.getLast();
				
				this.set(newTarget);
				return newTarget;
			}
		}
		return null;
	}
}
