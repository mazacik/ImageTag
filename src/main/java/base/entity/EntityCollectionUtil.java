package base.entity;

import base.CustomList;
import control.Select;
import control.filter.Filter;
import control.reload.Notifier;
import control.reload.Reload;

import java.util.Random;

public abstract class EntityCollectionUtil {
	public static void create() {
		int collectionID = new Random().nextInt();
		EntityList collection = new EntityList(Select.getEntities());
		for (Entity entity : Select.getEntities()) {
			entity.setCollectionID(collectionID);
			entity.setCollection(collection);
			entity.getTile().updateCollectionIcon();
		}
		
		Select.setTarget(collection.getFirstImpl());
		
		Reload.notify(Notifier.TARGET_COLLECTION_CHANGED);
	}
	public static void discard() {
		for (Entity entity : Select.getEntities().getFirstImpl().getCollection()) {
			entity.setCollectionID(0);
			entity.setCollection(null);
			entity.getTile().setEffect(null);
		}
		
		Reload.notify(Notifier.TARGET_COLLECTION_CHANGED);
	}
	
	public static Entity getRepresentingEntity(Entity entity) {
		if (EntityCollectionUtil.hasOpenOrNoCollection(entity)) {
			return entity;
		} else {
			return Filter.getFilteredList(entity.getCollection()).getFirstImpl();
		}
	}
	public static EntityList getRepresentingEntityList(EntityList entityList) {
		EntityList representingEntityList = new EntityList();
		CustomList<Integer> collections = new CustomList<>();
		
		for (Entity entity : entityList) {
			if (EntityCollectionUtil.hasOpenOrNoCollection(entity)) {
				representingEntityList.addImpl(entity);
			} else if (!collections.contains(entity.getCollectionID())) {
				collections.addImpl(entity.getCollectionID());
				representingEntityList.addImpl(entity);
			}
		}
		
		return representingEntityList;
	}
	
	public static boolean isCollection(EntityList entityList) {
		int collectionID = entityList.getFirstImpl().getCollectionID();
		if (collectionID == 0) return false;
		for (Entity entity : entityList) {
			if (entity.getCollectionID() != collectionID) {
				return false;
			}
		}
		return true;
	}
	public static boolean hasOpenOrNoCollection(Entity entity) {
		return entity.getCollectionID() == 0 || openCollections.contains(entity.getCollectionID());
	}
	
	private static CustomList<Integer> openCollections = new CustomList<>();
	public static CustomList<Integer> getOpenCollections() {
		return openCollections;
	}
	
	public static void toggleCollection(Entity entity) {
		int collectionID = entity.getCollectionID();
		
		if (collectionID != 0) {
			if (openCollections.contains(collectionID)) {
				openCollections.remove((Integer) collectionID);
			} else {
				openCollections.addImpl(collectionID);
			}
			
			for (Entity _entity : entity.getCollection()) {
				_entity.getTile().updateCollectionIcon();
			}
			
			Reload.notify(Notifier.ENTITYLIST_CHANGED);
		}
	}
}
