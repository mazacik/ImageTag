package base.tag;

import base.CustomList;

import java.util.Collection;
import java.util.Comparator;

public class TagList extends CustomList<Tag> {
	public TagList() {
	
	}
	public TagList(Tag[] c) {
		super(c);
	}
	public TagList(Collection<? extends Tag> c) {
		super(c);
	}
	
	public void sort() {
		super.sort(Comparator.comparing(Tag::getStringValue));
	}
	
	public Tag getTag(Integer tagID) {
		for (Tag tag : this) {
			if (tag.getID() == tagID) {
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
	public TagList getTagsStartingWith(String query) {
		TagList results = new TagList();
		for (Tag tag : this) {
			if (tag.getStringValue().startsWith(query)) {
				results.addImpl(tag);
			}
		}
		return results;
	}
	
	public boolean isAnyTagSubstringOf(CustomList<String> levels) {
		String query = new Tag(levels).getStringValue().toLowerCase();
		for (Tag tag : this) {
			if (levels.size() > tag.getLevels().size()) {
				if (query.startsWith(tag.getStringValue().toLowerCase())) {
					return true;
				}
			}
		}
		return false;
	}
	public boolean doesAnyTagStartWith(CustomList<String> levels) {
		String query = new Tag(levels).getStringValue().toLowerCase();
		for (Tag tag : this) {
			if (tag.getStringValue().toLowerCase().startsWith(query)) {
				return true;
			}
		}
		return false;
	}
	
	private static class Loader {
		private static final TagList INSTANCE = new TagList();
	}
	public static TagList getMain() {
		return Loader.INSTANCE;
	}
}
