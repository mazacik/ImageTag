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
		
		Instances.getGalleryPane().adjustViewportToCurrentTarget();
		Instances.getReload().notify(Reload.Control.TARGET);
		Logger.getGlobal().info("Target set to " + dataObject.getName());
	}
	public void move(Direction direction) {
		if (currentTarget == null) return;
		
		GalleryPane galleryPane = Instances.getGalleryPane();
		DataObjectList dataObjects = galleryPane.getDataObjectsOfTiles();
		
		if (dataObjects.isEmpty()) return;
		
		int currentTargetIndex;
		if (currentTarget.getMergeID() == 0) {
			currentTargetIndex = dataObjects.indexOf(currentTarget);
		} else {
			if (galleryPane.getExpandedGroups().contains(currentTarget.getMergeID())) {
				currentTargetIndex = dataObjects.indexOf(currentTarget);
			} else {
				currentTargetIndex = dataObjects.indexOf(currentTarget.getMergeGroup().getFirst());
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
	
	private int storePos = -1;
	public void storePosition() {
		CustomList<Integer> expandedMergeGroups = Instances.getGalleryPane().getExpandedGroups();
		CustomList<DataObject> visibleDataObjects = Instances.getGalleryPane().getDataObjectsOfTiles();
		
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
	public DataObject restorePosition() {
		if (storePos < 0) return null;
		DataObjectList visibleObjects = Instances.getGalleryPane().getDataObjectsOfTiles();
		if (!visibleObjects.isEmpty()) {
			DataObject newTarget;
			
			if (storePos <= visibleObjects.size() - 1) newTarget = visibleObjects.get(storePos);
			else newTarget = visibleObjects.getLast();
			
			this.set(newTarget);
			return newTarget;
		}
		return null;
	}
}
