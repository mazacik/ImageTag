package control.reload;

import baseobject.CustomList;
import baseobject.entity.Entity;
import baseobject.entity.EntityList;
import main.InstanceCollector;

import java.lang.reflect.InvocationTargetException;

public class Reload implements InstanceCollector {
	private CustomList<Entity> needsBorderUpdate;
	private CustomList<InvokeHelper> queue;
	
	public Reload() {
		needsBorderUpdate = new CustomList<>();
		queue = new CustomList<>();
		
		try {
			//  ToolbarPane
			InvokeHelper invokeHelper1 = new InvokeHelper(paneToolbar, paneToolbar.getClass().getMethod("reload"));
			ChangeIn.TARGET.getSubscribers().add(invokeHelper1);
			
			//  GalleryPane
			InvokeHelper invokeHelper2 = new InvokeHelper(paneGallery, paneGallery.getClass().getMethod("reload"));
			ChangeIn.ENTITY_LIST_MAIN.getSubscribers().add(invokeHelper2);
			ChangeIn.FILTER.getSubscribers().add(invokeHelper2);
			
			//  MediaPane
			InvokeHelper invokeHelper3 = new InvokeHelper(paneEntity, paneEntity.getClass().getMethod("reload"));
			ChangeIn.TARGET.getSubscribers().add(invokeHelper3);
			ChangeIn.VIEWMODE.getSubscribers().add(invokeHelper3);
			
			//  FilterPane
			InvokeHelper invokeHelper4 = new InvokeHelper(paneFilter, paneFilter.getClass().getMethod("reload"));
			InvokeHelper invokeHelper5 = new InvokeHelper(paneFilter, paneFilter.getClass().getMethod("refresh"));
			ChangeIn.TAG_LIST_MAIN.getSubscribers().add(invokeHelper4);
			ChangeIn.FILTER.getSubscribers().add(invokeHelper5);
			
			//  SelectPane
			InvokeHelper invokeHelper6 = new InvokeHelper(paneSelect, paneSelect.getClass().getMethod("reload"));
			InvokeHelper invokeHelper7 = new InvokeHelper(paneSelect, paneSelect.getClass().getMethod("refresh"));
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
		while (!queue.isEmpty()) {
			InvokeHelper invokeHelper = queue.getFirst();
			try {
				invokeHelper.getMethod().invoke(invokeHelper.getInstance());
			} catch (IllegalAccessException | InvocationTargetException e) {
				e.printStackTrace();
			}
			queue.remove(invokeHelper);
		}
		
		//update borders of affected tiles
		EntityList helper = new EntityList();
		for (Entity entity : needsBorderUpdate) {
			entity.getGalleryTile().updateSelectBorder();
			helper.add(entity);
		}
		needsBorderUpdate.removeAll(helper);
	}
	
	public void requestBorderUpdate(EntityList entityList) {
		needsBorderUpdate.addAll(entityList);
	}
	public void requestBorderUpdate(Entity entity) {
		needsBorderUpdate.add(entity);
	}
}
