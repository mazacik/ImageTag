package base.collection;

import base.CustomList;
import base.entity.Entity;
import base.entity.EntityList;
import control.reload.Notifier;
import control.reload.Reload;
import main.Root;

import java.util.Random;

public class Collection extends EntityList {
	//todo look for other methods that do shit with collection and
	//todo either move everything here or create factory and util class
	private static CustomList<Collection> openCollections = new CustomList<>();
	
	public Collection(Entity... entities) {
		super(entities);
	}
	private Collection(java.util.Collection<? extends Entity> c) {
		super(c);
	}
	
	public static Collection create(EntityList entityList) {
		Collection collection = new Collection(entityList);
		int ID = new Random().nextInt();
		
		for (Entity entity : entityList) {
			entity.setCollectionID(ID);
			entity.setCollection(collection);
			entity.getTile().updateCollectionIcon();
		}
		
		Root.SELECT.setTarget(collection.getFirst());
		
		Reload.notify(Notifier.TARGET_COLLECTION_CHANGED);
		
		return collection;
	}
	public void discard() {
		for (Entity entity : this) {
			entity.setCollectionID(0);
			entity.setCollection(null);
			entity.getTile().setEffect(null);
		}
		//todo change to target?
		Reload.notify(Notifier.TARGET_COLLECTION_CHANGED);
	}
	
	public void toggle() {
		if (openCollections.contains(this)) {
			openCollections.remove(this);
		} else {
			openCollections.addImpl(this);
		}
		
		for (Entity entity : this) {
			entity.getTile().updateCollectionIcon();
		}
		
		Reload.notify(Notifier.ENTITYLIST_CHANGED);//todo gallery reload, not like this
	}
	public boolean isOpen() {
		return openCollections.contains(this);
	}
}
