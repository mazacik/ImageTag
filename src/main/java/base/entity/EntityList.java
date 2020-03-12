package base.entity;

import base.CustomList;
import base.tag.Tag;
import base.tag.TagList;
import control.filter.Filter;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;

public class EntityList extends CustomList<Entity> {
	public EntityList() {
	
	}
	public EntityList(Collection<? extends Entity> c) {
		super(c);
	}
	public EntityList(Entity... entities) {
		super(Arrays.asList(entities));
	}
	public EntityList(CustomList<File> fileList) {
		fileList.forEach(file -> this.addImpl(new Entity(file)));
	}
	
	public void sort() {
		super.sort(Comparator.comparing(Entity::getName));
	}
	
	public Entity getRandom() {
		Entity entity = super.getRandomImpl();
		if (entity != null) {
			if (entity.getCollectionID() == 0) {
				return entity;
			} else {
				return Filter.getFilteredList(entity.getCollection()).getRandomImpl();
			}
		}
		return null;
	}
	
	public void addTag(Integer tagID) {
		this.forEach(entity -> entity.addTag(tagID));
	}
	public void removeTag(Integer tagID) {
		this.forEach(entity -> entity.removeTag(tagID));
	}
	public void clearTags() {
		this.forEach(Entity::clearTags);
	}
	
	public TagList getTagList() {
		TagList tagList = new TagList();
		this.forEach(entity -> tagList.addAllImpl(entity.getTagList(), true));
		return tagList;
	}
	public TagList getTagListIntersect() {
		if (!this.isEmpty()) {
			TagList tagListIntersect = new TagList();
			//check every tag of the first object
			for (Tag tag : this.getFirstImpl().getTagList()) {
				//check if all objects contain the tagID
				for (Entity entity : this) {
					if (entity.getTagList().contains(tag)) {
						//if the last object contains the tag, all before do too, add
						if (entity.equals(this.getLastImpl())) {
							tagListIntersect.addImpl(tag);
						}
					} else {
						//if any of the objects doesn't contain the tag, break
						break;
					}
				}
			}
			return tagListIntersect;
		} else {
			return new TagList();
		}
	}
	
	private static class Loader {
		private static final EntityList INSTANCE = new EntityList();
	}
	public static EntityList getMain() {
		return Loader.INSTANCE;
	}
}
