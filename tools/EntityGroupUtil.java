package application.tools;

import application.baseobject.entity.Entity;
import application.baseobject.entity.EntityList;
import application.main.InstanceCollector;

public abstract class EntityGroupUtil implements InstanceCollector {
	//todo load this into runtime memory on start
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
