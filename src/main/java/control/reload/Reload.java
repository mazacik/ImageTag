package control.reload;

import base.CustomList;
import base.entity.Entity;
import base.entity.EntityList;
import control.filter.Filter;
import ui.main.display.PaneDisplay;
import ui.main.gallery.PaneGallery;
import ui.main.side.PaneFilter;
import ui.main.side.PaneSelect;
import ui.main.top.PaneToolbar;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;

public abstract class Reload {
	private final static CustomList<Entity> needsBorderUpdate = new CustomList<>();
	private final static CustomList<Notifier> notifiers = new CustomList<>();
	
	static {
		try {
			InvokeHelper ihFilterRefresh = new InvokeHelper(1, Filter.getEntities(), Filter.getEntities().getClass().getMethod("refresh"));
			
			InvokeHelper ihPToolbarReload = new InvokeHelper(5, PaneToolbar.getInstance(), PaneToolbar.getInstance().getClass().getMethod("reload"));
			InvokeHelper ihPGalleryReload = new InvokeHelper(5, PaneGallery.getInstance(), PaneGallery.getInstance().getClass().getMethod("reload"));
			InvokeHelper ihPDisplayReload = new InvokeHelper(5, PaneDisplay.getInstance(), PaneDisplay.getInstance().getClass().getMethod("reload"));
			InvokeHelper ihPFilterReload = new InvokeHelper(5, PaneFilter.getInstance(), PaneFilter.getInstance().getClass().getMethod("reload"));
			InvokeHelper ihPSelectReload = new InvokeHelper(5, PaneSelect.getInstance(), PaneSelect.getInstance().getClass().getMethod("reload"));
			
			InvokeHelper ihPFilterRefresh = new InvokeHelper(10, PaneFilter.getInstance(), PaneFilter.getInstance().getClass().getMethod("refresh"));
			InvokeHelper ihPSelectRefresh = new InvokeHelper(10, PaneSelect.getInstance(), PaneSelect.getInstance().getClass().getMethod("refresh"));
			
			Notifier.FILTER_NEEDS_REFRESH.getInvokeHelpers().add(ihFilterRefresh);
			
			Notifier.FILTER.getInvokeHelpers().add(ihPGalleryReload);
			Notifier.FILTER.getInvokeHelpers().add(ihPFilterRefresh);
			Notifier.FILTER.getInvokeHelpers().add(ihPSelectRefresh);
			
			Notifier.SELECT.getInvokeHelpers().add(ihPSelectRefresh);
			
			Notifier.TARGET.getInvokeHelpers().add(ihPToolbarReload);
			Notifier.TARGET.getInvokeHelpers().add(ihPDisplayReload);
			Notifier.TARGET.getInvokeHelpers().add(ihPSelectRefresh);
			
			Notifier.ENTITY_LIST_MAIN.getInvokeHelpers().add(ihPGalleryReload);
			
			Notifier.TAG_LIST_MAIN.getInvokeHelpers().add(ihPFilterReload);
			Notifier.TAG_LIST_MAIN.getInvokeHelpers().add(ihPSelectReload);
			Notifier.TAG_LIST_MAIN.getInvokeHelpers().add(ihPFilterRefresh);
			Notifier.TAG_LIST_MAIN.getInvokeHelpers().add(ihPSelectRefresh);
			
			Notifier.TAGS_OF_SELECT.getInvokeHelpers().add(ihPSelectRefresh);
			Notifier.VIEWMODE.getInvokeHelpers().add(ihPDisplayReload);
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
	}
	
	public static void notify(Notifier... notifiers) {
		if (notifiers.length == 1) {
			Reload.notifiers.add(notifiers[0], true);
		} else {
			Reload.notifiers.addAll(Arrays.asList(notifiers), true);
		}
	}
	public static void start() {
		CustomList<InvokeHelper> invokeHelpers = new CustomList<>();
		notifiers.forEach(notifier -> invokeHelpers.addAll(notifier.getInvokeHelpers(), true));
		notifiers.clear();
		
		invokeHelpers.sort(Comparator.comparing(InvokeHelper::getPriority));
		invokeHelpers.forEach(InvokeHelper::invoke);
		
		if (!notifiers.isEmpty()) start();
		
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
