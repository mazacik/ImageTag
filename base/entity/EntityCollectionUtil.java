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
		int collectionID = new Random().nextInt();
		TagList collectionTags = Select.getEntities().getTagsAll();
		
		String s = "A collection of " + Select.getEntities().size() + " items will be created.\nMerge tags?";
		if (!collectionTags.isEmpty() && StageManager.getYesNoStage().show(s)) {
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
	public static void discard() {
		for (Entity entity : Select.getEntities().getFirst().getCollection()) {
			entity.setCollectionID(0);
			entity.setCollection(null);
			entity.getGalleryTile().setEffect(null);
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
				openCollections.remove((Integer) collectionID);
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
