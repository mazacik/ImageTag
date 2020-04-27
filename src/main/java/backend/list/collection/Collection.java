package backend.list.collection;

import backend.control.reload.InvokeHelper;
import backend.control.reload.Notifier;
import backend.control.reload.Reload;
import backend.list.BaseList;
import backend.list.entity.Entity;
import backend.list.entity.EntityList;
import main.Root;

import java.util.Random;

public class Collection extends EntityList {
	private static final BaseList<Collection> openCollections = new BaseList<>();
	
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
		
		Reload.notify(Notifier.TARGET_COLLECTION_CHANGED);
	}
	
	public void mergeTags() {
		BaseList<Integer> tagIDs = this.getTagIDList();
		
		this.forEach(entity -> {
			entity.getTagIDList().setAll(new BaseList<>(tagIDs));
			entity.initTags();
		});
	}
	
	public void toggle() {
		if (openCollections.contains(this)) {
			openCollections.remove(this);
		} else {
			openCollections.add(this);
		}
		
		this.forEach(entity -> entity.getTile().updateCollectionIcon());
		
		Reload.request(InvokeHelper.PANE_GALLERY_RELOAD);
	}
	public boolean isOpen() {
		return openCollections.contains(this);
	}
}
