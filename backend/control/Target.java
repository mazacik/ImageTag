package application.backend.control;

import application.backend.base.CustomList;
import application.backend.base.entity.Entity;
import application.backend.base.entity.EntityList;
import application.backend.util.EntityGroupUtil;
import application.backend.util.enums.Direction;
import application.main.InstanceCollector;
import javafx.scene.input.KeyCode;

import java.util.logging.Logger;

public class Target implements InstanceCollector {
	public Target() {
	
	}
	
	public void init() {
	
	}
	
	private Entity entity = null;
	public Entity get() {
		if (entity == null && !entityListMain.isEmpty()) {
			entity = entityListMain.get(0);
		}
		return entity;
	}
	
	public void set(Entity entity) {
		if (entity == null || entity == this.entity) return;
		
		reload.requestTileEffect(this.entity);
		reload.requestTileEffect(entity);
		
		this.entity = entity;
		
		galleryPane.adjustViewportToTarget();
		reload.notify(Reload.Control.TARGET);
		Logger.getGlobal().info(entity.getName());
	}
	public void move(Direction direction) {
		if (entity == null) return;
		
		EntityList dataObjects = galleryPane.getDataObjectsOfTiles();
		if (dataObjects.isEmpty()) return;
		
		int currentTargetIndex;
		if (entity.getEntityGroupID() == 0) {
			currentTargetIndex = dataObjects.indexOf(entity);
		} else {
			if (galleryPane.getExpandedGroups().contains(entity.getEntityGroupID())) {
				currentTargetIndex = dataObjects.indexOf(entity);
			} else {
				Entity groupFirst = EntityGroupUtil.getEntityGroup(entity).getFirst();
				if (dataObjects.contains(groupFirst)) {
					currentTargetIndex = dataObjects.indexOf(groupFirst);
				} else {
					currentTargetIndex = dataObjects.indexOf(entity);
				}
			}
		}
		
		int columnCount = galleryPane.getColumnCount();
		
		int newTargetIndex = currentTargetIndex;
		switch (direction) {
			case UP:
				newTargetIndex -= columnCount;
				break;
			case LEFT:
				newTargetIndex -= 1;
				break;
			case DOWN:
				newTargetIndex += columnCount;
				break;
			case RIGHT:
				newTargetIndex += 1;
				break;
		}
		
		if (newTargetIndex < 0) newTargetIndex = 0;
		if (newTargetIndex >= dataObjects.size()) newTargetIndex = dataObjects.size() - 1;
		
		this.set(dataObjects.get(newTargetIndex));
	}
	public void move(KeyCode keyCode) {
		switch (keyCode) {
			case W:
				move(Direction.UP);
				break;
			case A:
				move(Direction.LEFT);
				break;
			case S:
				move(Direction.DOWN);
				break;
			case D:
				move(Direction.RIGHT);
				break;
		}
	}
	
	private Entity storeObject = null;
	private int storePos = -1;
	public void storePosition() {
		CustomList<Integer> expandedentityGroup = galleryPane.getExpandedGroups();
		CustomList<Entity> visibleEntities = galleryPane.getDataObjectsOfTiles();
		
		if (entity.getEntityGroupID() == 0) {
			storeObject = entity;
			storePos = visibleEntities.indexOf(entity);
		} else {
			if (expandedentityGroup.contains(entity.getEntityGroupID())) {
				storeObject = entity;
				storePos = visibleEntities.indexOf(entity);
			} else {
				storeObject = EntityGroupUtil.getEntityGroup(entity).getFirst();
				storePos = visibleEntities.indexOf(storeObject);
			}
		}
	}
	public Entity restorePosition() {
		EntityList visibleObjects = galleryPane.getDataObjectsOfTiles();
		if (!visibleObjects.isEmpty()) {
			if (storeObject != null && visibleObjects.contains(storeObject)) {
				this.set(storeObject);
				return storeObject;
			} else if (storePos >= 0) {
				Entity newTarget;
				
				if (storePos <= visibleObjects.size() - 1) newTarget = visibleObjects.get(storePos);
				else newTarget = visibleObjects.getLast();
				
				this.set(newTarget);
				
				if (select.isEmpty()) {
					select.add(newTarget);
				}
				
				return newTarget;
			}
		}
		return null;
	}
}
