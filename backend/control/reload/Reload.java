package application.backend.control.reload;

import application.backend.base.CustomList;
import application.backend.base.entity.Entity;
import application.backend.base.entity.EntityList;
import application.main.InstanceCollector;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Reload implements InstanceCollector {
	private CustomList<Reloadable> reloadables;
	private CustomList<Entity> needsTileEffect;
	
	public Reload() {
	
	}
	
	public void init() {
		reloadables = new CustomList<>();
		reloadables.add(toolbarPane);
		reloadables.add(galleryPane);
		reloadables.add(mediaPane);
		reloadables.add(filterPane);
		reloadables.add(selectPane);
		
		needsTileEffect = new CustomList<>();
		
		try {
			this.subscribe(ChangeIn.TARGET, toolbarPane, toolbarPane.getClass().getMethod("reload"));
			
			this.subscribe(ChangeIn.ENTITY_LIST_MAIN, galleryPane, galleryPane.getClass().getMethod("reload"));
			this.subscribe(ChangeIn.FILTER, galleryPane, galleryPane.getClass().getMethod("reload"));
			
			this.subscribe(ChangeIn.TARGET, mediaPane, mediaPane.getClass().getMethod("reload"));
			
			this.subscribe(ChangeIn.TAG_LIST_MAIN, filterPane, filterPane.getClass().getMethod("reload"));
			this.subscribe(ChangeIn.FILTER, filterPane, filterPane.getClass().getMethod("refresh"));
			
			this.subscribe(ChangeIn.TAG_LIST_MAIN, selectPane, selectPane.getClass().getMethod("reload"));
			this.subscribe(ChangeIn.TARGET, selectPane, selectPane.getClass().getMethod("refresh"));
			this.subscribe(ChangeIn.SELECT, selectPane, selectPane.getClass().getMethod("refresh"));
			this.subscribe(ChangeIn.TAGS_OF_SELECT, selectPane, selectPane.getClass().getMethod("refresh"));
			
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
	}
	
	private void subscribe(ChangeIn onChangeIn, Reloadable reloadable, Method method) {
		onChangeIn.getSubscribers().add(new InvokeHelper(reloadable, method), true);
	}
	
	public void notify(ChangeIn... changeIns) {
		for (ChangeIn changeIn : changeIns) {
			for (InvokeHelper invokeHelper : changeIn.getSubscribers()) {
				invokeHelper.getInstance().getMethodsToInvokeOnNextReload().add(invokeHelper.getMethod(), true);
			}
		}
	}
	public void request(Reloadable reloadable, String method) {
		try {
			reloadable.getMethodsToInvokeOnNextReload().add(reloadable.getClass().getMethod(method), true);
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
	}
	
	public void doReload() {
		for (Reloadable reloadable : reloadables) {
			CustomList<Method> invokedMethods = new CustomList<>();
			
			for (Method method : reloadable.getMethodsToInvokeOnNextReload()) {
				try {
					if ((boolean) method.invoke(reloadable)) {
						invokedMethods.add(method, true);
					}
				} catch (IllegalAccessException | InvocationTargetException e) {
					e.printStackTrace();
				}
			}
			
			reloadable.getMethodsToInvokeOnNextReload().removeAll(invokedMethods);
			invokedMethods.clear();
		}
		
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
