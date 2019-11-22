package control.reload;

import baseobject.CustomList;
import baseobject.entity.Entity;
import baseobject.entity.EntityList;
import main.InstanceCollector;

import java.lang.reflect.InvocationTargetException;

public class Reload implements InstanceCollector {
	private CustomList<Entity> needsTileEffect;
	private CustomList<InvokeHelper> queue;
	
	public Reload() {
	
	}
	
	public void init() {
		needsTileEffect = new CustomList<>();
		queue = new CustomList<>();
		
		try {
			InvokeHelper invokeHelper1 = new InvokeHelper(toolbarPane, toolbarPane.getClass().getMethod("reload"));
			ChangeIn.TARGET.getSubscribers().add(invokeHelper1);
			
			InvokeHelper invokeHelper2 = new InvokeHelper(galleryPane, galleryPane.getClass().getMethod("reload"));
			ChangeIn.ENTITY_LIST_MAIN.getSubscribers().add(invokeHelper2);
			ChangeIn.FILTER.getSubscribers().add(invokeHelper2);
			
			InvokeHelper invokeHelper3 = new InvokeHelper(mediaPane, mediaPane.getClass().getMethod("reload"));
			ChangeIn.TARGET.getSubscribers().add(invokeHelper3);
			
			InvokeHelper invokeHelper4 = new InvokeHelper(filterPane, filterPane.getClass().getMethod("reload"));
			InvokeHelper invokeHelper5 = new InvokeHelper(filterPane, filterPane.getClass().getMethod("refresh"));
			ChangeIn.TAG_LIST_MAIN.getSubscribers().add(invokeHelper4);
			ChangeIn.FILTER.getSubscribers().add(invokeHelper5);
			
			InvokeHelper invokeHelper6 = new InvokeHelper(selectPane, selectPane.getClass().getMethod("reload"));
			InvokeHelper invokeHelper7 = new InvokeHelper(selectPane, selectPane.getClass().getMethod("refresh"));
			ChangeIn.TAG_LIST_MAIN.getSubscribers().add(invokeHelper6);
			ChangeIn.TARGET.getSubscribers().add(invokeHelper7);
			ChangeIn.SELECT.getSubscribers().add(invokeHelper7);
			ChangeIn.TAGS_OF_SELECT.getSubscribers().add(invokeHelper7);
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
	}
	
	public void notify(ChangeIn... changeIns) {
		for (ChangeIn changeIn : changeIns) {
			queue.addAll(changeIn.getSubscribers(), true);
		}
	}
	
	public void doReload() {
		CustomList<InvokeHelper> successful = new CustomList<>();
		
		for (InvokeHelper invokeHelper : queue) {
			try {
				if ((boolean) invokeHelper.getMethod().invoke(invokeHelper.getInstance())) {
					successful.add(invokeHelper);
				}
			} catch (IllegalAccessException | InvocationTargetException e) {
				e.printStackTrace();
			}
		}
		
		queue.removeAll(successful);
		
		EntityList entityList = galleryPane.getEntitiesOfTiles();
		EntityList helper = new EntityList();
		for (Entity entity : needsTileEffect) {
			if (entityList.contains(entity)) {
				entity.getGalleryTile().updateSelectBorder();
				helper.add(entity);
			}
		}
		needsTileEffect.removeAll(helper);
	}
	
	public void requestTileEffect(EntityList entityList) {
		needsTileEffect.addAll(entityList);
	}
	public void requestTileEffect(Entity entity) {
		needsTileEffect.add(entity);
	}
}
