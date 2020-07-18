package backend.group;

import backend.BaseList;
import backend.entity.Entity;
import backend.entity.EntityList;
import backend.reload.InvokeHelper;
import backend.reload.Notifier;
import backend.reload.Reload;
import main.Main;

import java.util.Collection;
import java.util.Random;

public class EntityGroup extends EntityList {
	private static final BaseList<EntityGroup> openEntityGroups = new BaseList<>();
	
	private int id;
	
	public EntityGroup(Entity entity) {
		super(entity);
		this.id = entity.getEntityGroupID();
	}
	private EntityGroup(Collection<? extends Entity> c) {
		super(c);
	}
	
	public static EntityGroup createFrom(EntityList entityList) {
		EntityGroup entityGroup = new EntityGroup(entityList);
		entityGroup.id = new Random().nextInt();
		
		for (Entity entity : entityList) {
			entity.setEntityGroupID(entityGroup.id);
			entity.setEntityGroup(entityGroup);
			entity.getTile().updateGroupIcon();
		}
		
		Main.SELECT.setTarget(entityGroup.getFirst());
		Reload.notify(Notifier.TARGET_GROUP_CHANGED);
		
		return entityGroup;
	}
	public void discard() {
		for (Entity entity : this) entity.discardGroup();
		
		Reload.notify(Notifier.TARGET_GROUP_CHANGED);
	}
	
	public Entity getRepresentingRandom() {
		BaseList<Entity> filteredList = Main.FILTER.getFilteredList(this);
		if (filteredList.isEmpty()) {
			return null;
		} else {
			return filteredList.getRandom();
		}
	}
	
	public void toggle() {
		if (openEntityGroups.contains(this)) {
			openEntityGroups.remove(this);
		} else {
			openEntityGroups.add(this);
		}
		
		this.forEach(entity -> entity.getTile().updateGroupIcon());
		
		Reload.request(InvokeHelper.PANE_GALLERY_RELOAD);
	}
	public boolean isOpen() {
		return openEntityGroups.contains(this);
	}
	
	public int getID() {
		return id;
	}
}
