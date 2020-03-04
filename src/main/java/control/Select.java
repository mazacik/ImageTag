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
		if (EntityCollectionUtil.hasOpenOrNoCollection(entity)) {
			if (super.add(entity, checkDuplicates)) {
				Reload.requestBorderUpdate(entity);
				Reload.notify(Notifier.SELECT);
				return true;
			}
		} else {
			if (super.addAll(entity.getCollection(), checkDuplicates)) {
				Reload.requestBorderUpdate((Filter.getFilteredList(entity.getCollection())));
				Reload.notify(Notifier.SELECT);
				return true;
			}
		}
		return false;
	}
	
	public boolean addAll(Collection<? extends Entity> c) {
		return this.addAll(c, false);
	}
	public boolean addAll(Collection<? extends Entity> c, boolean checkDuplicates) {
		int sizeOld = this.size();
		
		for (Entity entity : c) {
			if (EntityCollectionUtil.hasOpenOrNoCollection(entity)) {
				if (super.add(entity, true)) {
					Reload.requestBorderUpdate(entity);
					
				}
			} else {
				EntityList afterFilter = Filter.getFilteredList(entity.getCollection());
				if (super.addAll(afterFilter, true)) {
					Reload.requestBorderUpdate(afterFilter);
				}
			}
		}
		
		if (this.size() != sizeOld) {
			Reload.notify(Notifier.SELECT);
			return true;
		} else {
			return false;
		}
	}
	
	public boolean remove(Entity entity) {
		if (EntityCollectionUtil.hasOpenOrNoCollection(entity)) {
			if (this.size() != 1) {
				if (super.remove(entity)) {
					Reload.requestBorderUpdate(entity);
					Reload.notify(Notifier.SELECT);
					return true;
				}
			}
		} else {
			if (this.size() != entity.getCollection().size()) {
				if (super.removeAll((Filter.getFilteredList(entity.getCollection())))) {
					Reload.requestBorderUpdate(entity.getCollection());
					Reload.notify(Notifier.SELECT);
					return true;
				}
			}
		}
		return false;
	}
	public boolean removeAll(Collection<?> c) {
		if (super.removeAll(c)) {
			Reload.requestBorderUpdate((EntityList) c);
			Reload.notify(Notifier.SELECT);
			return true;
		}
		return false;
	}
	
	public boolean set(Entity entity) {
		this.clear();
		if (EntityCollectionUtil.hasOpenOrNoCollection(entity)) {
			if (super.add(entity)) {
				Reload.requestBorderUpdate(entity);
				Reload.notify(Notifier.SELECT);
				return true;
			}
		} else {
			EntityList afterFilter = Filter.getFilteredList(entity.getCollection());
			if (super.addAll(afterFilter)) {
				Reload.requestBorderUpdate(afterFilter);
				Reload.notify(Notifier.SELECT);
				return true;
			}
		}
		return false;
	}
	public boolean setAll(Collection<? extends Entity> c) {
		this.clear();
		return this.addAll(c);
	}
	
	public void clear() {
		Reload.requestBorderUpdate(this);
		Reload.notify(Notifier.SELECT);
		super.clear();
	}
	
	public void deleteFiles() {
		EntityList helper = new EntityList();
		
		getEntities().forEach(entity -> {
			if (FileUtil.deleteFile(FileUtil.getFileEntity(entity))) {
				FileUtil.deleteFile(FileUtil.getFileCache(entity));
				helper.add(entity);
			}
		});
		
		if (!helper.isEmpty()) {
			Select.getEntities().removeAll(helper);
			Filter.getEntities().removeAll(helper);
			EntityList.getMain().removeAll(helper);
			
			Reload.notify(Notifier.ENTITY_LIST_MAIN);
		}
	}
	public static void deleteTarget() {
		Entity target = getTarget();
		
		if (FileUtil.deleteFile(FileUtil.getFileEntity(target))) {
			FileUtil.deleteFile(FileUtil.getFileCache(target));
			
			if (target.getCollectionID() != 0) {
				target.getCollection().remove(target);
			}
			
			Select.getEntities().remove(target);
			Filter.getEntities().remove(target);
			EntityList.getMain().remove(target);
			
			Reload.notify(Notifier.TARGET, Notifier.ENTITY_LIST_MAIN);
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
		
		getEntities().addAll(entities.subList(indexLower, indexHigher + 1), true);
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
				Filter.resolve();
				Reload.requestBorderUpdate(target);
			}
			
			target = newTarget;
			Reload.requestBorderUpdate(target);
			GalleryPane.moveViewportToTarget();
			
			Reload.notify(Notifier.TARGET);
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
				Entity groupFirst = target.getCollection().getFirst();
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
	
	private static int storePos = -1;
	private static Entity storeEntity = null;
	public static void storeTargetPosition() {
		if (target != null) {
			if (EntityCollectionUtil.hasOpenOrNoCollection(target)) {
				storeEntity = target;
			} else {
				storeEntity = target.getCollection().getFirst();
			}
			storePos = GalleryPane.getTileEntities().indexOf(storeEntity);
		}
	}
	public static void restoreTargetPosition() {
		EntityList tileEntities = GalleryPane.getTileEntities();
		if (!tileEntities.isEmpty()) {
			if (!tileEntities.contains(storeEntity) && storePos >= 0) {
				if (storePos <= tileEntities.size() - 1) {
					setTarget(tileEntities.get(storePos));
				} else {
					setTarget(tileEntities.getLast());
				}
				
				if (EntityCollectionUtil.hasOpenOrNoCollection(target)) {
					getEntities().set(target);
				} else {
					getEntities().setAll(target.getCollection());
				}
			}
		}
	}
}
