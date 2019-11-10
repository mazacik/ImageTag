package application.base.tag;

import application.base.CustomList;

import java.util.Collection;
import java.util.Comparator;

public class TagList extends CustomList<Tag> {
	public TagList() {
	
	}
	public TagList(Collection<? extends Tag> c) {
		super(c);
	}
	
	public boolean containsEqualTo(Tag tag) {
		String group = tag.getGroup();
		String name = tag.getName();
		
		for (Tag iterator : this) {
			String iteratorGroup = iterator.getGroup();
			String iteratorName = iterator.getName();
			
			if (group.equals(iteratorGroup) && name.equals(iteratorName)) {
				return true;
			}
		}
		
		return false;
	}
	public void sort() {
		super.sort(Comparator.comparing(Tag::getFull));
	}
	
	public CustomList<String> getGroups() {
		CustomList<String> groups = new CustomList<>();
		
		for (Tag tag : this) {
			groups.add(tag.getGroup());
		}
		
		groups.sort(Comparator.naturalOrder());
		
		return groups;
	}
	public CustomList<String> getNames(String group) {
		CustomList<String> names = new CustomList<>();
		for (Tag tag : this) {
			if (tag.getGroup().equals(group)) {
				names.add(tag.getName());
			}
		}
		
		return names;
	}
	
	public Tag getTag(String group, String name) {
		for (Tag tag : this) {
			if (group.equals(tag.getGroup()) && name.equals(tag.getName())) {
				return tag;
			}
		}
		
		return null;
	}
	public Tag getTag(Tag tag) {
		return getTag(tag.getGroup(), tag.getName());
	}
}
