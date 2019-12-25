package control;

import baseobject.CustomList;
import baseobject.entity.Entity;
import baseobject.entity.EntityList;
import baseobject.tag.Tag;
import control.reload.ChangeIn;
import main.InstanceCollector;

import java.util.Collection;

public class Select extends EntityList implements InstanceCollector {
	public Select() {
	
	}
	
	public boolean add(Entity entity) {
		if (entity == null) return false;
		int collectionID = entity.getCollectionID();
		if (collectionID != 0 && !paneGallery.getExpandedGroups().contains(collectionID)) {
			EntityList collection = entity.getCollection();
			if (super.addAll(collection)) {
				reload.requestBorderUpdate(collection);
				reload.notify(ChangeIn.SELECT);
				return true;
			}
		} else {
			if (super.add(entity)) {
				reload.requestBorderUpdate(entity);
				reload.notify(ChangeIn.SELECT);
				return true;
			}
		}
		return false;
	}
	public boolean addAll(Collection<? extends Entity> c) {
		if (c == null || c.isEmpty()) return false;
		
		CustomList<Integer> expandedGroups = paneGallery.getExpandedGroups();
		
		for (Entity entity : c) {
			if (entity == null || this.contains(entity)) continue;
			
			int collectionID = entity.getCollectionID();
			if (collectionID == 0 || expandedGroups.contains(collectionID)) {
				super.add(entity);
				reload.requestBorderUpdate(entity);
			} else {
				EntityList collection = entity.getCollection();
				super.addAll(collection);
				reload.requestBorderUpdate(collection);
			}
		}
		
		reload.notify(ChangeIn.SELECT);
		return true;
	}
	public boolean remove(Entity entity) {
		if (entity == null) return false;
		
		int size = this.size();
		if (entity.getCollectionID() == 0 || paneGallery.getExpandedGroups().contains(entity.getCollectionID())) {
			reload.requestBorderUpdate(entity);
			super.remove(entity);
		} else {
			EntityList collection = entity.getCollection();
			reload.requestBorderUpdate(collection);
			this.removeAll(collection);
		}
		
		if (size != this.size()) {
			reload.notify(ChangeIn.SELECT);
			return true;
		}
		return false;
	}
	public boolean removeAll(EntityList entityList) {
		if (entityList == null) return false;
		if (super.removeAll(entityList)) {
			for (Entity entity : entityList) reload.requestBorderUpdate(entity);
			reload.notify(ChangeIn.SELECT);
			return true;
		}
		return false;
	}
	
	public void set(Entity entity) {
		this.clear();
		this.add(entity);
	}
	public void setAll(EntityList entities) {
		this.clear();
		this.addAll(entities);
	}
	public void setRandom() {
		Entity entity = EntityList.getRandom(paneGallery.getEntitiesOfTiles());
		this.set(entity);
		target.set(entity);
	}
	public void setRandomFromCollection() {
		Entity entity = EntityList.getRandom(target.get().getCollection());
		this.set(entity);
		target.set(entity);
	}
	public void clear() {
		reload.requestBorderUpdate(this);
		super.clear();
		reload.notify(ChangeIn.SELECT);
	}
	public void swapState(Entity entity) {
		if (super.contains(entity)) {
			this.remove(entity);
		} else {
			this.add(entity);
		}
	}
	
	public void addTag(Tag tag) {
		this.forEach(entity -> entity.getTagList().add(tag));
		reload.notify(ChangeIn.TAGS_OF_SELECT);
	}
	public void removeTag(Tag tag) {
		this.forEach(entity -> entity.getTagList().remove(tag));
		reload.notify(ChangeIn.TAGS_OF_SELECT);
	}
	
	private Entity shiftStart = null;
	public void shiftSelectFrom(Entity entityFrom) {
		this.shiftStart = entityFrom;
	}
	public void shiftSelectTo(Entity entityTo) {
		CustomList<Entity> entities = paneGallery.getEntitiesOfTiles();
		
		int indexFrom = entities.indexOf(shiftStart);
		int indexTo = entities.indexOf(entityTo);
		
		int indexLower;
		int indexHigher;
		
		if (indexFrom > indexTo) {
			indexLower = indexTo;
			indexHigher = indexFrom;
		} else {
			indexLower = indexFrom;
			indexHigher = indexTo;
		}
		
		this.addAll(entities.subList(indexLower, indexHigher + 1));
	}
}
