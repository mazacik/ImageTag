package base.entity;

import base.CustomList;
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
	public CustomList<Integer> getTagsIntersect() {
		if (!this.isEmpty()) {
			CustomList<Integer> tagsIntersect = new CustomList<>();
			
			//check every tag of the first object
			for (Integer tagID : this.getFirst().getTagIDs()) {
				//check if all objects contain the tagID
				for (Entity entity : this) {
					if (entity.getTagIDs().contains(tagID)) {
						//if the last object contains the tagID, all before do too, add
						if (entity.equals(this.getLast())) {
							tagsIntersect.add(tagID);
						}
						//if any of the objects doesn't contain the tagID, break
					} else {
						break;
					}
				}
			}
			
			return tagsIntersect;
		} else {
			return new CustomList<>();
		}
	}
	
	private static class Loader {
		private static final EntityList INSTANCE = new EntityList();
	}
	public static EntityList getMain() {
		return Loader.INSTANCE;
	}
}
