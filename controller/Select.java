package application.controller;

import application.database.list.CustomList;
import application.database.list.DataObjectList;
import application.database.list.TagList;
import application.database.object.DataObject;
import application.database.object.TagObject;
import application.gui.stage.Stages;
import application.gui.stage.YesNoCancelStage;
import application.main.Instances;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

public class Select extends DataObjectList {
	private DataObject shiftStart = null;
	
	public boolean add(DataObject dataObject) {
		if (dataObject == null) return false;
		int jointID = dataObject.getJointID();
		if (jointID != 0 && !Instances.getGalleryPane().getExpandedGroups().contains(jointID)) {
			if (super.addAll(dataObject.getJointObjects())) {
				Instances.getReload().requestTileEffect(dataObject.getJointObjects());
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
			
			int jointID = dataObject.getJointID();
			if (jointID == 0 || expandedGroups.contains(jointID)) {
				super.add(dataObject);
				Instances.getReload().requestTileEffect(dataObject);
			} else {
				DataObjectList jointObject = dataObject.getJointObjects();
				super.addAll(jointObject);
				Instances.getReload().requestTileEffect(jointObject);
			}
		}
		
		Instances.getReload().notify(Reload.Control.SELECT);
		return true;
	}
	public boolean remove(DataObject dataObject) {
		if (dataObject == null) return false;
		
		int size = this.size();
		if (dataObject.getJointID() == 0 || Instances.getGalleryPane().getExpandedGroups().contains(dataObject.getJointID())) {
			Instances.getReload().requestTileEffect(dataObject);
			super.remove(dataObject);
		} else {
			Instances.getReload().requestTileEffect(dataObject.getJointObjects());
			this.removeAll(dataObject.getJointObjects());
		}
		
		if (size != this.size()) {
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
	
	public void jointObjectCreate() {
		CustomList<Integer> jointIDs = Instances.getObjectListMain().getJointIDs();
		int jointID;
		do jointID = new Random().nextInt();
		while (jointIDs.contains(jointID));
		
		YesNoCancelStage.Result result = Stages.getYesNoCancelStage()._show("Merge tags? (" + this.size() + " items selected)");
		if (result == YesNoCancelStage.Result.YES) {
			TagList tagList = new TagList();
			for (DataObject dataObject : this) {
				tagList.addAll(dataObject.getTagList());
			}
			for (DataObject dataObject : this) {
				dataObject.setJointID(jointID);
				dataObject.setTagList(tagList);
			}
		} else if (result == YesNoCancelStage.Result.NO) {
			for (DataObject dataObject : this) {
				dataObject.setJointID(jointID);
			}
		} else return;
		
		Instances.getTarget().set(this.getFirst());
		Instances.getReload().notify(Reload.Control.DATA, Reload.Control.TAGS);
	}
	public void jointObjectDiscard() {
		DataObject dataObject = Instances.getTarget().getCurrentTarget();
		if (dataObject.getJointID() != 0) {
			ArrayList<DataObject> jointObjects = dataObject.getJointObjects();
			for (DataObject jointObject : jointObjects) {
				jointObject.setJointID(0);
			}
		}
		Instances.getReload().notify(Reload.Control.DATA, Reload.Control.TAGS);
	}
	public boolean isSelectJoint() {
		int jointID = Instances.getTarget().getCurrentTarget().getJointID();
		if (jointID == 0) return false;
		for (DataObject dataObject : this) {
			if (dataObject.getJointID() != jointID) {
				return false;
			}
		}
		return true;
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
		this.forEach(dataObject -> dataObject.getTagList().add(tagObject));
		Instances.getReload().notify(Reload.Control.TAGS);
	}
	public void removeTagObject(TagObject tagObject) {
		this.forEach(dataObject -> dataObject.getTagList().remove(tagObject));
		Instances.getReload().notify(Reload.Control.TAGS);
	}
	
	public void setShiftStart(DataObject shiftStart) {
		this.shiftStart = shiftStart;
	}
}
