package backend.list.entity;

import backend.list.BaseList;
import backend.list.tag.Tag;
import backend.list.tag.TagList;
import main.Root;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;

public class EntityList extends BaseList<Entity> {
	public EntityList() {
	
	}
	public EntityList(Collection<? extends Entity> c) {
		super(c);
	}
	public EntityList(Entity... entities) {
		super(Arrays.asList(entities));
	}
	public EntityList(BaseList<File> fileList) {
		fileList.forEach(file -> this.add(new Entity(file)));
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
		Entity entity = this.getRepresentingEntityList().getRandom();
		if (entity != null) {
			if (entity.hasCollection()) {
				return Root.FILTER.getFilteredList(entity.getCollection()).getRandom();
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
	
	public BaseList<Integer> getTagIDList() {
		BaseList<Integer> tagIDList = new BaseList<>();
		this.forEach(entity -> tagIDList.addAll(entity.getTagIDList(), true));
		return tagIDList;
	}
	public BaseList<Integer> getTagIDListIntersect() {
		if (!this.isEmpty()) {
			BaseList<Integer> tagIDList = new BaseList<>();
			//check every tag of the first object
			for (int tagID : this.getFirst().getTagIDList()) {
				//check if all objects contain the tagID
				for (Entity entity : this) {
					if (entity.getTagIDList().contains(tagID)) {
						//if the last object contains the tagID, all before do too, add
						if (entity.equals(this.getLast())) {
							tagIDList.add(tagID, true);
						}
					} else {
						//if any of the objects doesn't contain the tag, break
						break;
					}
				}
			}
			return tagIDList;
		} else {
			return new BaseList<>();
		}
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
				for (Entity entity : new EntityList(this)) {
					if (entity.getTagList().contains(tag)) {
						//if the last object contains the tag, all before do too, add
						if (entity.equals(this.getLast())) {
							tagListIntersect.add(tag, true);
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
		BaseList<Integer> collections = new BaseList<>();
		
		for (Entity entity : this) {
			if (entity.hasCollection()) {
				if (!collections.contains(entity.getCollectionID())) {
					if (entity.getCollection().isOpen()) {
						collections.add(entity.getCollectionID());
						representingEntityList.addAll(Root.FILTER.getFilteredList(entity.getCollection()));
					} else {
						collections.add(entity.getCollectionID());
						representingEntityList.add(entity);
					}
				}
			} else {
				representingEntityList.add(entity);
			}
		}
		
		return representingEntityList;
	}
}
