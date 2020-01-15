package base.tag;

import base.CustomList;

import java.util.Comparator;

public class TagList extends CustomList<Tag> {
	public TagList() {
	
	}
	
	public void sort() {
		super.sort(Comparator.comparing(Tag::getStringValue));
	}
	
	public Tag getTag(Integer id) {
		for (Tag tag : this) {
			if (tag.getID() == id) {
				return tag;
			}
		}
		
		return null;
	}
	public Tag getTag(String stringValue) {
		for (Tag tag : this) {
			if (tag.getStringValue().equals(stringValue)) {
				return tag;
			}
		}
		
		return null;
	}
	public TagList getTagsContaining(String query) {
		TagList results = new TagList();
		for (Tag tag : this) {
			if (tag.getStringValue().contains(query)) {
				results.add(tag);
			}
		}
		return results;
	}
	
	private static class Loader {
		private static final TagList INSTANCE = new TagList();
	}
	public static TagList getMain() {
		return Loader.INSTANCE;
	}
}
