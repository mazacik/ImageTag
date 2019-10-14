package application.control;

import application.data.list.CustomList;
import application.data.list.DataList;
import application.data.object.DataObject;
import application.gui.panes.center.GalleryPane;
import application.main.Instances;
import application.misc.enums.Direction;
import javafx.scene.input.KeyCode;

import java.util.logging.Logger;

public class Target {
	public Target() {
	
	}
	
	private DataObject dataObject = null;
	public DataObject get() {
		if (dataObject == null && !Instances.getDataListMain().isEmpty()) {
			dataObject = Instances.getDataListMain().get(0);
		}
		return dataObject;
	}
	
	public void set(DataObject dataObject) {
		if (dataObject == null || dataObject == this.dataObject) return;
		
		Instances.getReload().requestTileEffect(this.dataObject);
		Instances.getReload().requestTileEffect(dataObject);
		
		this.dataObject = dataObject;
		
		Instances.getGalleryPane().adjustViewportToTarget();
		Instances.getReload().notify(Reload.Control.TARGET);
		Logger.getGlobal().info(dataObject.getName());
	}
	public void move(Direction direction) {
		if (dataObject == null) return;
		
		GalleryPane galleryPane = Instances.getGalleryPane();
		DataList dataObjects = galleryPane.getDataObjectsOfTiles();
		
		if (dataObjects.isEmpty()) return;
		
		int currentTargetIndex;
		if (dataObject.getJointID() == 0) {
			currentTargetIndex = dataObjects.indexOf(dataObject);
		} else {
			if (galleryPane.getExpandedGroups().contains(dataObject.getJointID())) {
				currentTargetIndex = dataObjects.indexOf(dataObject);
			} else {
				DataObject groupFirst = dataObject.getJointObjects().getFirst();
				if (dataObjects.contains(groupFirst)) {
					currentTargetIndex = dataObjects.indexOf(groupFirst);
				} else {
					currentTargetIndex = dataObjects.indexOf(dataObject);
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
		
		if (dataObject.getJointID() == 0) {
			storeObject = dataObject;
			storePos = visibleDataObjects.indexOf(dataObject);
		} else {
			if (expandedJointObjects.contains(dataObject.getJointID())) {
				storeObject = dataObject;
				storePos = visibleDataObjects.indexOf(dataObject);
			} else {
				storeObject = dataObject.getJointObjects().getFirst();
				storePos = visibleDataObjects.indexOf(storeObject);
			}
		}
	}
	public DataObject restorePosition() {
		DataList visibleObjects = Instances.getGalleryPane().getDataObjectsOfTiles();
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
