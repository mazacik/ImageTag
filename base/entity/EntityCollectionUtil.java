package base.entity;

import base.CustomList;
import base.tag.TagList;
import control.Select;
import control.reload.ChangeIn;
import control.reload.Reload;
import ui.stage.StageManager;

import java.util.Random;

public abstract class EntityCollectionUtil {
	public static void create() {
		TagList collectionTags = Select.getEntities().getTagsAll();
		
		if (collectionTags.isEmpty()) {
			create(collectionTags, false);
		} else {
			String s = "A collection of " + Select.getEntities().size() + " items will be created.\nMerge tags?";
			if (StageManager.getYesNoStage().show(s)) {
				create(collectionTags, true);
			} else {
				create(collectionTags, false);
			}
		}
	}
	private static void create(TagList collectionTags, boolean mergeTags) {
		int collectionID = EntityCollectionUtil.getID();
		
		if (mergeTags) {
			for (Entity entity : Select.getEntities()) {
				entity.setCollectionID(collectionID);
				entity.setCollection(Select.getEntities());
				entity.setTagList(collectionTags);
				entity.getGalleryTile().updateCollectionIcon();
			}
			Reload.notify(ChangeIn.TAGS_OF_SELECT);
		} else {
			for (Entity entity : Select.getEntities()) {
				entity.setCollectionID(collectionID);
				entity.setCollection(Select.getEntities());
				entity.getGalleryTile().updateCollectionIcon();
			}
		}
		
		Reload.notify(ChangeIn.ENTITY_LIST_MAIN);
	}
	private static int getID() {
		CustomList<Integer> collectionIDs = EntityCollectionUtil.getCollectionIDs();
		int collectionID;
		do {
			collectionID = new Random().nextInt();
		}
		while (collectionIDs.contains(collectionID));
		return collectionID;
	}
	private static CustomList<Integer> getCollectionIDs() {
		CustomList<Integer> collectionIDs = new CustomList<>();
		int collectionID;
		for (Entity entity : EntityList.getMain()) {
			collectionID = entity.getCollectionID();
			if (collectionID != 0) {
				collectionIDs.add(collectionID);
			}
		}
		return collectionIDs;
	}
	
	public static void discard(Entity entity) {
		for (Entity _entity : entity.getCollection()) {
			_entity.setCollectionID(0);
			_entity.setCollection(null);
			_entity.getGalleryTile().setEffect(null);
		}
		
		Reload.notify(ChangeIn.ENTITY_LIST_MAIN);
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
	
	private static CustomList<Integer> openCollections = new CustomList<>();
	public static void openCollection(Entity entity) {
		int collectionID = entity.getCollectionID();
		if (collectionID != 0) {
			if (openCollections.contains(collectionID)) {
				//noinspection RedundantCollectionOperation//todo try removing this + testing
				openCollections.remove(openCollections.indexOf(collectionID));
			} else {
				openCollections.add(collectionID);
			}
			entity.getGalleryTile().updateCollectionIcon();
			Reload.notify(ChangeIn.ENTITY_LIST_MAIN);
		}
	}
	public static CustomList<Integer> getOpenCollections() {
		return openCollections;
	}
}
