package base.entity;

import base.CustomList;
import base.tag.Tag;
import base.tag.TagList;
import control.Select;
import control.filter.Filter;
import control.reload.ChangeIn;
import control.reload.Reload;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;

public class EntityList extends CustomList<Entity> {
	//todo rework classes extending entitylist
	public EntityList() {
	
	}
	public EntityList(Entity... entities) {
		this.addAll(Arrays.asList(entities));
	}
	public EntityList(Collection<? extends Entity> c) {
		super(c);
	}
	public EntityList(CustomList<File> fileList) {
		fileList.forEach(file -> this.add(new Entity(file)));
	}
	
	public void sort() {
		super.sort(Comparator.comparing(Entity::getName));
	}
	
	public TagList getTagsAll() {
		TagList tagList = new TagList();
		this.forEach(entity -> tagList.addAll(entity.getTagList(), true));
		return tagList;
	}
	public TagList getTagsIntersect() {
		if (!this.isEmpty()) {
			TagList tagsIntersect = new TagList();
			
			//check every tag of the first object
			for (Tag tag : this.getFirst().getTagList()) {
				//check if all objects contain the tag
				for (Entity entity : this) {
					if (entity.getTagList().contains(tag)) {
						//if the last object contains the tag, all before do too, add
						if (entity.equals(this.getLast())) {
							tagsIntersect.add(tag);
						}
						//if any of the objects doesn't contain the tag, break
					} else {
						break;
					}
				}
			}
			
			return tagsIntersect;
		} else {
			return new TagList();
		}
	}
	
	public void addTag(Tag tag) {
		this.forEach(entity -> entity.getTagList().add(tag));
		Reload.notify(ChangeIn.TAGS_OF_SELECT);
	}
	public void removeTag(Tag tag) {
		this.forEach(entity -> entity.getTagList().remove(tag));
		Reload.notify(ChangeIn.TAGS_OF_SELECT);
	}
	
	public Entity getRandom() {
		Entity entity = super.getRandom();
		if (entity != null) {
			if (entity.getCollectionID() == 0) {
				return entity;
			} else {
				return Filter.applyTo(entity.getCollection()).getRandom();
			}
		} else {
			return null;
		}
	}
	
	private static class Loader {
		private static final EntityList INSTANCE = new EntityList();
	}
	public static EntityList getMain() {
		return Loader.INSTANCE;
	}
}
