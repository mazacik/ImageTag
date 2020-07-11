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

public class Group extends EntityList {
	private static final BaseList<Group> openGroups = new BaseList<>();
	
	public Group(Entity... entities) {
		super(entities);
	}
	private Group(Collection<? extends Entity> c) {
		super(c);
	}
	
	public static Group create(EntityList entityList) {
		Group group = new Group(entityList);
		int ID = new Random().nextInt();
		
		for (Entity entity : entityList) {
			entity.setGroupID(ID);
			entity.setGroup(group);
			entity.getTile().updateGroupIcon();
		}
		
		Main.SELECT.setTarget(group.getFirst(), true);
		
		Reload.notify(Notifier.TARGET_GROUP_CHANGED);
		
		return group;
	}
	public void discard() {
		for (Entity entity : this) {
			entity.setGroupID(0);
			entity.setGroup(null);
			entity.getTile().setEffect(null);
		}
		
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
		if (openGroups.contains(this)) {
			openGroups.remove(this);
		} else {
			openGroups.add(this);
		}
		
		this.forEach(entity -> entity.getTile().updateGroupIcon());
		
		Reload.request(InvokeHelper.PANE_GALLERY_RELOAD);
	}
	public boolean isOpen() {
		return openGroups.contains(this);
	}
}
