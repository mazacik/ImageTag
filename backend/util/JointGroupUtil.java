package application.backend.util;

import application.backend.base.entity.Entity;
import application.backend.base.entity.EntityList;
import application.main.InstanceCollector;

public abstract class JointGroupUtil implements InstanceCollector {
	public static EntityList getJointObjects(Entity entity) {
		int jointID = entity.getJointID();
		
		if (jointID == 0) {
			return new EntityList(entity);
		} else {
			EntityList jointObjects = new EntityList();
			for (Entity _entity : entityListMain) {
				if (jointID == _entity.getJointID()) {
					jointObjects.add(_entity);
				}
			}
			return jointObjects;
		}
	}
}
