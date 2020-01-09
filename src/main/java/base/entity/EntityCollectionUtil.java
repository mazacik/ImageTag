package base.entity;

import base.CustomList;
import base.tag.TagList;
import control.Select;
import control.reload.Notifier;
import control.reload.Reload;
import ui.stage.StageConfirmation;

import java.util.Random;

public abstract class EntityCollectionUtil {
	public static void create() {
		int collectionID = new Random().nextInt();
		TagList collectionTags = Select.getEntities().getTags();
		
		String s = "A collection of " + Select.getEntities().size() + " items will be created.\nMerge tags?";
		if (!collectionTags.isEmpty() && StageConfirmation.show(s)) {
			for (Entity entity : Select.getEntities()) {
				entity.setCollectionID(collectionID);
				entity.setCollection(new EntityList(Select.getEntities()));
				entity.setTagList(collectionTags);
				entity.getTile().updateCollectionIcon();
			}
			Reload.notify(Notifier.TAGS_OF_SELECT);
		} else {
			for (Entity entity : Select.getEntities()) {
				entity.setCollectionID(collectionID);
				entity.setCollection(new EntityList(Select.getEntities()));
				entity.getTile().updateCollectionIcon();
			}
		}
		
		Reload.notify(Notifier.ENTITY_LIST_MAIN);
	}
	public static void discard() {
		for (Entity entity : Select.getEntities().getFirst().getCollection()) {
			entity.setCollectionID(0);
			entity.setCollection(null);
			entity.getTile().setEffect(null);
		}
		
		Reload.notify(Notifier.ENTITY_LIST_MAIN);
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
	public static boolean hasOpenOrNoCollection(Entity entity) {
		return entity.getCollectionID() == 0 || openCollections.contains(entity.getCollectionID());
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
			entity.getTile().updateCollectionIcon();
			Reload.notify(Notifier.ENTITY_LIST_MAIN);
		}
	}
	public static CustomList<Integer> getOpenCollections() {
		return openCollections;
	}
}
