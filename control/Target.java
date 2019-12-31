package control;

import base.CustomList;
import base.entity.Entity;
import base.entity.EntityList;
import control.reload.ChangeIn;
import control.reload.Reload;
import enums.Direction;
import javafx.scene.input.KeyCode;
import ui.main.center.PaneGallery;

import java.util.logging.Logger;

public class Target {
	private static Entity instance = null;
	
	private Target() {
	
	}
	
	public static Entity get() {
		return instance;
	}
	
	public static void set(Entity entity) {
		if (entity != null && entity != instance) {
			Reload.requestBorderUpdate(instance);
			Reload.requestBorderUpdate(entity);
			
			instance = entity;
			
			PaneGallery.get().moveViewportToTarget();
			Reload.notify(ChangeIn.TARGET);
			
			Logger.getGlobal().info(entity.getName());
		}
	}
	public static void move(Direction direction) {
		if (instance == null) return;
		
		EntityList entities = PaneGallery.get().getEntitiesOfTiles();
		if (entities.isEmpty()) return;
		
		int currentTargetIndex;
		if (instance.getCollectionID() == 0) {
			currentTargetIndex = entities.indexOf(instance);
		} else {
			if (PaneGallery.get().getExpandedCollections().contains(instance.getCollectionID())) {
				currentTargetIndex = entities.indexOf(instance);
			} else {
				Entity groupFirst = instance.getCollection().getFirst();
				if (entities.contains(groupFirst)) {
					currentTargetIndex = entities.indexOf(groupFirst);
				} else {
					currentTargetIndex = entities.indexOf(instance);
				}
			}
		}
		
		int columnCount = PaneGallery.get().getColumnCount();
		
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
		
		set(entities.get(newTargetIndex));
	}
	public static void move(KeyCode keyCode) {
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
	
	private static Entity storeEntity = null;
	private static int storePos = -1;
	public static void storePosition() {
		CustomList<Integer> expandedcollection = PaneGallery.get().getExpandedCollections();
		CustomList<Entity> visibleEntities = PaneGallery.get().getEntitiesOfTiles();
		
		if (instance.getCollectionID() == 0) {
			storeEntity = instance;
			storePos = visibleEntities.indexOf(instance);
		} else {
			if (expandedcollection.contains(instance.getCollectionID())) {
				storeEntity = instance;
				storePos = visibleEntities.indexOf(instance);
			} else {
				storeEntity = instance.getCollection().getFirst();
				storePos = visibleEntities.indexOf(storeEntity);
			}
		}
	}
	public static Entity restorePosition() {
		//todo if this lands on a non-expanded collection, add the whole collection
		//todo add move viewpoert to target
		EntityList visibleEntities = PaneGallery.get().getEntitiesOfTiles();
		if (!visibleEntities.isEmpty()) {
			if (storeEntity != null && visibleEntities.contains(storeEntity)) {
				set(storeEntity);
				return storeEntity;
			} else if (storePos >= 0) {
				Entity newTarget;
				
				if (storePos <= visibleEntities.size() - 1) newTarget = visibleEntities.get(storePos);
				else newTarget = visibleEntities.getLast();
				
				set(newTarget);
				
				if (Select.getEntities().isEmpty()) {
					Select.getEntities().add(newTarget);
				}
				
				return newTarget;
			}
		}
		return null;
	}
}
