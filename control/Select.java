package control;

import base.CustomList;
import base.entity.Entity;
import base.entity.EntityList;
import control.filter.Filter;
import control.reload.ChangeIn;
import control.reload.Reload;
import enums.Direction;
import javafx.scene.input.KeyCode;
import misc.FileUtil;
import ui.main.center.PaneGallery;

import java.util.Collection;
import java.util.logging.Logger;

public class Select extends EntityList {
	public boolean add(Entity entity) {
		if (entity.getCollectionID() == 0 || PaneGallery.get().getExpandedCollections().contains(entity.getCollectionID())) {
			if (super.add(entity)) {
				Reload.requestBorderUpdate(entity);
				Reload.notify(ChangeIn.SELECT);
				return true;
			}
		} else {
			if (super.addAll(entity.getCollection())) {
				Reload.requestBorderUpdate(entity.getCollection());
				Reload.notify(ChangeIn.SELECT);
				return true;
			}
		}
		return false;
	}
	public boolean addAll(Collection<? extends Entity> c) {
		if (super.addAll(c)) {
			Reload.requestBorderUpdate(c);
			Reload.notify(ChangeIn.SELECT);
			return true;
		}
		return false;
	}
	
	public boolean remove(Entity entity) {
		if (entity.getCollectionID() == 0 || PaneGallery.get().getExpandedCollections().contains(entity.getCollectionID())) {
			if (super.remove(entity)) {
				Reload.requestBorderUpdate(entity);
				Reload.notify(ChangeIn.SELECT);
				return true;
			}
		} else {
			if (super.removeAll(entity.getCollection())) {
				Reload.requestBorderUpdate(entity.getCollection());
				Reload.notify(ChangeIn.SELECT);
				return true;
			}
		}
		return false;
	}
	public boolean removeAll(Collection<?> c) {
		if (super.removeAll(c)) {
			Reload.requestBorderUpdate((Entity) c);
			Reload.notify(ChangeIn.SELECT);
			return true;
		}
		return false;
	}
	
	public boolean set(Entity entity) {
		this.clear();
		return this.add(entity);
	}
	public boolean setAll(Collection<? extends Entity> c) {
		this.clear();
		return this.addAll(c);
	}
	
	public void clear() {
		Reload.requestBorderUpdate(this);
		Reload.notify(ChangeIn.SELECT);
		super.clear();
	}
	
	public void deleteFiles() {
		storeTargetPosition();
		
		EntityList helper = new EntityList(Loader.INSTANCE);
		helper.forEach(entity -> {
			FileUtil.deleteFile(FileUtil.getFileEntity(entity));
			FileUtil.deleteFile(FileUtil.getFileCache(entity));
		});
		
		Select.getEntities().removeAll(helper);
		Filter.getEntities().removeAll(helper);
		EntityList.getMain().removeAll(helper);
		
		Reload.notify(ChangeIn.ENTITY_LIST_MAIN);
		Reload.start();
		
		restoreTargetPosition();
	}
	
	private Entity shiftStart = null;
	public static void shiftSelectFrom(Entity entityFrom) {
		Loader.INSTANCE.shiftStart = entityFrom;
	}
	public static void shiftSelectTo(Entity entityTo) {
		CustomList<Entity> entities = PaneGallery.get().getEntitiesOfTiles();
		
		int indexFrom = entities.indexOf(Loader.INSTANCE.shiftStart);
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
		//todo probably a bug here too
		Loader.INSTANCE.addAll(entities.subList(indexLower, indexHigher + 1));
	}
	
	private Select() {}
	private static class Loader {
		private static final Select INSTANCE = new Select();
	}
	public static Select getEntities() {
		return Loader.INSTANCE;
	}
	
	/* Target */
	private static Entity target;
	
	public static Entity getTarget() {
		return target;
	}
	public static void setTarget(Entity newTarget) {
		if (newTarget != null && newTarget != target) {
			if (target != null) Reload.requestBorderUpdate(target);
			Reload.requestBorderUpdate(newTarget);
			
			target = newTarget;
			
			PaneGallery.get().moveViewportToTarget();
			Reload.notify(ChangeIn.TARGET);
			
			Logger.getGlobal().info(newTarget.getName());
		}
	}
	
	public static void moveTarget(Direction direction) {
		if (target == null) return;
		
		EntityList entities = PaneGallery.get().getEntitiesOfTiles();
		if (entities.isEmpty()) return;
		
		int currentTargetIndex;
		if (target.getCollectionID() == 0) {
			currentTargetIndex = entities.indexOf(target);
		} else {
			if (PaneGallery.get().getExpandedCollections().contains(target.getCollectionID())) {
				currentTargetIndex = entities.indexOf(target);
			} else {
				Entity groupFirst = target.getCollection().getFirst();
				if (entities.contains(groupFirst)) {
					currentTargetIndex = entities.indexOf(groupFirst);
				} else {
					currentTargetIndex = entities.indexOf(target);
				}
			}
		}
		
		int columnCount = PaneGallery.get().getColumnCount();
		
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
	
	private static Entity storeEntity = null;
	private static int storePos = -1;
	public static void storeTargetPosition() {
		CustomList<Integer> expandedcollection = PaneGallery.get().getExpandedCollections();
		CustomList<Entity> visibleEntities = PaneGallery.get().getEntitiesOfTiles();
		
		if (target.getCollectionID() == 0) {
			storeEntity = target;
			storePos = visibleEntities.indexOf(target);
		} else {
			if (expandedcollection.contains(target.getCollectionID())) {
				storeEntity = target;
				storePos = visibleEntities.indexOf(target);
			} else {
				storeEntity = target.getCollection().getFirst();
				storePos = visibleEntities.indexOf(storeEntity);
			}
		}
	}
	public static Entity restoreTargetPosition() {
		//todo if this lands on a non-expanded collection, add the whole collection
		//todo add move viewpoert to target
		EntityList visibleEntities = PaneGallery.get().getEntitiesOfTiles();
		if (!visibleEntities.isEmpty()) {
			if (storeEntity != null && visibleEntities.contains(storeEntity)) {
				setTarget(storeEntity);
				return storeEntity;
			} else if (storePos >= 0) {
				Entity newTarget;
				
				if (storePos <= visibleEntities.size() - 1) newTarget = visibleEntities.get(storePos);
				else newTarget = visibleEntities.getLast();
				
				setTarget(newTarget);
				
				if (Select.getEntities().isEmpty()) {
					Select.getEntities().add(newTarget);
				}
				
				return newTarget;
			}
		}
		return null;
	}
}
