package control.reload;

import base.CustomList;
import base.entity.Entity;
import base.entity.EntityList;
import ui.main.center.PaneGallery;
import ui.main.center.PaneEntity;
import ui.main.side.left.PaneFilter;
import ui.main.side.right.PaneSelect;
import ui.main.top.PaneToolbar;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

public class Reload {
	private final static CustomList<Entity> needsBorderUpdate = new CustomList<>();
	private final static CustomList<InvokeHelper> queue = new CustomList<>();
	
	private Reload() {
	
	}
	
	public static void init() {
		try {
			//  ToolbarPane
			InvokeHelper invokeHelper1 = new InvokeHelper(PaneToolbar.get(), PaneToolbar.get().getClass().getMethod("reload"));
			ChangeIn.TARGET.getSubscribers().add(invokeHelper1);
			
			//  GalleryPane
			InvokeHelper invokeHelper2 = new InvokeHelper(PaneGallery.get(), PaneGallery.get().getClass().getMethod("reload"));
			ChangeIn.ENTITY_LIST_MAIN.getSubscribers().add(invokeHelper2);
			ChangeIn.FILTER.getSubscribers().add(invokeHelper2);
			
			//  MediaPane
			InvokeHelper invokeHelper3 = new InvokeHelper(PaneEntity.get(), PaneEntity.get().getClass().getMethod("reload"));
			ChangeIn.TARGET.getSubscribers().add(invokeHelper3);
			ChangeIn.VIEWMODE.getSubscribers().add(invokeHelper3);
			
			//  FilterPane
			InvokeHelper invokeHelper4 = new InvokeHelper(PaneFilter.get(), PaneFilter.get().getClass().getMethod("reload"));
			InvokeHelper invokeHelper5 = new InvokeHelper(PaneFilter.get(), PaneFilter.get().getClass().getMethod("refresh"));
			ChangeIn.TAG_LIST_MAIN.getSubscribers().add(invokeHelper4);
			ChangeIn.FILTER.getSubscribers().add(invokeHelper5);
			
			//  SelectPane
			InvokeHelper invokeHelper6 = new InvokeHelper(PaneSelect.get(), PaneSelect.get().getClass().getMethod("reload"));
			InvokeHelper invokeHelper7 = new InvokeHelper(PaneSelect.get(), PaneSelect.get().getClass().getMethod("refresh"));
			ChangeIn.TAG_LIST_MAIN.getSubscribers().add(invokeHelper6);
			ChangeIn.TARGET.getSubscribers().add(invokeHelper7);
			ChangeIn.SELECT.getSubscribers().add(invokeHelper7);
			ChangeIn.TAGS_OF_SELECT.getSubscribers().add(invokeHelper7);
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
	}
	
	public static void notify(ChangeIn... changeIns) {
		for (ChangeIn changeIn : changeIns) {
			queue.addAll(changeIn.getSubscribers(), true);
		}
	}
	public static void start() {
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
	
	public static void requestBorderUpdate(Collection<? extends Entity> c) {
		needsBorderUpdate.addAll(c);
	}
	public static void requestBorderUpdate(Entity entity) {
		needsBorderUpdate.add(entity);
	}
}
