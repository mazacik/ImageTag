package backend.control.reload;

import backend.list.BaseList;
import backend.list.entity.Entity;
import backend.list.entity.EntityList;
import main.Root;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;

public abstract class Reload {
	private final static BaseList<Entity> needsFilterCheck = new BaseList<>();
	private final static BaseList<Entity> needsBorderUpdate = new BaseList<>();
	private final static BaseList<InvokeHelper> invokeHelpers = new BaseList<>();
	
	static {
		link(Notifier.ENTITYLIST_CHANGED,
		     InvokeHelper.PANE_GALLERY_RELOAD,
		     InvokeHelper.PANE_FILTER_REFRESH
		);
		link(Notifier.TAGLIST_CHANGED,
		     InvokeHelper.PANE_FILTER_RELOAD,
		     InvokeHelper.PANE_SELECT_RELOAD,
		     InvokeHelper.PANE_FILTER_REFRESH,
		     InvokeHelper.PANE_SELECT_REFRESH
		);
		
		link(Notifier.FILTER_CHANGED,
		     InvokeHelper.PANE_GALLERY_RELOAD,
		     InvokeHelper.PANE_FILTER_REFRESH,
		     InvokeHelper.PANE_SELECT_REFRESH
		);
		link(Notifier.FILTER_NEEDS_REFRESH,
		     InvokeHelper.FILTER_REFRESH
		);
		
		link(Notifier.SELECT_CHANGED,
		     InvokeHelper.PANE_SELECT_REFRESH
		);
		link(Notifier.SELECT_TAGLIST_CHANGED,
		     InvokeHelper.PANE_SELECT_REFRESH
		);
		
		link(Notifier.TARGET_CHANGED,
		     InvokeHelper.PANE_TOOLBAR_RELOAD,
		     InvokeHelper.PANE_DISPLAY_RELOAD,
		     InvokeHelper.PANE_SELECT_REFRESH
		);
		link(Notifier.TARGET_COLLECTION_CHANGED,
		     InvokeHelper.PANE_TOOLBAR_RELOAD,
		     InvokeHelper.PANE_GALLERY_RELOAD
		);
		
		link(Notifier.VIEWMODE_CHANGED,
		     InvokeHelper.PANE_DISPLAY_RELOAD
		);
	}
	
	private static void link(Notifier notifier, InvokeHelper... invokeHelpers) {
		for (InvokeHelper invokeHelper : invokeHelpers) {
			notifier.getInvokeHelpers().addImpl(invokeHelper);
		}
	}
	
	public static void request(InvokeHelper... invokeHelpers) {
		if (invokeHelpers.length == 1) {
			Reload.invokeHelpers.addImpl(invokeHelpers[0]);
		} else {
			Arrays.asList(invokeHelpers).forEach(invokeHelper -> Reload.invokeHelpers.addImpl(invokeHelper, true));
		}
		
		Reload.invokeHelpers.sort(Comparator.comparing(InvokeHelper::getPriority));
	}
	public static void notify(Notifier... notifiers) {
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
		
		if (Root.SELECT.isEmpty()) {
			Entity target = Root.FILTER.getFirst();
			if (target != null) {
				Root.SELECT.setTarget(target);
				if (target.hasCollection()) {
					Root.SELECT.setAll(target.getCollection());
				} else {
					Root.SELECT.set(target);
				}
			}
		}
		
		//update tile borders
		EntityList helper = new EntityList();
		for (Entity entity : needsBorderUpdate) {
			entity.getTile().updateBorder();
			helper.addImpl(entity);
		}
		needsBorderUpdate.removeAll(helper);
	}
	
	public static void requestFilterCheck(Collection<? extends Entity> c) {
		needsFilterCheck.addAllImpl(c);
	}
	public static void requestFilterCheck(Entity entity) {
		needsFilterCheck.addImpl(entity);
	}
	public static BaseList<Entity> getNeedsFilterCheck() {
		return needsFilterCheck;
	}
	
	public static void requestBorderUpdate(Collection<? extends Entity> c) {
		needsBorderUpdate.addAllImpl(c);
	}
	public static void requestBorderUpdate(Entity entity) {
		needsBorderUpdate.addImpl(entity);
	}
}
