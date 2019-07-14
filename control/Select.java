package control;

import database.list.CustomList;
import database.list.DataObjectList;
import database.list.TagList;
import database.object.DataObject;
import database.object.TagObject;
import main.InstanceManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

public class Select extends DataObjectList {
	private DataObject shiftStart = null;
	
	public boolean add(DataObject dataObject) {
		if (dataObject == null) return false;
		
		ArrayList<Integer> expandedGroups = InstanceManager.getGalleryPane().getExpandedGroups();
		int mergeID = dataObject.getMergeID();
		if (mergeID != 0 && !expandedGroups.contains(mergeID)) return super.addAll(dataObject.getMergeGroup());
		
		if (super.add(dataObject)) {
			InstanceManager.getReload().flag(Reload.Control.SELECT);
			return true;
		}
		
		return false;
	}
	public boolean addAll(Collection<? extends DataObject> c) {
		if (c == null || c.isEmpty()) return false;
		
		ArrayList<Integer> expandedGroups = InstanceManager.getGalleryPane().getExpandedGroups();
		
		for (DataObject dataObject : c) {
			if (dataObject == null || this.contains(dataObject)) continue;
			
			int mergeID = dataObject.getMergeID();
			if (mergeID == 0 || expandedGroups.contains(mergeID)) {
				super.add(dataObject);
				dataObject.generateTileEffect();
			} else {
				CustomList<DataObject> mergeGroup = dataObject.getMergeGroup();
				super.addAll(mergeGroup);
				mergeGroup.getFirst().generateTileEffect();
			}
		}
		
		InstanceManager.getReload().flag(Reload.Control.SELECT);
		return true;
	}
	public boolean remove(DataObject dataObject) {
		if (dataObject == null) return false;
		if (dataObject.getMergeID() != 0 && !InstanceManager.getGalleryPane().getExpandedGroups().contains(dataObject.getMergeID())) {
			return this.removeAll(dataObject.getMergeGroup());
		}
		if (super.remove(dataObject)) {
			dataObject.generateTileEffect();
			InstanceManager.getReload().flag(Reload.Control.SELECT);
			return true;
		}
		return false;
	}
	public boolean removeAll(ArrayList<DataObject> arrayList) {
		if (arrayList == null) return false;
		if (super.removeAll(arrayList)) {
			arrayList.forEach(DataObject::generateTileEffect);
			InstanceManager.getReload().flag(Reload.Control.SELECT);
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
		DataObject dataObject = getRandom(InstanceManager.getGalleryPane().getVisibleDataObjects());
		InstanceManager.getSelect().set(dataObject);
		InstanceManager.getTarget().set(dataObject);
	}
	public void clear() {
		DataObjectList helper = new DataObjectList();
		helper.addAll(this);
		super.clear();
		ArrayList<DataObject> visibleTiles = InstanceManager.getGalleryPane().getVisibleDataObjects();
		helper.forEach(dataObject -> {
			if (visibleTiles.contains(dataObject)) {
				dataObject.generateTileEffect();
			}
		});
		InstanceManager.getReload().flag(Reload.Control.SELECT);
	}
	public void swapState(DataObject dataObject) {
		if (super.contains(dataObject)) {
			this.remove(dataObject);
		} else {
			this.add(dataObject);
		}
	}
	public void merge() {
		TagList tagList = new TagList();
		for (DataObject dataObject : this) {
			for (TagObject tagObject : dataObject.getTagList()) {
				if (!tagList.contains(tagObject)) {
					tagList.add(tagObject);
				}
			}
		}
		
		CustomList<Integer> mergeIDs = InstanceManager.getObjectListMain().getMergeIDs();
		int mergeID;
		do mergeID = new Random().nextInt();
		while (mergeIDs.contains(mergeID));
		
		for (DataObject dataObject : this) {
			dataObject.setMergeID(mergeID);
			dataObject.setTagList(tagList);
		}
		
		InstanceManager.getTarget().set(this.getFirst());
		
		InstanceManager.getReload().flag(Reload.Control.OBJ, Reload.Control.TAG);
	}
	public void unmerge() {
		DataObject dataObject = InstanceManager.getTarget().getCurrentTarget();
		if (dataObject.getMergeID() != 0) {
			ArrayList<DataObject> mergeGroup = dataObject.getMergeGroup();
			for (DataObject mergeObject : mergeGroup) {
				mergeObject.setMergeID(0);
			}
		}
		InstanceManager.getReload().flag(Reload.Control.OBJ, Reload.Control.TAG);
	}
	
	public void shiftSelectTo(DataObject shiftCurrent) {
		CustomList<DataObject> dataObjects = InstanceManager.getGalleryPane().getVisibleDataObjects();
		
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
		if (!tagObject.isEmpty()) {
			if (!InstanceManager.getTagListMain().contains(tagObject)) {
				InstanceManager.getTagListMain().add(tagObject);
			}
			
			DataObjectList selectHelper = new DataObjectList();
			selectHelper.addAll(InstanceManager.getSelect());
			
			TagList tagList;
			for (DataObject dataObject : selectHelper) {
				tagList = dataObject.getTagList();
				if (!tagList.contains(tagObject)) {
					tagList.add(tagObject);
				}
			}
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
