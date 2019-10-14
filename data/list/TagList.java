package application.data.list;

import application.data.object.TagObject;

import java.util.ArrayList;
import java.util.Comparator;

public class TagList extends CustomList<TagObject> {
	public boolean contains(TagObject tagObject) {
		String group = tagObject.getGroup();
		String name = tagObject.getName();
		for (TagObject iterator : this) {
			String iteratorGroup = iterator.getGroup();
			String iteratorName = iterator.getName();
			if (group.equals(iteratorGroup) && name.equals(iteratorName)) return true;
		}
		return false;
	}
	public void sort() {
		super.sort(Comparator.comparing(TagObject::getFull));
	}
	
	public ArrayList<String> getGroups() {
		ArrayList<String> groups = new ArrayList<>();
		for (TagObject iterator : this) {
			if (!groups.contains(iterator.getGroup())) {
				groups.add(iterator.getGroup());
			}
		}
		groups.sort(Comparator.naturalOrder());
		return groups;
	}
	public ArrayList<String> getNames(String group) {
		ArrayList<String> names = new ArrayList<>();
		for (TagObject iterator : this) {
			String iteratorGroup = iterator.getGroup();
			String iteratorName = iterator.getName();
			if (iteratorGroup.equals(group) && !names.contains(iteratorName)) {
				names.add(iteratorName);
			}
		}
		return names;
	}
	
	public TagObject getTagObject(String group, String name) {
		for (TagObject iterator : this) {
			String iteratorGroup = iterator.getGroup();
			String iteratorName = iterator.getName();
			if (group.equals(iteratorGroup) && name.equals(iteratorName)) {
				return iterator;
			}
		}
		return null;
	}
	public TagObject getTagObject(TagObject tagObject) {
		String tagObjectGroup = tagObject.getGroup();
		String tagObjectName = tagObject.getName();
		return getTagObject(tagObjectGroup, tagObjectName);
	}
}
