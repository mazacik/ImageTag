package backend.select;

import backend.BaseList;
import backend.entity.Entity;
import backend.entity.EntityList;
import backend.misc.Direction;
import backend.misc.FileUtil;
import backend.reload.Notifier;
import backend.reload.Reload;
import frontend.UserInterface;
import javafx.scene.input.KeyEvent;
import main.Main;

import java.util.Collection;
import java.util.List;

public class Select extends EntityList {
	public Select() {
		super();
	}
	
	public boolean add(Entity entity) {
		return this.add(entity, false);
	}
	public boolean add(Entity entity, boolean checkDuplicates) {
		if (entity == null) return false;
		
		int sizeOld = this.size();
		
		if (entity.hasGroup()) {
			if (entity.getEntityGroup().isOpen()) {
				if (super.add(entity, checkDuplicates)) {
					Reload.requestBorderUpdate(entity);
				}
			} else {
				if (super.addAll(entity.getEntityGroup(), checkDuplicates)) {
					Reload.requestBorderUpdate(Main.FILTER.getFilteredList(entity.getEntityGroup()));
				}
			}
		} else {
			if (super.add(entity, checkDuplicates)) {
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
			if (entity.hasGroup()) {
				if (entity.getEntityGroup().isOpen()) {
					if (super.add(entity, checkDuplicates)) {
						Reload.requestBorderUpdate(entity);
					}
				} else {
					EntityList afterFilter = Main.FILTER.getFilteredList(entity.getEntityGroup());
					if (super.addAll(afterFilter, true)) {
						Reload.requestBorderUpdate(afterFilter);
					}
				}
			} else {
				if (super.add(entity, checkDuplicates)) {
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
		
		if (entity.hasGroup()) {
			if (entity.getEntityGroup().isOpen()) {
				if (super.remove(entity)) {
					Reload.requestBorderUpdate(entity);
				}
			} else {
				if (super.removeAll(entity.getEntityGroup())) {
					Reload.requestBorderUpdate(Main.FILTER.getFilteredList(entity.getEntityGroup()));
				}
			}
		} else {
			if (super.remove(entity)) {
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
		if (super.removeAll(c)) {
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
			if (FileUtil.deleteFile(FileUtil.getFileEntity(entity))) {
				FileUtil.deleteFile(FileUtil.getFileCache(entity));
				
				if (entity.hasGroup()) {
					entity.getEntityGroup().remove(entity);
					
					if (target.getEntityGroup().size() <= 1) {
						target.getEntityGroup().discard();
					}
					
					Reload.notify(Notifier.TARGET_GROUP_CHANGED);
				}
				
				helper.add(entity);
			}
		});
		
		if (!helper.isEmpty()) {
			this.removeAll(helper);
			Main.FILTER.removeAll(helper);
			Main.ENTITYLIST.removeAll(helper);
			Reload.notify(Notifier.ENTITYLIST_CHANGED);
			
			this.restorePosition();
		}
	}
	
	private Entity entityFrom = null;
	private final EntityList selectBefore = new EntityList();
	public void shiftSelectTo(Entity entityTo) {
		BaseList<Entity> entities = Main.FILTER.createRepresentingList();
		
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
		selectBefore.setAll(this);
	}
	
	public void setRandom() {
		Entity randomEntity = null;
		if (Main.FILTER.size() == 1) {
			randomEntity = Main.FILTER.getFirst();
		} else if (Main.FILTER.size() > 1) {
			do {
				randomEntity = Main.FILTER.getRepresentingRandom();
			} while (!Main.FILTER.isValid(randomEntity));
		}
		this.set(randomEntity);
		this.setTarget(randomEntity);
	}
	
	private final Slideshow slideshow = new Slideshow();
	public Slideshow getSlideshow() {
		return slideshow;
	}
	
	public void addTag(String tag) {
		super.addTag(tag);
		Reload.requestFilterCheck(this);
		Reload.notify(Notifier.SELECT_TAGLIST_CHANGED);
	}
	public void addTags(Collection<String> tags) {
		super.addTags(tags);
		Reload.requestFilterCheck(this);
		Reload.notify(Notifier.SELECT_TAGLIST_CHANGED);
	}
	
	public void removeTag(String tag) {
		super.removeTag(tag);
		Reload.requestFilterCheck(this);
		Reload.notify(Notifier.SELECT_TAGLIST_CHANGED);
	}
	public void removeTags(Collection<String> tags) {
		super.removeTags(tags);
		Reload.requestFilterCheck(this);
		Reload.notify(Notifier.SELECT_TAGLIST_CHANGED);
	}
	
	public void setTags(Collection<String> tags) {
		super.setTags(tags);
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
		if (slideshow.isRunning() && !slideshow.isChanging()) {
			slideshow.stop();
		}
		
		if (newTarget != target) {
			if (target != null) {
				Main.FILTER.resolve();
				Reload.requestBorderUpdate(target);
			}
			
			target = newTarget;
			
			if (target != null) {
				Reload.requestBorderUpdate(target);
				UserInterface.getCenterPane().getGalleryPane().moveViewportToTarget();
			}
			
			Reload.notify(Notifier.TARGET_CHANGED);
		}
	}
	
	public void moveTarget(KeyEvent event) {
		switch (event.getCode()) {
			case W:
				moveTarget(Direction.UP, event.isShiftDown());
				break;
			case A:
				moveTarget(Direction.LEFT, event.isShiftDown());
				break;
			case S:
				moveTarget(Direction.DOWN, event.isShiftDown());
				break;
			case D:
				moveTarget(Direction.RIGHT, event.isShiftDown());
				break;
		}
	}
	public void moveTarget(Direction direction) {
		moveTarget(direction, false);
	}
	public void moveTarget(Direction direction, boolean isShiftDown) {
		if (target == null) return;
		
		EntityList repreList = Main.FILTER.createRepresentingList();
		if (repreList.isEmpty()) return;
		
		int newTargetIndex = repreList.indexOf(target.getRepresentingEntity());
		
		switch (direction) {
			case UP:
				newTargetIndex -= UserInterface.getCenterPane().getGalleryPane().getTilePane().getPrefColumns();
				break;
			case LEFT:
				newTargetIndex -= 1;
				break;
			case DOWN:
				newTargetIndex += UserInterface.getCenterPane().getGalleryPane().getTilePane().getPrefColumns();
				break;
			case RIGHT:
				newTargetIndex += 1;
				break;
			default:
				break;
		}
		
		if (newTargetIndex < 0) {
			newTargetIndex = 0;
		} else if (newTargetIndex >= repreList.size()) {
			newTargetIndex = repreList.size() - 1;
		}
		
		if (isShiftDown) {
			Entity entityTo = repreList.get(newTargetIndex);
			this.shiftSelectTo(entityTo);
			this.setTarget(entityTo);
		} else {
			this.setTarget(repreList.get(newTargetIndex));
			this.set(target);
			this.entityFrom = target;
		}
	}
	
	public void deleteTarget() {
		Entity target = this.getTarget();
		
		if (UserInterface.getCenterPane().getDisplayPane().getVideoPlayer().hasMedia()) {
			UserInterface.getCenterPane().getDisplayPane().getVideoPlayer().release();
		}
		
		if (FileUtil.deleteFile(FileUtil.getFileEntity(target))) {
			FileUtil.deleteFile(FileUtil.getFileCache(target));
			
			this.storePosition();
			
			if (target.hasGroup()) {
				target.getEntityGroup().remove(target);
				
				if (target.getEntityGroup().size() <= 1) {
					target.getEntityGroup().discard();
				}
				
				Reload.notify(Notifier.TARGET_GROUP_CHANGED);
			}
			
			this.remove(target);
			Main.FILTER.remove(target);
			Main.ENTITYLIST.remove(target);
			
			this.restorePosition();
			
			Reload.notify(Notifier.TARGET_CHANGED, Notifier.ENTITYLIST_CHANGED);
		}
	}
	
	private Entity memEntity = null;
	private int memIndex = -1;
	public void storePosition() {
		memEntity = Main.SELECT.getFirst();
		memIndex = Main.FILTER.createRepresentingList().indexOf(memEntity);
	}
	public void restorePosition() {
		if (Main.FILTER.isEmpty()) {
			this.clear();
			this.setTarget(null);
		} else {
			EntityList representingEntities = Main.FILTER.createRepresentingList();
			if (!representingEntities.isEmpty()) {
				if (!representingEntities.contains(memEntity) && memIndex >= 0) {
					if (memIndex <= representingEntities.size() - 1) {
						this.setTarget(representingEntities.get(memIndex));
					} else {
						this.setTarget(representingEntities.getLast());
					}
				}
				
				if (!Main.SELECT.contains(target)) {
					this.set(target);
				}
			}
		}
	}
}
