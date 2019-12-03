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
	
	public void init() {
	
	}
	
	public boolean add(Entity entity) {
		if (entity == null) return false;
		int entityGroupID = entity.getEntityGroupID();
		if (entityGroupID != 0 && !galleryPane.getExpandedGroups().contains(entityGroupID)) {
			EntityList entityGroup = entity.getEntityGroup();
			if (super.addAll(entityGroup)) {
				reload.requestBorderUpdate(entityGroup);
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
		
		CustomList<Integer> expandedGroups = galleryPane.getExpandedGroups();
		
		for (Entity entity : c) {
			if (entity == null || this.contains(entity)) continue;
			
			int entityGroupID = entity.getEntityGroupID();
			if (entityGroupID == 0 || expandedGroups.contains(entityGroupID)) {
				super.add(entity);
				reload.requestBorderUpdate(entity);
			} else {
				EntityList entityGroup = entity.getEntityGroup();
				super.addAll(entityGroup);
				reload.requestBorderUpdate(entityGroup);
			}
		}
		
		reload.notify(ChangeIn.SELECT);
		return true;
	}
	public boolean remove(Entity entity) {
		if (entity == null) return false;
		
		int size = this.size();
		if (entity.getEntityGroupID() == 0 || galleryPane.getExpandedGroups().contains(entity.getEntityGroupID())) {
			reload.requestBorderUpdate(entity);
			super.remove(entity);
		} else {
			EntityList entityGroup = entity.getEntityGroup();
			reload.requestBorderUpdate(entityGroup);
			this.removeAll(entityGroup);
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
		Entity entity = EntityList.getRandom(galleryPane.getEntitiesOfTiles());
		this.set(entity);
		target.set(entity);
	}
	public void setRandomFromEntityGroup() {
		Entity entity = EntityList.getRandom(target.get().getEntityGroup());
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
		CustomList<Entity> entities = galleryPane.getEntitiesOfTiles();
		
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
