package base.tag;

import base.CustomList;

import java.util.Collection;
import java.util.Comparator;

public class TagList extends CustomList<Tag> {
	public TagList() {
	
	}
	public TagList(Collection<? extends Tag> c) {
		super(c);
	}
	
	public void sort() {
		super.sort(Comparator.comparing(Tag::getString));
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
	
	public Tag getTag(Integer id) {
		for (Tag tag : this) {
			if (tag.getID() == id) {
				return tag;
			}
		}
		
		return null;
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
	
	private static class Loader {
		private static final TagList INSTANCE = new TagList();
	}
	public static TagList getMain() {
		return Loader.INSTANCE;
	}
}
