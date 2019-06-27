package control;

import database.list.ObjectList;
import database.list.TagList;
import database.object.DataObject;
import database.object.TagObject;
import main.InstanceManager;

import java.util.ArrayList;
import java.util.Random;

public class Select extends ObjectList {
	private DataObject shiftStart = null;
	
	public Select() {
	
	}
	
	public boolean add(DataObject dataObject) {
		if (dataObject == null) return false;
		if (dataObject.getMergeID() != 0 && !InstanceManager.getGalleryPane().getExpandedGroups().contains(dataObject.getMergeID())) {
			return this.addAll(dataObject.getMergeGroup());
		}
		if (super.add(dataObject)) {
			dataObject.generateTileEffect();
			InstanceManager.getReload().flag(Reload.Control.SELECT);
			return true;
		}
		return false;
	}
	public boolean addAll(ArrayList<DataObject> arrayList) {
		if (arrayList == null) return false;
		if (super.addAll(arrayList)) {
			arrayList.forEach(DataObject::generateTileEffect);
			InstanceManager.getReload().flag(Reload.Control.SELECT);
			return true;
		}
		return false;
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
		DataObject dataObject = ObjectList.getRandom(InstanceManager.getGalleryPane().getVisibleDataObjects());
		InstanceManager.getSelect().set(dataObject);
		InstanceManager.getTarget().set(dataObject);
	}
	public void clear() {
		ObjectList helper = new ObjectList();
		helper.addAll(this);
		super.clear();
		helper.forEach(DataObject::generateTileEffect);
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
		
		int mergeID = new Random().nextInt();
		for (DataObject dataObject : this) {
			dataObject.setMergeID(mergeID);
			dataObject.setTagList(tagList);
		}
		
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
		Filter filter = InstanceManager.getFilter();
		
		int indexFrom = filter.indexOf(shiftStart);
		int indexTo = filter.indexOf(shiftCurrent);
		
		int indexLower;
		int indexHigher;
		
		if (indexFrom > indexTo) {
			indexLower = indexTo;
			indexHigher = indexFrom;
		} else {
			indexLower = indexFrom;
			indexHigher = indexTo;
		}
		
		this.setAll(new ArrayList<>(filter.subList(indexLower, indexHigher + 1)));
	}
	
	public void addTagObject(TagObject tagObject) {
		if (!tagObject.isEmpty()) {
			if (!InstanceManager.getTagListMain().contains(tagObject)) {
				InstanceManager.getTagListMain().add(tagObject);
			}
			
			ObjectList selectHelper = new ObjectList();
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
