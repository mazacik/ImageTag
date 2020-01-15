package base.entity;

import base.CustomList;
import base.tag.Tag;
import base.tag.TagList;
import control.filter.Filter;
import control.reload.Notifier;
import control.reload.Reload;

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
		fileList.forEach(file -> this.add(new Entity(file)));
	}
	
	public void sort() {
		super.sort(Comparator.comparing(Entity::getName));
	}
	
	public Entity getRandom() {
		Entity entity = super.getRandom();
		if (entity != null) {
			if (EntityCollectionUtil.hasOpenOrNoCollection(entity)) {
				return entity;
			} else {
				return new CustomList<>(Filter.applyTo(entity.getCollection())).getRandom();
			}
		}
		return null;
	}
	
	public void addTag(Integer tagID) {
		this.forEach(entity -> entity.addTag(tagID));
		Reload.notify(Notifier.TAGS_OF_SELECT);
	}
	public void removeTag(Integer tagID) {
		this.forEach(entity -> entity.removeTag(tagID));
		Reload.notify(Notifier.TAGS_OF_SELECT);
	}
	
	public CustomList<Integer> getTagIDs() {
		CustomList<Integer> tagIDs = new CustomList<>();
		this.forEach(entity -> tagIDs.addAll(entity.getTagIDs(), true));
		return tagIDs;
	}
	public TagList getTagList() {
		TagList tagList = new TagList();
		this.forEach(entity -> tagList.addAll(entity.getTagList(), true));
		return tagList;
	}
	public TagList getTagListIntersect() {
		if (!this.isEmpty()) {
			TagList tagListIntersect = new TagList();
			//check every tag of the first object
			for (Tag tag : this.getFirst().getTagList()) {
				//check if all objects contain the tagID
				for (Entity entity : this) {
					if (entity.getTagList().contains(tag)) {
						//if the last object contains the tag, all before do too, add
						if (entity.equals(this.getLast())) {
							tagListIntersect.add(tag);
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
