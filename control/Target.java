package control;

import baseobject.CustomList;
import baseobject.entity.Entity;
import baseobject.entity.EntityList;
import control.reload.ChangeIn;
import javafx.scene.input.KeyCode;
import main.InstanceCollector;
import tools.enums.Direction;

import java.util.logging.Logger;

public class Target implements InstanceCollector {
	private Entity current;
	
	public Target() {
	
	}
	
	public void init() {
		current = null;
	}
	
	public Entity get() {
		return current;
	}
	
	public void set(Entity entity) {
		if (entity != null && entity != this.current) {
			reload.requestBorderUpdate(this.current);
			reload.requestBorderUpdate(entity);
			
			this.current = entity;
			
			galleryPane.moveViewportToTarget();
			reload.notify(ChangeIn.TARGET);
			
			Logger.getGlobal().info(entity.getName());
		}
	}
	public void move(Direction direction) {
		if (current == null) return;
		
		EntityList entities = galleryPane.getEntitiesOfTiles();
		if (entities.isEmpty()) return;
		
		int currentTargetIndex;
		if (current.getCollectionID() == 0) {
			currentTargetIndex = entities.indexOf(current);
		} else {
			if (galleryPane.getExpandedGroups().contains(current.getCollectionID())) {
				currentTargetIndex = entities.indexOf(current);
			} else {
				Entity groupFirst = current.getCollection().getFirst();
				if (entities.contains(groupFirst)) {
					currentTargetIndex = entities.indexOf(groupFirst);
				} else {
					currentTargetIndex = entities.indexOf(current);
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
		if (newTargetIndex >= entities.size()) newTargetIndex = entities.size() - 1;
		
		this.set(entities.get(newTargetIndex));
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
	
	private Entity storeEntity = null;
	private int storePos = -1;
	public void storePosition() {
		CustomList<Integer> expandedcollection = galleryPane.getExpandedGroups();
		CustomList<Entity> visibleEntities = galleryPane.getEntitiesOfTiles();
		
		if (current.getCollectionID() == 0) {
			storeEntity = current;
			storePos = visibleEntities.indexOf(current);
		} else {
			if (expandedcollection.contains(current.getCollectionID())) {
				storeEntity = current;
				storePos = visibleEntities.indexOf(current);
			} else {
				storeEntity = current.getCollection().getFirst();
				storePos = visibleEntities.indexOf(storeEntity);
			}
		}
	}
	public Entity restorePosition() {
		EntityList visibleEntities = galleryPane.getEntitiesOfTiles();
		if (!visibleEntities.isEmpty()) {
			if (storeEntity != null && visibleEntities.contains(storeEntity)) {
				this.set(storeEntity);
				return storeEntity;
			} else if (storePos >= 0) {
				Entity newTarget;
				
				if (storePos <= visibleEntities.size() - 1) newTarget = visibleEntities.get(storePos);
				else newTarget = visibleEntities.getLast();
				
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
