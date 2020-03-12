package control.filter;

import base.CustomList;
import base.entity.Entity;
import base.entity.EntityList;
import control.Select;
import control.reload.Notifier;
import control.reload.Reload;
import enums.MediaType;

import java.util.Collection;

public class Filter extends EntityList {
	private static final FilterSettings settings = new FilterSettings();
	private static final FilterListManager listManager = new FilterListManager();
	private static final EntityList lastImport = new EntityList();
	
	public boolean add(Entity entity) {
		if (super.addImpl(entity)) {
			Reload.notify(Notifier.FILTER_CHANGED);
			return true;
		} else {
			return false;
		}
	}
	public boolean addAll(Collection<? extends Entity> c) {
		if (super.addAllImpl(c)) {
			Reload.notify(Notifier.FILTER_CHANGED);
			return true;
		} else {
			return false;
		}
	}
	
	public boolean remove(Entity entity) {
		if (super.removeImpl(entity)) {
			Reload.notify(Notifier.FILTER_CHANGED);
			return true;
		} else {
			return false;
		}
	}
	public boolean removeAll(Collection<?> c) {
		if (super.removeAllImpl(c)) {
			Reload.notify(Notifier.FILTER_CHANGED);
			return true;
		} else {
			return false;
		}
	}
	
	public boolean set(Entity entity) {
		this.clear();
		return this.add(entity);
	}
	public boolean setAll(Collection<? extends Entity> c) {
		this.clear();
		return this.addAll(c);
	}
	
	public void clear() {
		super.clear();
		Reload.notify(Notifier.FILTER_CHANGED);
	}
	
	public static void reset() {
		listManager.clear();
		Reload.notify(Notifier.FILTER_CHANGED);
	}
	public static void refresh() {
		Select.storeTargetPosition();
		
		Filter.getEntities().clear();
		for (Entity entity : EntityList.getMain()) {
			if (Filter.isValid(entity)) {
				Filter.getEntities().addImpl(entity);
			}
		}
		
		Select.restoreTargetPosition();
		Reload.notify(Notifier.FILTER_CHANGED);
	}
	
	public static void resolve() {
		if (Select.getEntities().isEmpty()) {
			Filter.resolve(Select.getTarget());
		} else {
			Filter.resolve(Select.getEntities());
		}
	}
	public static void resolve(Entity entity) {
		boolean contains = Filter.getEntities().contains(entity);
		boolean valid = Filter.isValid(entity);
		
		if (contains && !valid) {
			Filter.getEntities().remove(entity);
			Reload.notify(Notifier.FILTER_CHANGED);
		} else if (!contains && valid) {
			Filter.getEntities().addImpl(entity);
			Reload.notify(Notifier.FILTER_CHANGED);//todo move to getEntities().add()
		}
	}
	public static void resolve(EntityList entities) {
		entities.forEach(Filter::resolve);
	}
	public static boolean isValid(Entity entity) {
		if (entity == null) return false;
		
		if (entity.getMediaType() == MediaType.IMAGE) {
			if (!settings.isShowImages()) {
				return false;
			}
		} else if (entity.getMediaType() == MediaType.GIF || entity.getMediaDuration() < 30000) {
			if (!settings.isShowGifs()) {
				return false;
			}
		} else if (entity.getMediaType() == MediaType.VIDEO) {
			if (!settings.isShowVideos()) {
				return false;
			}
		}
		
		if (settings.isShowOnlyNewEntities() && !lastImport.contains(entity)) {
			return false;
		}
		
		if (settings.isEnableLimit() && entity.getTagIDs().size() > settings.getLimit()) {
			return false;
		}
		
		return listManager.applyLists(entity);
	}
	
	public static void showSimilar(Entity entity) {
		listManager.clear();
		listManager.clear();
		getEntities().clear();
		
		CustomList<Integer> query = entity.getTagIDs();
		for (Entity iterator : EntityList.getMain()) {
			if (iterator.getTagIDs().size() != 0) {
				CustomList<Integer> sameTags = new CustomList<>(query);
				sameTags.retainAll(iterator.getTagIDs());
				
				if (!sameTags.isEmpty()) {
					double similarity = (double) sameTags.size() / (double) query.size();
					if (similarity >= settings.getSimilarityFactor()) {
						getEntities().addImpl(iterator);
					}
				}
			}
		}
	}
	public static EntityList getFilteredList(EntityList listOld) {
		EntityList returnValue = new EntityList(listOld);
		returnValue.retainAll(getEntities());
		return returnValue;
	}
	
	public static FilterSettings getSettings() {
		return settings;
	}
	public static FilterListManager getListManager() {
		return listManager;
	}
	public static EntityList getLastImport() {
		return lastImport;
	}
	
	private Filter() {}
	private static class Loader {
		private static final Filter INSTANCE = new Filter();
	}
	public static Filter getEntities() {
		return Loader.INSTANCE;
	}
}
