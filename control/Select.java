package control;

import base.CustomList;
import base.entity.Entity;
import base.entity.EntityList;
import base.tag.Tag;
import control.filter.Filter;
import control.reload.ChangeIn;
import control.reload.Reload;
import misc.FileUtil;
import ui.main.center.PaneGallery;

import java.util.Collection;

public class Select extends EntityList {
	public boolean add(Entity entity) {
		if (entity == null) return false;
		int collectionID = entity.getCollectionID();
		if (collectionID != 0 && !PaneGallery.get().getOpenCollections().contains(collectionID)) {
			EntityList collection = entity.getCollection();
			if (super.addAll(collection)) {
				Reload.requestBorderUpdate(collection);
				Reload.notify(ChangeIn.SELECT);
				return true;
			}
		} else {
			if (super.add(entity)) {
				Reload.requestBorderUpdate(entity);
				Reload.notify(ChangeIn.SELECT);
				return true;
			}
		}
		return false;
	}
	public boolean addAll(Collection<? extends Entity> c) {
		if (c == null || c.isEmpty()) return false;
		
		CustomList<Integer> expandedGroups = PaneGallery.get().getOpenCollections();
		
		for (Entity entity : c) {
			if (entity == null || this.contains(entity)) continue;
			
			int collectionID = entity.getCollectionID();
			if (collectionID == 0 || expandedGroups.contains(collectionID)) {
				super.add(entity);
				Reload.requestBorderUpdate(entity);
			} else {
				EntityList collection = entity.getCollection();
				super.addAll(collection);
				Reload.requestBorderUpdate(collection);
			}
		}
		
		Reload.notify(ChangeIn.SELECT);
		return true;
	}
	public boolean remove(Entity entity) {
		if (entity == null) return false;
		
		int size = this.size();
		if (entity.getCollectionID() == 0 || PaneGallery.get().getOpenCollections().contains(entity.getCollectionID())) {
			Reload.requestBorderUpdate(entity);
			super.remove(entity);
		} else {
			EntityList collection = entity.getCollection();
			Reload.requestBorderUpdate(collection);
			this.removeAll(collection);
		}
		
		if (size != this.size()) {
			Reload.notify(ChangeIn.SELECT);
			return true;
		}
		return false;
	}
	public boolean removeAll(EntityList entityList) {
		if (entityList == null) return false;
		if (super.removeAll(entityList)) {
			for (Entity entity : entityList) Reload.requestBorderUpdate(entity);
			Reload.notify(ChangeIn.SELECT);
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
		Entity entity = EntityList.getRandom(PaneGallery.get().getEntitiesOfTiles());
		this.set(entity);
		Target.set(entity);
	}
	public void setRandomFromCollection() {
		Entity entity = EntityList.getRandom(Target.get().getCollection());
		this.set(entity);
		Target.set(entity);
	}
	public void clear() {
		Reload.requestBorderUpdate(this);
		super.clear();
		Reload.notify(ChangeIn.SELECT);
	}
	public void swapState(Entity entity) {
		if (super.contains(entity)) {
			this.remove(entity);
		} else {
			this.add(entity);
		}
	}
	
	public static void addTag(Tag tag) {
		Loader.INSTANCE.forEach(entity -> entity.getTagList().add(tag));
		Reload.notify(ChangeIn.TAGS_OF_SELECT);
	}
	public static void removeTag(Tag tag) {
		Loader.INSTANCE.forEach(entity -> entity.getTagList().remove(tag));
		Reload.notify(ChangeIn.TAGS_OF_SELECT);
	}
	
	public static void deleteFiles() {
		Target.storePosition();
		Loader.INSTANCE.forEach(entity -> {
			FileUtil.deleteFile(FileUtil.getFileEntity(Target.get()));
			FileUtil.deleteFile(FileUtil.getFileCache(entity));
			
			PaneGallery.get().getTiles().getChildren().remove(entity.getGalleryTile());
			Loader.INSTANCE.remove(entity);
			Filter.getEntities().remove(entity);
			EntityList.getMain().remove(entity);
		});
		Target.restorePosition();
		
		Reload.notify(ChangeIn.ENTITY_LIST_MAIN);
		Reload.start();
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
