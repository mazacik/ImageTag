package application.backend.control;

import application.backend.base.CustomList;
import application.backend.base.entity.Entity;
import application.backend.base.entity.EntityList;
import application.backend.base.tag.Tag;
import application.backend.base.tag.TagList;
import application.backend.control.reload.ChangeIn;
import application.backend.util.EntityGroupUtil;
import application.frontend.stage.StageManager;
import application.frontend.stage.template.YesNoCancelStage;
import application.main.InstanceCollector;

import java.util.Collection;
import java.util.Random;

public class Select extends EntityList implements InstanceCollector {
	private Entity shiftStart = null;
	
	public Select() {
	
	}
	
	public void init() {
	
	}
	
	public boolean add(Entity entity) {
		if (entity == null) return false;
		int entityGroupID = entity.getEntityGroupID();
		if (entityGroupID != 0 && !galleryPane.getExpandedGroups().contains(entityGroupID)) {
			if (super.addAll(EntityGroupUtil.getEntityGroup(entity))) {
				reload.requestTileEffect(EntityGroupUtil.getEntityGroup(entity));
				reload.notify(ChangeIn.SELECT);
				return true;
			}
		} else {
			if (super.add(entity)) {
				reload.requestTileEffect(entity);
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
				reload.requestTileEffect(entity);
			} else {
				EntityList entityGroup = EntityGroupUtil.getEntityGroup(entity);
				super.addAll(entityGroup);
				reload.requestTileEffect(entityGroup);
			}
		}
		
		reload.notify(ChangeIn.SELECT);
		return true;
	}
	public boolean remove(Entity entity) {
		if (entity == null) return false;
		
		int size = this.size();
		if (entity.getEntityGroupID() == 0 || galleryPane.getExpandedGroups().contains(entity.getEntityGroupID())) {
			reload.requestTileEffect(entity);
			super.remove(entity);
		} else {
			reload.requestTileEffect(EntityGroupUtil.getEntityGroup(entity));
			this.removeAll(EntityGroupUtil.getEntityGroup(entity));
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
			for (Entity entity : entityList) reload.requestTileEffect(entity);
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
		Entity entity = galleryPane.getEntitiesOfTiles().getRandom();
		this.set(entity);
		target.set(entity);
	}
	public void setRandomFromEntityGroup() {
		Entity entity = getRandom(EntityGroupUtil.getEntityGroup(target.get()));
		this.set(entity);
		target.set(entity);
	}
	public void clear() {
		reload.requestTileEffect(this);
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
	
	public void entityGroupCreate() {
		CustomList<Integer> entityGroupIDs = entityListMain.getentityGroupIDs();
		int entityGroupID;
		do entityGroupID = new Random().nextInt();
		while (entityGroupIDs.contains(entityGroupID));
		
		YesNoCancelStage.Result result = StageManager.getYesNoCancelStage().show("Merge tags? (" + this.size() + " items selected)");
		if (result == YesNoCancelStage.Result.YES) {
			TagList tagList = new TagList();
			for (Entity entity : this) {
				tagList.addAll(entity.getTagList());
			}
			for (Entity entity : this) {
				entity.setEntityGroupID(entityGroupID);
				entity.setTagList(tagList);
			}
			
			reload.notify(ChangeIn.TAGS_OF_SELECT);
		} else if (result == YesNoCancelStage.Result.NO) {
			for (Entity entity : this) {
				entity.setEntityGroupID(entityGroupID);
			}
		} else return;  //YesNoCancelStage.Result.CANCEL
		
		target.set(this.getFirst());
		reload.notify(ChangeIn.ENTITY_LIST_MAIN);
	}
	public void entityGroupDiscard() {
		Entity target = InstanceCollector.target.get();
		if (target.getEntityGroupID() != 0) {
			EntityList entityGroup = EntityGroupUtil.getEntityGroup(target);
			for (Entity entity : entityGroup) {
				entity.setEntityGroupID(0);
			}
		}
		reload.notify(ChangeIn.ENTITY_LIST_MAIN, ChangeIn.TAGS_OF_SELECT);
	}
	public boolean isSelectGrouped() {
		int entityGroupID = target.get().getEntityGroupID();
		if (entityGroupID == 0) return false;
		for (Entity entity : this) {
			if (entity.getEntityGroupID() != entityGroupID) {
				return false;
			}
		}
		return true;
	}
	
	public void shiftSelectTo(Entity shiftCurrent) {
		CustomList<Entity> entities = galleryPane.getEntitiesOfTiles();
		
		int indexFrom = entities.indexOf(shiftStart);
		int indexTo = entities.indexOf(shiftCurrent);
		
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
	
	public void addTag(Tag tag) {
		this.forEach(entity -> entity.getTagList().add(tag));
		reload.notify(ChangeIn.TAGS_OF_SELECT);
	}
	public void removeTag(Tag tag) {
		this.forEach(entity -> entity.getTagList().remove(tag));
		reload.notify(ChangeIn.TAGS_OF_SELECT);
	}
	
	public void setShiftStart(Entity shiftStart) {
		this.shiftStart = shiftStart;
	}
}
