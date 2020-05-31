package backend.list.group;

import backend.control.reload.InvokeHelper;
import backend.control.reload.Notifier;
import backend.control.reload.Reload;
import backend.list.BaseList;
import backend.list.entity.Entity;
import backend.list.entity.EntityList;
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
