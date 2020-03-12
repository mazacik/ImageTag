package control;

import base.CustomList;
import base.entity.Entity;
import base.entity.EntityCollectionUtil;
import base.entity.EntityList;
import control.filter.Filter;
import control.reload.Notifier;
import control.reload.Reload;
import enums.Direction;
import javafx.scene.input.KeyCode;
import misc.FileUtil;
import ui.main.gallery.GalleryPane;

import java.util.Collection;

public class Select extends EntityList {
	public boolean add(Entity entity) {
		return this.add(entity, false);
	}
	public boolean add(Entity entity, boolean checkDuplicates) {
		int sizeOld = this.size();
		
		if (EntityCollectionUtil.hasOpenOrNoCollection(entity)) {
			if (super.addImpl(entity, checkDuplicates)) {
				Reload.requestBorderUpdate(entity);
			}
		} else {
			if (super.addAllImpl(entity.getCollection(), checkDuplicates)) {
				Reload.requestBorderUpdate(Filter.getFilteredList(entity.getCollection()));
			}
		}
		
		if (this.size() != sizeOld) {
			Reload.notify(Notifier.SELECT_CHANGED);
			return true;
		} else {
			return false;
		}
	}
	
	public boolean addAll(Collection<? extends Entity> c) {
		return this.addAll(c, false);
	}
	public boolean addAll(Collection<? extends Entity> c, boolean checkDuplicates) {
		int sizeOld = this.size();
		
		for (Entity entity : c) {
			if (EntityCollectionUtil.hasOpenOrNoCollection(entity)) {
				if (super.addImpl(entity, checkDuplicates)) {
					Reload.requestBorderUpdate(entity);
				}
			} else {
				EntityList afterFilter = Filter.getFilteredList(entity.getCollection());
				if (super.addAllImpl(afterFilter, checkDuplicates)) {
					Reload.requestBorderUpdate(afterFilter);
				}
			}
		}
		
		if (this.size() != sizeOld) {
			Reload.notify(Notifier.SELECT_CHANGED);
			return true;
		} else {
			return false;
		}
	}
	
	public boolean set(Entity entity) {
		this.clear();
		return this.add(entity);
	}
	public boolean setAll(Collection<? extends Entity> c) {
		this.clear();
		return this.addAll(c);
	}
	
	public boolean remove(Entity entity) {
		int sizeOld = this.size();
		
		if (EntityCollectionUtil.hasOpenOrNoCollection(entity)) {
			if (super.removeImpl(entity)) {
				Reload.requestBorderUpdate(entity);
			}
		} else {
			if (super.removeAllImpl(entity.getCollection())) {
				Reload.requestBorderUpdate(Filter.getFilteredList(entity.getCollection()));
			}
		}
		
		if (this.size() != sizeOld) {
			Reload.notify(Notifier.SELECT_CHANGED);
			return true;
		} else {
			return false;
		}
	}
	public boolean removeAll(Collection<?> c) {
		if (super.removeAllImpl(c)) {
			Reload.requestBorderUpdate((EntityList) c);
			Reload.notify(Notifier.SELECT_CHANGED);
			return true;
		} else {
			return false;
		}
	}
	
	public void clear() {
		Reload.requestBorderUpdate(this);
		Reload.notify(Notifier.SELECT_CHANGED);
		super.clear();
	}
	
	public void deleteSelect() {
		EntityList helper = new EntityList();
		
		getEntities().forEach(entity -> {
			if (FileUtil.deleteFile(FileUtil.getFileEntity(entity))) {
				FileUtil.deleteFile(FileUtil.getFileCache(entity));
				
				if (entity.getCollectionID() != 0) {
					entity.getCollection().remove(entity);
					Reload.notify(Notifier.TARGET_COLLECTION_CHANGED);//todo move to collection.remove
				}
				
				helper.addImpl(entity);
			}
		});
		
		if (!helper.isEmpty()) {
			Select.getEntities().removeAll(helper);
			Filter.getEntities().removeAll(helper);
			EntityList.getMain().removeAll(helper);
			
			Reload.notify(Notifier.ENTITYLIST_CHANGED); //todo move to entitylist.getmain.removeall
		}
	}
	
	public static void shiftSelectTo(Entity entityTo) {
		CustomList<Entity> entities = GalleryPane.getTileEntities();
		
		int indexFrom = entities.indexOf(Select.getTarget());
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
		
		getEntities().addAllImpl(entities.subList(indexLower, indexHigher + 1), true);
	}
	
	public void addTag(Integer tagID) {
		super.addTag(tagID);
		Reload.notify(Notifier.SELECT_TAGLIST_CHANGED);
	}
	public void removeTag(Integer tagID) {
		super.removeTag(tagID);
		Reload.notify(Notifier.SELECT_TAGLIST_CHANGED);
	}
	public void clearTags() {
		super.clearTags();
		Reload.notify(Notifier.SELECT_TAGLIST_CHANGED);
	}
	
	private Select() {}
	private static class Loader {
		private static final Select INSTANCE = new Select();
	}
	public static Select getEntities() {
		return Loader.INSTANCE;
	}
	
	private static Entity target;
	public static Entity getTarget() {
		return target;
	}
	public static void setTarget(Entity newTarget) {
		if (newTarget != null && newTarget != target) {
			if (target != null) {
				Filter.resolve(target);
				Reload.requestBorderUpdate(target);
			}
			
			target = newTarget;
			Reload.requestBorderUpdate(target);
			GalleryPane.moveViewportToTarget();
			
			Reload.notify(Notifier.TARGET_CHANGED);
		}
	}
	
	public static void moveTarget(Direction direction) {
		if (target == null) return;
		
		EntityList entities = GalleryPane.getTileEntities();
		if (entities.isEmpty()) return;
		
		int currentTargetIndex;
		if (target.getCollectionID() == 0) {
			currentTargetIndex = entities.indexOf(target);
		} else {
			if (EntityCollectionUtil.getOpenCollections().contains(target.getCollectionID())) {
				currentTargetIndex = entities.indexOf(target);
			} else {
				Entity groupFirst = target.getCollection().getFirstImpl();
				if (entities.contains(groupFirst)) {
					currentTargetIndex = entities.indexOf(groupFirst);
				} else {
					currentTargetIndex = entities.indexOf(target);
				}
			}
		}
		
		int columnCount = GalleryPane.getTilePane().getPrefColumns();
		
		int newTargetIndex = currentTargetIndex;
		switch (direction) {
			case UP:
				newTargetIndex -= columnCount;
				break;
			case LEFT:
				newTargetIndex -= 1;
				break;
			case DOWN:
				newTargetIndex += columnCount;
				break;
			case RIGHT:
				newTargetIndex += 1;
				break;
		}
		
		if (newTargetIndex < 0) newTargetIndex = 0;
		if (newTargetIndex >= entities.size()) newTargetIndex = entities.size() - 1;
		
		setTarget(entities.get(newTargetIndex));
	}
	public static void moveTarget(KeyCode keyCode) {
		switch (keyCode) {
			case W:
				moveTarget(Direction.UP);
				break;
			case A:
				moveTarget(Direction.LEFT);
				break;
			case S:
				moveTarget(Direction.DOWN);
				break;
			case D:
				moveTarget(Direction.RIGHT);
				break;
		}
	}
	
	public static void deleteTarget() {
		Entity target = Select.getTarget();
		if (FileUtil.deleteFile(FileUtil.getFileEntity(target))) {
			FileUtil.deleteFile(FileUtil.getFileCache(target));
			
			Select.storeTargetPosition();
			
			if (target.getCollectionID() != 0) {
				target.getCollection().remove(target);
				Reload.notify(Notifier.TARGET_COLLECTION_CHANGED);//todo move to collection.remove
			}
			
			Select.getEntities().remove(target);
			Filter.getEntities().remove(target);
			EntityList.getMain().remove(target);
			
			Select.restoreTargetPosition();
			
			Reload.notify(Notifier.TARGET_CHANGED, Notifier.ENTITYLIST_CHANGED);
		}
	}
	
	private static Entity memEntity = null;
	private static int memIndex = -1;
	public static void storeTargetPosition() {
		if (target == null) {
			memEntity = null;
			memIndex = -1;
		} else {
			memEntity = EntityCollectionUtil.getRepresentingEntity(target);
			EntityList representingEntities = EntityCollectionUtil.getRepresentingEntityList(Filter.getEntities());
			if (!representingEntities.isEmpty()) {
				memIndex = representingEntities.indexOf(memEntity);
			}
		}
	}
	public static void restoreTargetPosition() {
		EntityList representingEntities = EntityCollectionUtil.getRepresentingEntityList(Filter.getEntities());
		if (!representingEntities.isEmpty()) {
			if (!representingEntities.contains(memEntity) && memIndex >= 0) {
				if (memIndex <= representingEntities.size() - 1) {
					Select.setTarget(representingEntities.get(memIndex));
				} else {
					Select.setTarget(representingEntities.getLastImpl());
				}
				
				if (EntityCollectionUtil.hasOpenOrNoCollection(target)) {
					Select.getEntities().setImpl(target);
				} else {
					Select.getEntities().setAllImpl(Filter.getFilteredList(target.getCollection()));
				}
			}
		}
	}
}
