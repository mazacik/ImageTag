package control.reload;

import base.CustomList;
import base.entity.Entity;
import base.entity.EntityList;
import ui.main.display.PaneDisplay;
import ui.main.gallery.PaneGallery;
import ui.main.side.PaneFilter;
import ui.main.side.PaneSelect;
import ui.main.top.PaneToolbar;

import java.util.Arrays;
import java.util.Collection;

public abstract class Reload {
	private final static CustomList<Entity> needsBorderUpdate = new CustomList<>();
	private final static CustomList<Notifier> notifiers = new CustomList<>();
	
	static {
		try {
			//  ToolbarPane
			InvokeHelper invokeHelper1 = new InvokeHelper(PaneToolbar.getInstance(), PaneToolbar.getInstance().getClass().getMethod("reload"));
			Notifier.TARGET.getInvokeHelpers().add(invokeHelper1);
			
			//  GalleryPane
			InvokeHelper invokeHelper2 = new InvokeHelper(PaneGallery.getInstance(), PaneGallery.getInstance().getClass().getMethod("reload"));
			Notifier.ENTITY_LIST_MAIN.getInvokeHelpers().add(invokeHelper2);
			Notifier.FILTER.getInvokeHelpers().add(invokeHelper2);
			
			//  MediaPane
			InvokeHelper invokeHelper3 = new InvokeHelper(PaneDisplay.getInstance(), PaneDisplay.getInstance().getClass().getMethod("reload"));
			Notifier.TARGET.getInvokeHelpers().add(invokeHelper3);
			Notifier.VIEWMODE.getInvokeHelpers().add(invokeHelper3);
			
			//  FilterPane
			InvokeHelper invokeHelper4 = new InvokeHelper(PaneFilter.getInstance(), PaneFilter.getInstance().getClass().getMethod("reload"));
			InvokeHelper invokeHelper5 = new InvokeHelper(PaneFilter.getInstance(), PaneFilter.getInstance().getClass().getMethod("refresh"));
			Notifier.TAG_LIST_MAIN.getInvokeHelpers().add(invokeHelper4);
			Notifier.FILTER.getInvokeHelpers().add(invokeHelper5);
			
			//  SelectPane
			InvokeHelper invokeHelper6 = new InvokeHelper(PaneSelect.getInstance(), PaneSelect.getInstance().getClass().getMethod("reload"));
			InvokeHelper invokeHelper7 = new InvokeHelper(PaneSelect.getInstance(), PaneSelect.getInstance().getClass().getMethod("refresh"));
			Notifier.TAG_LIST_MAIN.getInvokeHelpers().add(invokeHelper6);
			Notifier.TARGET.getInvokeHelpers().add(invokeHelper7);
			Notifier.SELECT.getInvokeHelpers().add(invokeHelper7);
			Notifier.TAGS_OF_SELECT.getInvokeHelpers().add(invokeHelper7);
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
	}
	
	public static void notify(Notifier... notifiers) {
		Reload.notifiers.addAll(Arrays.asList(notifiers), true);
	}
	public static void start() {
		CustomList<InvokeHelper> invokeHelpers = new CustomList<>();
		notifiers.forEach(notifier -> invokeHelpers.addAll(notifier.getInvokeHelpers(), true));
		notifiers.clear();
		
		invokeHelpers.forEach(InvokeHelper::invoke);
		
		//update tile borders
		EntityList helper = new EntityList();
		for (Entity entity : needsBorderUpdate) {
			entity.getTile().updateHighlight();
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
