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
	private final static CustomList<Notifier> notifiers = new CustomList<>();
	private final static CustomList<InvokeHelper> invokeHelpers = new CustomList<>();
	
	static {
		try {
			InvokeHelper ihFilterRefresh = new InvokeHelper(1, Filter.getEntities(), Filter.getEntities().getClass().getMethod("refresh"));
			
			InvokeHelper ihPFilterReload = new InvokeHelper(3, FilterPane.getInstance(), FilterPane.getInstance().getClass().getMethod("reload"));
			InvokeHelper ihPSelectReload = new InvokeHelper(3, SelectPane.getInstance(), SelectPane.getInstance().getClass().getMethod("reload"));
			
			InvokeHelper ihPFilterRefresh = new InvokeHelper(4, FilterPane.getInstance(), FilterPane.getInstance().getClass().getMethod("refresh"));
			InvokeHelper ihPSelectRefresh = new InvokeHelper(4, SelectPane.getInstance(), SelectPane.getInstance().getClass().getMethod("refresh"));
			
			InvokeHelper ihPToolbarReload = new InvokeHelper(5, ToolbarPane.getInstance(), ToolbarPane.getInstance().getClass().getMethod("reload"));
			InvokeHelper ihPGalleryReload = new InvokeHelper(5, GalleryPane.getInstance(), GalleryPane.getInstance().getClass().getMethod("reload"));
			InvokeHelper ihPDisplayReload = new InvokeHelper(5, DisplayPane.getInstance(), DisplayPane.getInstance().getClass().getMethod("reload"));
			
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
			invokeHelpers.addAll(notifiers[0].getInvokeHelpers(), true);
		} else {
			Arrays.asList(notifiers).forEach(notifier -> invokeHelpers.addAll(notifier.getInvokeHelpers(), true));
		}
		
		invokeHelpers.sort(Comparator.comparing(InvokeHelper::getPriority));
	}
	public static void start() {
		while (!invokeHelpers.isEmpty()) {
			invokeHelpers.remove(0).invoke();
		}
		
		if (Select.getEntities().isEmpty()) {
			Select.setTarget(Filter.getEntities().getFirst());
			Select.getEntities().set(Select.getTarget());
		}
		
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
