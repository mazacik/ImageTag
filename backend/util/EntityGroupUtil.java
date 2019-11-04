package application.backend.util;

import application.backend.base.entity.Entity;
import application.backend.base.entity.EntityList;
import application.main.InstanceCollector;

public abstract class EntityGroupUtil implements InstanceCollector {
	public static EntityList getEntityGroup(Entity entity) {
		int entityGroupID = entity.getEntityGroupID();
		
		if (entityGroupID == 0) {
			return new EntityList(entity);
		} else {
			EntityList entityGroup = new EntityList();
			for (Entity _entity : entityListMain) {
				if (entityGroupID == _entity.getEntityGroupID()) {
					entityGroup.add(_entity);
				}
			}
			return entityGroup;
		}
	}
}
