package backend.control.select;

import backend.control.reload.Notifier;
import backend.control.reload.Reload;
import backend.list.BaseList;
import backend.list.entity.Entity;
import backend.list.entity.EntityList;
import backend.misc.Direction;
import backend.misc.FileUtil;
import javafx.scene.input.KeyEvent;
import main.Root;

import java.util.Collection;
import java.util.List;

public class Select extends EntityList {
	//todo a lot of shit here is bound to GalleryPane, remove that
	public Select() {
	
	}
	
	public boolean add(Entity entity) {
		return this.add(entity, false);
	}
	public boolean add(Entity entity, boolean checkDuplicates) {
		if (entity == null) return false;
		
		int sizeOld = this.size();
		
		if (entity.hasCollection()) {
			if (entity.getCollection().isOpen()) {
				if (super.addImpl(entity, checkDuplicates)) {
					Reload.requestBorderUpdate(entity);
				}
			} else {
				if (super.addAllImpl(entity.getCollection(), checkDuplicates)) {
					Reload.requestBorderUpdate(Root.FILTER.getFilteredList(entity.getCollection()));
				}
			}
		} else {
			if (super.addImpl(entity, checkDuplicates)) {
				Reload.requestBorderUpdate(entity);
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
			if (entity.hasCollection()) {
				if (entity.getCollection().isOpen()) {
					if (super.addImpl(entity, checkDuplicates)) {
						Reload.requestBorderUpdate(entity);
					}
				} else {
					EntityList afterFilter = Root.FILTER.getFilteredList(entity.getCollection());
					if (super.addAllImpl(afterFilter, checkDuplicates)) {
						Reload.requestBorderUpdate(afterFilter);
					}
				}
			} else {
				if (super.addImpl(entity, checkDuplicates)) {
					Reload.requestBorderUpdate(entity);
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
		
		if (entity.hasCollection()) {
			if (entity.getCollection().isOpen()) {
				if (super.removeImpl(entity)) {
					Reload.requestBorderUpdate(entity);
				}
			} else {
				if (super.removeAllImpl(entity.getCollection())) {
					Reload.requestBorderUpdate(Root.FILTER.getFilteredList(entity.getCollection()));
				}
			}
		} else {
			if (super.removeImpl(entity)) {
				Reload.requestBorderUpdate(entity);
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
		
		this.storePosition();
		
		this.forEach(entity -> {
			//todo somehow show loading bar
			if (FileUtil.deleteFile(FileUtil.getFileEntity(entity))) {
				FileUtil.deleteFile(FileUtil.getFileCache(entity));
				
				if (entity.hasCollection()) {
					entity.getCollection().remove(entity);
					Reload.notify(Notifier.TARGET_COLLECTION_CHANGED);//todo move to collection.remove
				}
				
				helper.addImpl(entity);
			}
		});
		
		if (!helper.isEmpty()) {
			this.removeAll(helper);
			Root.FILTER.removeAll(helper);
			Root.ENTITYLIST.removeAll(helper);
			
			this.restorePosition();
			
			Reload.notify(Notifier.ENTITYLIST_CHANGED); //todo move to entitylist.getmain.removeall
		}
	}
	
	private Entity entityFrom = null;
	private EntityList selectBefore = new EntityList();
	public void shiftSelectTo(Entity entityTo) {
		BaseList<Entity> entities = Root.FILTER.getRepresentingEntityList();
		
		int indexFrom = entities.indexOf(entityFrom);
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
		
		List<Entity> shiftSelect = entities.subList(indexLower, indexHigher + 1);
		selectBefore.removeAll(shiftSelect);
		
		this.setAll(selectBefore);
		this.addAll(shiftSelect, true);
	}
	public void setupShiftSelect() {
		entityFrom = target;
		selectBefore.setAllImpl(this);
	}
	
	public void setRandom() {
		Entity randomEntity = null;
		if (Root.FILTER.size() == 1) {
			randomEntity = Root.FILTER.getFirst();
		} else if (Root.FILTER.size() > 1) {
			do {
				randomEntity = Root.FILTER.getRandom();
			} while (!Root.FILTER.isValid(randomEntity));
		}
		this.set(randomEntity);
		this.setTarget(randomEntity);
	}
	
	public void addTag(Integer tagID) {
		super.addTag(tagID);
		Reload.requestFilterCheck(this);
		Reload.notify(Notifier.SELECT_TAGLIST_CHANGED);
	}
	public void removeTag(Integer tagID) {
		super.removeTag(tagID);
		Reload.requestFilterCheck(this);
		Reload.notify(Notifier.SELECT_TAGLIST_CHANGED);
	}
	public void clearTags() {
		super.clearTags();
		Reload.requestFilterCheck(this);
		Reload.notify(Notifier.SELECT_TAGLIST_CHANGED);
	}
	
	private Entity target;
	public Entity getTarget() {
		return target;
	}
	public void setTarget(Entity newTarget) {
		if (newTarget != null && newTarget != target) {
			if (target != null) {
				Root.FILTER.resolve();
				Reload.requestBorderUpdate(target);
			}
			
			target = newTarget;
			Reload.requestBorderUpdate(target);
			Root.GALLERY_PANE.moveViewportToTarget();
			
			Reload.notify(Notifier.TARGET_CHANGED);
		}
	}
	
	public void moveTarget(KeyEvent event) {
		switch (event.getCode()) {
			case W:
				moveTarget(Direction.UP, event.isShiftDown(), event.isControlDown());
				break;
			case A:
				moveTarget(Direction.LEFT, event.isShiftDown(), event.isControlDown());
				break;
			case S:
				moveTarget(Direction.DOWN, event.isShiftDown(), event.isControlDown());
				break;
			case D:
				moveTarget(Direction.RIGHT, event.isShiftDown(), event.isControlDown());
				break;
		}
	}
	public void moveTarget(Direction direction) {
		moveTarget(direction, false, false);
	}
	public void moveTarget(Direction direction, boolean isShiftDown, boolean isControlDown) {
		if (target == null) return;
		
		EntityList entityList = Root.FILTER.getRepresentingEntityList();
		if (entityList.isEmpty()) return;
		
		int currentTargetIndex;
		if (target.hasCollection()) {
			if (target.getCollection().isOpen()) {
				currentTargetIndex = entityList.indexOf(target);
			} else {
				Entity collectionFirst = Root.FILTER.getFilteredList(target.getCollection()).getFirst();
				if (entityList.contains(collectionFirst)) {
					currentTargetIndex = entityList.indexOf(collectionFirst);
				} else {
					currentTargetIndex = entityList.indexOf(target);
				}
			}
		} else {
			currentTargetIndex = entityList.indexOf(target);
		}
		
		int newTargetIndex = currentTargetIndex;
		switch (direction) {
			case UP:
				newTargetIndex -= Root.GALLERY_PANE.getTilePane().getPrefColumns();
				break;
			case LEFT:
				newTargetIndex -= 1;
				break;
			case DOWN:
				newTargetIndex += Root.GALLERY_PANE.getTilePane().getPrefColumns();
				break;
			case RIGHT:
				newTargetIndex += 1;
				break;
			default:
				break;
		}
		
		if (newTargetIndex < 0) {
			newTargetIndex = 0;
		} else if (newTargetIndex >= entityList.size()) {
			newTargetIndex = entityList.size() - 1;
		}
		
		if (isShiftDown) {
			Entity entityTo = entityList.get(newTargetIndex);
			this.shiftSelectTo(entityTo);
			this.setTarget(entityTo);
		} else if (isControlDown) {
			this.setTarget(entityList.get(newTargetIndex));
			this.add(target);
			this.entityFrom = target;
		} else {
			this.setTarget(entityList.get(newTargetIndex));
			this.set(target);
			this.entityFrom = target;
		}
	}
	
	public void deleteTarget() {
		Entity target = this.getTarget();
		
		if (FileUtil.deleteFile(FileUtil.getFileEntity(target))) {
			FileUtil.deleteFile(FileUtil.getFileCache(target));
			
			this.storePosition();
			
			if (target.hasCollection()) {
				target.getCollection().remove(target);
				Reload.notify(Notifier.TARGET_COLLECTION_CHANGED);//todo move to collection.remove
			}
			
			this.remove(target);
			Root.FILTER.remove(target);
			Root.ENTITYLIST.remove(target);
			
			this.restorePosition();
			
			Reload.notify(Notifier.TARGET_CHANGED, Notifier.ENTITYLIST_CHANGED);
		}
	}
	
	private Entity memEntity = null;
	private int memIndex = -1;
	public void storePosition() {
		memEntity = Root.SELECT.getFirst();
		memIndex = Root.FILTER.getRepresentingEntityList().indexOf(memEntity);
	}
	public void restorePosition() {
		EntityList representingEntities = Root.FILTER.getRepresentingEntityList();
		if (!representingEntities.isEmpty()) {
			if (!representingEntities.contains(memEntity) && memIndex >= 0) {
				if (memIndex <= representingEntities.size() - 1) {
					this.setTarget(representingEntities.get(memIndex));
				} else {
					this.setTarget(representingEntities.getLast());
				}
			}
			
			if (!Root.SELECT.contains(target)) {
				this.set(target);
			}
		}
	}
}
