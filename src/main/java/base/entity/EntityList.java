package base.entity;

import base.CustomList;
import base.tag.Tag;
import base.tag.TagList;
import main.Root;

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
	
	public Entity getFirst() {
		this.sort();
		return (!this.isEmpty()) ? this.get(0) : null;
	}
	public Entity getLast() {
		this.sort();
		return (!this.isEmpty()) ? this.get(this.size() - 1) : null;
	}
	public Entity getRandom() {
		Entity entity = this.getRepresentingEntityList().getRandomImpl();
		if (entity != null) {
			if (entity.hasCollection()) {
				return Root.FILTER.getFilteredList(entity.getCollection()).getRandomImpl();
			} else {
				return entity;
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
							tagListIntersect.addImpl(tag, true);
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
	
	public boolean isCollection() {
		if (this.isEmpty()) {
			return false;
		} else {
			int collectionID = this.getFirst().getCollectionID();
			if (collectionID == 0) return false;
			for (Entity entity : this) {
				if (entity.getCollectionID() != collectionID) {
					return false;
				}
			}
			return true;
		}
	}
	public EntityList getRepresentingEntityList() {
		EntityList representingEntityList = new EntityList();
		CustomList<Integer> collections = new CustomList<>();
		
		for (Entity entity : this) {
			if (entity.hasCollection()) {
				if (!collections.contains(entity.getCollectionID())) {
					if (entity.getCollection().isOpen()) {
						collections.addImpl(entity.getCollectionID());
						representingEntityList.addAllImpl(Root.FILTER.getFilteredList(entity.getCollection()));
					} else {
						collections.addImpl(entity.getCollectionID());
						representingEntityList.addImpl(entity);
					}
				}
			} else {
				representingEntityList.addImpl(entity);
			}
		}
		
		return representingEntityList;
	}
}
