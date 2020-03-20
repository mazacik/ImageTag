package base.entity;

import base.CustomList;
import control.reload.Notifier;
import control.reload.Reload;
import main.Root;

import java.util.Random;

public abstract class CollectionUtil {
	//todo move this to Collection, remove CollectionUtil?
	public static void create() {
		int collectionID = new Random().nextInt();
		Collection collection = new Collection(Root.SELECT);
		
		for (Entity entity : Root.SELECT) {
			entity.setCollectionID(collectionID);
			entity.setCollection(collection);
			entity.getTile().updateCollectionIcon();
		}
		
		Root.SELECT.setTarget(collection.getFirst());
		
		Reload.notify(Notifier.TARGET_COLLECTION_CHANGED);
	}
	public static void discard() {
		//todo change to target?
		for (Entity entity : Root.SELECT.getFirst().getCollection()) {
			entity.setCollectionID(0);
			entity.setCollection(null);
			entity.getTile().setEffect(null);
		}
		
		Reload.notify(Notifier.TARGET_COLLECTION_CHANGED);
	}
	
	//todo move me
	public static Entity getRepresentingEntity(Entity entity) {
		if (entity.hasCollection()) {
			if (entity.getCollection().isOpen()) {
				return entity;
			} else {
				return Root.FILTER.getFilteredList(entity.getCollection()).getFirst();
			}
		} else {
			return entity;
		}
	}
	public static EntityList getRepresentingEntityList(EntityList entityList) {
		EntityList representingEntityList = new EntityList();
		CustomList<Integer> collections = new CustomList<>();
		
		for (Entity entity : entityList) {
			if (entity.hasCollection()) {
				if (!collections.contains(entity.getCollectionID())) {
					if (entity.getCollection().isOpen()) {
						collections.addImpl(entity.getCollectionID());
						representingEntityList.addAllImpl(Root.FILTER.getFilteredList(entity.getCollection()));
					} else {
						collections.addImpl(entity.getCollectionID());
						representingEntityList.addImpl(entity);
					}
				}
			} else {
				representingEntityList.addImpl(entity);
			}
		}
		
		return representingEntityList;
	}
	
	public static boolean isCollection(EntityList entityList) {
		int collectionID = entityList.getFirst().getCollectionID();
		if (collectionID == 0) return false;
		for (Entity entity : entityList) {
			if (entity.getCollectionID() != collectionID) {
				return false;
			}
		}
		return true;
	}
	
	private static CustomList<Collection> openCollections = new CustomList<>();
	public static CustomList<Collection> getOpenCollections() {
		return openCollections;
	}
	
	//todo move me
	public static void toggleCollection(Entity entity) {
		if (entity.hasCollection()) {
			if (openCollections.contains(entity.getCollection())) {
				openCollections.remove(entity.getCollection());
			} else {
				openCollections.addImpl(entity.getCollection());
			}
			
			for (Entity _entity : entity.getCollection()) {
				_entity.getTile().updateCollectionIcon();
			}
			
			Reload.notify(Notifier.ENTITYLIST_CHANGED);//todo huh?
		}
	}
}
