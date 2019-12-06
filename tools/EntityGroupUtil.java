package tools;

import baseobject.CustomList;
import baseobject.entity.Entity;
import baseobject.entity.EntityList;
import baseobject.tag.TagList;
import control.reload.ChangeIn;
import gui.stage.StageManager;
import gui.stage.template.ButtonBooleanValue;
import main.InstanceCollector;

import java.util.Random;

public abstract class EntityGroupUtil implements InstanceCollector {
	public static void initGroups() {
		CustomList<EntityList> entityGroups = new CustomList<>();
		int entityGroupID;
		for (Entity entity : entityListMain) {
			entityGroupID = entity.getEntityGroupID();
			if (entityGroupID != 0) {
				boolean match = false;
				for (EntityList entityGroup : entityGroups) {
					if (entityGroup.getFirst().getEntityGroupID() == entityGroupID) {
						entityGroup.add(entity);
						entity.setEntityGroup(entityGroup);
						match = true;
						break;
					}
				}
				if (!match) {
					EntityList entityGroupNew = new EntityList(entity);
					entityGroups.add(entityGroupNew);
					entity.setEntityGroup(entityGroupNew);
				}
			}
		}
	}
	public static void createGroup(EntityList entityList) {
		ButtonBooleanValue result = StageManager.getYesNoCancelStage().show("Merge tags? (" + entityList.size() + " items selected)");
		if (result != ButtonBooleanValue.CANCEL) {
			int entityGroupID = EntityGroupUtil.getID();
			
			if (result.getBooleanValue()) {
				//merge tags
				TagList tagList = new TagList();
				for (Entity entity : entityList) {
					tagList.addAll(entity.getTagList());
				}
				for (Entity entity : entityList) {
					entity.setEntityGroupID(entityGroupID);
					entity.setEntityGroup(entityList);
					entity.setTagList(tagList);
					entity.getGalleryTile().updateGroupIcon();
				}
				reload.notify(ChangeIn.TAGS_OF_SELECT);
			} else {
				//don't merge tags
				for (Entity entity : entityList) {
					entity.setEntityGroupID(entityGroupID);
					entity.setEntityGroup(entityList);
					entity.getGalleryTile().updateGroupIcon();
				}
			}
			
			target.set(entityList.getFirst());
			reload.notify(ChangeIn.ENTITY_LIST_MAIN);
		}
	}
	public static void discardGroup(EntityList entityList) {
		if (EntityGroupUtil.isGroup(entityList)) {
			for (Entity entity : entityList) {
				entity.setEntityGroupID(0);
				entity.setEntityGroup(null);
				entity.getGalleryTile().setEffect(null);
			}
			reload.notify(ChangeIn.ENTITY_LIST_MAIN/*, ChangeIn.TAGS_OF_SELECT*/);
		}
	}
	public static boolean isGroup(EntityList entityList) {
		int entityGroupID = entityList.getFirst().getEntityGroupID();
		if (entityGroupID == 0) return false;
		for (Entity entity : entityList) {
			if (entity.getEntityGroupID() != entityGroupID) {
				return false;
			}
		}
		return true;
	}
	
	private static CustomList<Integer> getEntityGroupIDs() {
		CustomList<Integer> entityGroupIDs = new CustomList<>();
		int entityGroupID;
		for (Entity entity : entityListMain) {
			entityGroupID = entity.getEntityGroupID();
			if (entityGroupID != 0) {
				entityGroupIDs.add(entityGroupID);
			}
		}
		return entityGroupIDs;
	}
	private static int getID() {
		CustomList<Integer> entityGroupIDs = EntityGroupUtil.getEntityGroupIDs();
		int entityGroupID;
		do {
			entityGroupID = new Random().nextInt();
		}
		while (entityGroupIDs.contains(entityGroupID));
		return entityGroupID;
	}
}
