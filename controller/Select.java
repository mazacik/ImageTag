package application.controller;

import application.database.list.CustomList;
import application.database.list.DataObjectList;
import application.database.list.TagList;
import application.database.object.DataObject;
import application.database.object.TagObject;
import application.gui.stage.Stages;
import application.main.Instances;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

public class Select extends DataObjectList {
	private DataObject shiftStart = null;
	
	public boolean add(DataObject dataObject) {
		if (dataObject == null) return false;
		int mergeID = dataObject.getMergeID();
		if (mergeID != 0 && !Instances.getGalleryPane().getExpandedGroups().contains(mergeID)) {
			if (super.addAll(dataObject.getMergeGroup())) {
				Instances.getReload().requestTileEffect(dataObject.getMergeGroup());
				Instances.getReload().notify(Reload.Control.SELECT);
				return true;
			}
		} else {
			if (super.add(dataObject)) {
				Instances.getReload().requestTileEffect(dataObject);
				Instances.getReload().notify(Reload.Control.SELECT);
				return true;
			}
		}
		return false;
	}
	public boolean addAll(Collection<? extends DataObject> c) {
		if (c == null || c.isEmpty()) return false;
		
		ArrayList<Integer> expandedGroups = Instances.getGalleryPane().getExpandedGroups();
		
		for (DataObject dataObject : c) {
			if (dataObject == null || this.contains(dataObject)) continue;
			
			int mergeID = dataObject.getMergeID();
			if (mergeID == 0 || expandedGroups.contains(mergeID)) {
				super.add(dataObject);
				Instances.getReload().requestTileEffect(dataObject);
			} else {
				DataObjectList mergeGroup = dataObject.getMergeGroup();
				super.addAll(mergeGroup);
				Instances.getReload().requestTileEffect(mergeGroup);
			}
		}
		
		Instances.getReload().notify(Reload.Control.SELECT);
		return true;
	}
	public boolean remove(DataObject dataObject) {
		if (dataObject == null) return false;
		if (dataObject.getMergeID() != 0 && !Instances.getGalleryPane().getExpandedGroups().contains(dataObject.getMergeID())) {
			return this.removeAll(dataObject.getMergeGroup());
		}
		if (super.remove(dataObject)) {
			Instances.getReload().requestTileEffect(dataObject);
			Instances.getReload().notify(Reload.Control.SELECT);
			return true;
		}
		return false;
	}
	public boolean removeAll(ArrayList<DataObject> arrayList) {
		if (arrayList == null) return false;
		if (super.removeAll(arrayList)) {
			for (DataObject dataObject : arrayList) Instances.getReload().requestTileEffect(dataObject);
			Instances.getReload().notify(Reload.Control.SELECT);
			return true;
		}
		return false;
	}
	
	public void set(DataObject dataObject) {
		this.clear();
		this.add(dataObject);
	}
	public void setAll(ArrayList<DataObject> dataObjects) {
		this.clear();
		this.addAll(dataObjects);
	}
	public void setRandom() {
		DataObject dataObject = getRandom(Instances.getGalleryPane().getDataObjectsOfTiles());
		this.set(dataObject);
		Instances.getTarget().set(dataObject);
	}
	public void clear() {
		Instances.getReload().requestTileEffect(this);
		super.clear();
		Instances.getReload().notify(Reload.Control.SELECT);
	}
	public void swapState(DataObject dataObject) {
		if (super.contains(dataObject)) {
			this.remove(dataObject);
		} else {
			this.add(dataObject);
		}
	}
	public void merge() {
		CustomList<Integer> mergeIDs = Instances.getObjectListMain().getMergeIDs();
		int mergeID;
		do mergeID = new Random().nextInt();
		while (mergeIDs.contains(mergeID));
		
		if (Stages.getYesNoStage()._show("Do you also want to merge the tags of these items?")) {
			TagList tagList = new TagList();
			for (DataObject dataObject : this) {
				tagList.addAll(dataObject.getTagList());
			}
			for (DataObject dataObject : this) {
				dataObject.setMergeID(mergeID);
				dataObject.setTagList(tagList);
			}
		}
		
		Instances.getTarget().set(this.getFirst());
		Instances.getReload().notify(Reload.Control.OBJ, Reload.Control.TAG);
	}
	public void unmerge() {
		DataObject dataObject = Instances.getTarget().getCurrentTarget();
		if (dataObject.getMergeID() != 0) {
			ArrayList<DataObject> mergeGroup = dataObject.getMergeGroup();
			for (DataObject mergeObject : mergeGroup) {
				mergeObject.setMergeID(0);
			}
		}
		Instances.getReload().notify(Reload.Control.OBJ, Reload.Control.TAG);
	}
	
	public void shiftSelectTo(DataObject shiftCurrent) {
		CustomList<DataObject> dataObjects = Instances.getGalleryPane().getDataObjectsOfTiles();
		
		int indexFrom = dataObjects.indexOf(shiftStart);
		int indexTo = dataObjects.indexOf(shiftCurrent);
		
		int indexLower;
		int indexHigher;
		
		if (indexFrom > indexTo) {
			indexLower = indexTo;
			indexHigher = indexFrom;
		} else {
			indexLower = indexFrom;
			indexHigher = indexTo;
		}
		
		this.addAll(dataObjects.subList(indexLower, indexHigher + 1));
	}
	
	public void addTagObject(TagObject tagObject) {
		for (DataObject dataObject : this) {
			dataObject.getTagList().add(tagObject);
		}
	}
	public void removeTagObject(TagObject tagObject) {
		for (DataObject dataObject : this) {
			dataObject.getTagList().remove(tagObject);
		}
	}
	
	public void setShiftStart(DataObject shiftStart) {
		this.shiftStart = shiftStart;
	}
}
