package application.data.list;

import application.control.Filter;
import application.data.object.DataObject;
import application.data.object.TagObject;
import application.main.Instances;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;

public class DataList extends CustomList<DataObject> {
	public DataList() {
	
	}
	public DataList(DataObject... dataObjects) {
		this.addAll(Arrays.asList(dataObjects));
	}
	public DataList(Collection<? extends DataObject> c) {
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
	
	public CustomList<Integer> getJointIDs() {
		CustomList<Integer> jointIDs = new CustomList<>();
		for (DataObject dataObject : this) {
			if (dataObject.getJointID() != 0) {
				jointIDs.add(dataObject.getJointID());
			}
		}
		return jointIDs;
	}
	
	public DataObject getRandom(CustomList<DataObject> customList) {
		DataObject dataObject = customList.getRandom();
		if (dataObject != null) {
			if (dataObject.getJointID() == 0) return dataObject;
			else {
				//use sort-into-comparison algorithm if this is too slow or if too bored
				Filter filter = Instances.getFilter();
				CustomList<DataObject> jointObjectsThatAlsoPassFilter = new CustomList<>();
				for (DataObject jointObject : dataObject.getJointObjects()) {
					if (filter.contains(jointObject)) jointObjectsThatAlsoPassFilter.add(jointObject);
				}
				return jointObjectsThatAlsoPassFilter.getRandom(); // run again in case of a group-hidden object being chosen
			}
		} else return null;
	}
}
