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
		int columnCount = galleryPane.getColumnCount();
		int visibleTilesCount = galleryPane.getTiles().size();
		
		int currentTargetIndex;
		if (currentTarget.getMergeID() == 0) {
			currentTargetIndex = galleryPane.getDataObjectsOfTiles().indexOf(currentTarget);
		} else {
			if (galleryPane.getExpandedGroups().contains(currentTarget.getMergeID())) {
				currentTargetIndex = galleryPane.getDataObjectsOfTiles().indexOf(currentTarget);
			} else {
				currentTargetIndex = galleryPane.getDataObjectsOfTiles().indexOf(currentTarget.getMergeGroup().getFirst());
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
		this.set(galleryPane.getDataObjectsOfTiles().get(newTargetIndex));
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
	
	//todo does this need to be in merging?
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
	public void restorePosition() {
		if (storePos < 0) return;
		DataObjectList visibleObjects = Instances.getGalleryPane().getDataObjectsOfTiles();
		if (!visibleObjects.isEmpty()) {
			if (storePos <= visibleObjects.size() - 1) this.set(visibleObjects.get(storePos));
			else this.set(visibleObjects.getLast());
		}
	}
}
