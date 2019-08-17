package application.database.list;

import application.controller.Filter;
import application.database.object.DataObject;
import application.database.object.TagObject;
import application.main.Instances;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;

public class DataObjectList extends CustomList<DataObject> {
	public DataObjectList() {
	
	}
	public DataObjectList(Collection<? extends DataObject> c) {
		super(c);
	}
	
	public TagList getIntersectingTags() {
		if (this.isEmpty()) return new TagList();
		
		TagList intersectingTags = new TagList();
		DataObject lastObject = this.getLast();
		
		for (TagObject tagObject : this.getFirst().getTagList()) {
			for (DataObject dataObject : this) {
				if (dataObject.getTagList().contains(tagObject)) {
					if (dataObject.equals(lastObject)) {
						intersectingTags.add(tagObject);
					}
				} else break;
			}
		}
		
		return intersectingTags;
	}
	public TagList getSharedTags() {
		if (this.isEmpty()) return new TagList();
		
		TagList sharedTags = new TagList();
		
		for (DataObject dataObject : this) {
			for (TagObject tagObject : dataObject.getTagList()) {
				if (!sharedTags.contains(tagObject)) {
					sharedTags.add(tagObject);
				}
			}
		}
		
		return sharedTags;
	}
	
	public void sort() {
		super.sort(Comparator.comparing(DataObject::getName));
	}
	
	public CustomList<Integer> getMergeIDs() {
		CustomList<Integer> mergeIDs = new CustomList<>();
		for (DataObject dataObject : this) {
			if (dataObject.getMergeID() != 0) {
				mergeIDs.add(dataObject.getMergeID());
			}
		}
		return mergeIDs;
	}
	
	public DataObject getRandom(ArrayList<DataObject> arrayList) {
		DataObject dataObject = super.getRandom(arrayList);
		if (dataObject != null) {
			if (dataObject.getMergeID() == 0) return dataObject;
			else {
				//use sort-into-comparison algorithm if this is too slow or if too bored
				Filter filter = Instances.getFilter();
				CustomList<DataObject> mergeGroupObjectsThatAlsoPassFilter = new CustomList<>();
				for (DataObject mergeObject : dataObject.getMergeGroup()) {
					if (filter.contains(mergeObject)) mergeGroupObjectsThatAlsoPassFilter.add(mergeObject);
				}
				return super.getRandom(mergeGroupObjectsThatAlsoPassFilter); // run again in case of a group-hidden object being chosen
			}
		} else return null;
	}
}
