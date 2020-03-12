package control.reload;

import base.CustomList;
import base.entity.Entity;
import base.entity.EntityList;
import control.Select;
import control.filter.Filter;
import ui.main.display.DisplayPane;
import ui.main.gallery.GalleryPane;
import ui.main.side.FilterPane;
import ui.main.side.SelectPane;
import ui.main.top.ToolbarPane;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;

public abstract class Reload {
	private final static CustomList<Entity> needsBorderUpdate = new CustomList<>();
	private final static CustomList<InvokeHelper> invokeHelpers = new CustomList<>();
	
	static {
		try {
			InvokeHelper filterRefresh = new InvokeHelper(1, Filter.getEntities(), Filter.getEntities().getClass().getMethod("refresh"));
			
			InvokeHelper paneFilterReload = new InvokeHelper(3, FilterPane.getInstance(), FilterPane.getInstance().getClass().getMethod("reload"));
			InvokeHelper paneSelectReload = new InvokeHelper(3, SelectPane.getInstance(), SelectPane.getInstance().getClass().getMethod("reload"));
			
			InvokeHelper paneFilterRefresh = new InvokeHelper(4, FilterPane.getInstance(), FilterPane.getInstance().getClass().getMethod("refresh"));
			InvokeHelper paneSelectRefresh = new InvokeHelper(4, SelectPane.getInstance(), SelectPane.getInstance().getClass().getMethod("refresh"));
			
			InvokeHelper paneToolbarReload = new InvokeHelper(5, ToolbarPane.getInstance(), ToolbarPane.getInstance().getClass().getMethod("reload"));
			InvokeHelper paneGalleryReload = new InvokeHelper(5, GalleryPane.getInstance(), GalleryPane.getInstance().getClass().getMethod("reload"));
			InvokeHelper paneDisplayReload = new InvokeHelper(5, DisplayPane.getInstance(), DisplayPane.getInstance().getClass().getMethod("reload"));
			
			link(Notifier.ENTITYLIST_CHANGED, paneGalleryReload, paneFilterRefresh);
			link(Notifier.TAGLIST_CHANGED, paneFilterReload, paneSelectReload, paneFilterRefresh, paneSelectRefresh);
			
			link(Notifier.FILTER_CHANGED, paneGalleryReload, paneFilterRefresh, paneSelectRefresh);
			link(Notifier.FILTER_NEEDS_REFRESH, filterRefresh);
			
			link(Notifier.SELECT_CHANGED, paneSelectRefresh);
			link(Notifier.SELECT_TAGLIST_CHANGED, paneSelectRefresh);
			
			link(Notifier.TARGET_CHANGED, paneToolbarReload, paneDisplayReload, paneSelectRefresh);
			link(Notifier.TARGET_COLLECTION_CHANGED, paneToolbarReload);
			
			link(Notifier.VIEWMODE_CHANGED, paneDisplayReload);
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
	}
	
	private static void link(Notifier notifier, InvokeHelper... invokeHelpers) {
		for (InvokeHelper invokeHelper : invokeHelpers) {
			notifier.getInvokeHelpers().addImpl(invokeHelper);
		}
	}
	
	public static void notify(Notifier... notifiers) {
		//todo create notify(InvokeHelper... ihs)
		//better performance, low level methods can notify without lagging the app
		if (notifiers.length == 1) {
			invokeHelpers.addAllImpl(notifiers[0].getInvokeHelpers(), true);
		} else {
			Arrays.asList(notifiers).forEach(notifier -> invokeHelpers.addAllImpl(notifier.getInvokeHelpers(), true));
		}
		
		invokeHelpers.sort(Comparator.comparing(InvokeHelper::getPriority));
	}
	public static void start() {
		while (!invokeHelpers.isEmpty()) {
			invokeHelpers.remove(0).invoke();
		}
		
		if (Select.getEntities().isEmpty()) {
			Select.setTarget(Filter.getEntities().getFirstImpl());
			Select.getEntities().setImpl(Select.getTarget());
		}
		
		//update tile borders
		EntityList helper = new EntityList();
		for (Entity entity : needsBorderUpdate) {
			entity.getTile().updateHighlight();
			helper.addImpl(entity);
		}
		needsBorderUpdate.removeAll(helper);
	}
	
	public static void requestBorderUpdate(Collection<? extends Entity> c) {
		needsBorderUpdate.addAllImpl(c);
	}
	public static void requestBorderUpdate(Entity entity) {
		needsBorderUpdate.addImpl(entity);
	}
}
