package tools;

import baseobject.CustomList;
import baseobject.entity.Entity;
import baseobject.entity.EntityList;
import baseobject.tag.TagList;
import control.reload.ChangeIn;
import gui.stage.StageManager;
import main.InstanceCollector;

import java.util.Random;

public abstract class CollectionUtil implements InstanceCollector {
	public static void init() {
		CustomList<EntityList> collections = new CustomList<>();
		int collectionID;
		for (Entity entity : mainEntityList) {
			collectionID = entity.getCollectionID();
			if (collectionID != 0) {
				boolean match = false;
				for (EntityList collection : collections) {
					if (collection.getFirst().getCollectionID() == collectionID) {
						collection.add(entity);
						entity.setCollection(collection);
						match = true;
						break;
					}
				}
				if (!match) {
					EntityList collectionNew = new EntityList(entity);
					collections.add(collectionNew);
					entity.setCollection(collectionNew);
				}
			}
		}
	}
	
	public static void create() {
		TagList collectionTags = select.getTagsAll();
		
		if (collectionTags.isEmpty()) {
			create(collectionTags, false);
		} else {
			String s = "A collection of " + select.size() + " items will be created.\nMerge tags?";
			if (StageManager.getYesNoStage().show(s)) {
				create(collectionTags, true);
			} else {
				create(collectionTags, false);
			}
		}
	}
	private static void create(TagList collectionTags, boolean mergeTags) {
		int collectionID = CollectionUtil.getID();
		
		if (mergeTags) {
			for (Entity entity : select) {
				entity.setCollectionID(collectionID);
				entity.setCollection(select);
				entity.setTagList(collectionTags);
				entity.getGalleryTile().updateGroupIcon();
			}
			reload.notify(ChangeIn.TAGS_OF_SELECT);
		} else {
			for (Entity entity : select) {
				entity.setCollectionID(collectionID);
				entity.setCollection(select);
				entity.getGalleryTile().updateGroupIcon();
			}
		}
		
		reload.notify(ChangeIn.ENTITY_LIST_MAIN);
	}
	public static void discard(Entity entity) {
		for (Entity _entity : entity.getCollection()) {
			_entity.setCollectionID(0);
			_entity.setCollection(null);
			_entity.getGalleryTile().setEffect(null);
		}
		
		reload.notify(ChangeIn.ENTITY_LIST_MAIN);
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
	
	private static CustomList<Integer> getCollectionIDs() {
		CustomList<Integer> collectionIDs = new CustomList<>();
		int collectionID;
		for (Entity entity : mainEntityList) {
			collectionID = entity.getCollectionID();
			if (collectionID != 0) {
				collectionIDs.add(collectionID);
			}
		}
		return collectionIDs;
	}
	private static int getID() {
		CustomList<Integer> collectionIDs = CollectionUtil.getCollectionIDs();
		int collectionID;
		do {
			collectionID = new Random().nextInt();
		}
		while (collectionIDs.contains(collectionID));
		return collectionID;
	}
}
