package control;

import base.CustomList;
import base.entity.Entity;
import base.entity.EntityList;
import control.filter.Filter;
import control.reload.ChangeIn;
import control.reload.Reload;
import misc.FileUtil;
import ui.main.center.PaneGallery;

import java.util.Collection;

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
		Target.storePosition();
		
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
		
		Target.restorePosition();
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
}
