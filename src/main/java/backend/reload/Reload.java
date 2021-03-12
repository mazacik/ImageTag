package backend.reload;

import backend.BaseList;
import backend.entity.Entity;
import backend.entity.EntityList;
import main.Main;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;

public abstract class Reload {
	private final static BaseList<Entity> needsFilterCheck = new BaseList<>();
	private final static BaseList<Entity> needsBorderUpdate = new BaseList<>();
	private final static BaseList<InvokeHelper> invokeHelpers = new BaseList<>();
	
	static {
		link(Notifier.ENTITYLIST_CHANGED,
		     InvokeHelper.FILTER_REFRESH,
		     InvokeHelper.PANE_GALLERY_RELOAD,
		     InvokeHelper.PANE_FILTER_RELOAD
		);
		link(Notifier.TAGLIST_CHANGED,
		     InvokeHelper.PANE_FILTER_RELOAD,
		     InvokeHelper.PANE_SELECT_RELOAD
		);
		
		link(Notifier.FILTER_CHANGED,
		     InvokeHelper.PANE_GALLERY_RELOAD,
		     InvokeHelper.PANE_FILTER_RELOAD,
		     InvokeHelper.PANE_SELECT_RELOAD
		);
		link(Notifier.FILTER_NEEDS_REFRESH,
		     InvokeHelper.FILTER_REFRESH
		);
		
		link(Notifier.SELECT_CHANGED,
		     InvokeHelper.PANE_SELECT_RELOAD
		);
		link(Notifier.SELECT_TAGLIST_CHANGED,
		     InvokeHelper.PANE_SELECT_RELOAD
		);
		
		link(Notifier.TARGET_CHANGED,
		     InvokeHelper.PANE_TOOLBAR_RELOAD,
		     InvokeHelper.PANE_DISPLAY_RELOAD,
		     InvokeHelper.PANE_SELECT_RELOAD
		);
		link(Notifier.TARGET_GROUP_CHANGED,
		     InvokeHelper.PANE_TOOLBAR_RELOAD,
		     InvokeHelper.PANE_GALLERY_RELOAD
		);
		
		link(Notifier.VIEWMODE_CHANGED,
		     InvokeHelper.PANE_DISPLAY_RELOAD
		);
	}
	
	private static void link(Notifier notifier, InvokeHelper... invokeHelpers) {
		for (InvokeHelper invokeHelper : invokeHelpers) {
			notifier.getInvokeHelpers().add(invokeHelper);
		}
	}
	
	public static void request(InvokeHelper... invokeHelpers) {
		if (invokeHelpers.length == 1) {
			Reload.invokeHelpers.add(invokeHelpers[0]);
		} else {
			Arrays.asList(invokeHelpers).forEach(invokeHelper -> Reload.invokeHelpers.add(invokeHelper, true));
		}
		
		Reload.invokeHelpers.sort(Comparator.comparing(InvokeHelper::getPriority));
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
		
		if (Main.SELECT.isEmpty()) {
			Entity target = Main.FILTER.getFirst();
			if (target != null) {
				Main.SELECT.setTarget(target);
				if (target.hasGroup()) {
					Main.SELECT.setAll(target.getGroup());
				} else {
					Main.SELECT.set(target);
				}
			}
		}
		
		//update tile borders
		EntityList helper = new EntityList();
		for (Entity entity : needsBorderUpdate) {
			entity.getTile().updateBorder();
			helper.add(entity);
		}
		needsBorderUpdate.removeAll(helper);
	}
	
	public static void requestFilterCheck(Collection<? extends Entity> c) {
		needsFilterCheck.addAll(c, true);
	}
	public static void requestFilterCheck(Entity entity) {
		needsFilterCheck.add(entity, true);
	}
	public static BaseList<Entity> getNeedsFilterCheck() {
		return needsFilterCheck;
	}
	
	public static void requestBorderUpdate(Collection<? extends Entity> c) {
		needsBorderUpdate.addAll(c);
	}
	public static void requestBorderUpdate(Entity entity) {
		needsBorderUpdate.add(entity);
	}
}
