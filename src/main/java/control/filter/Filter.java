package control.filter;

import base.CustomList;
import base.entity.Entity;
import base.entity.EntityList;
import control.reload.Notifier;
import control.reload.Reload;
import enums.MediaType;

public class Filter extends EntityList {
	private static final FilterSettings settings = new FilterSettings();
	private static final FilterListManager listManager = new FilterListManager();
	private static final EntityList newEntities = new EntityList();
	
	public static void reset() {
		listManager.clear();
		Reload.notify(Notifier.TAG_LIST_MAIN);
	}
	public static void refresh() {
		getEntities().clear();
		
		for (Entity entity : EntityList.getMain()) {
			if (entity.getMediaType() == MediaType.IMAGE) {
				if (!settings.isShowImages()) {
					continue;
				}
			} else if (entity.getMediaType() == MediaType.GIF || entity.getMediaDuration() < 30000) {
				if (!settings.isShowGifs()) {
					continue;
				}
			} else if (entity.getMediaType() == MediaType.VIDEO) {
				if (!settings.isShowVideos()) {
					continue;
				}
			}
			
			if (settings.isShowOnlyNewEntities() && !newEntities.contains(entity)) {
				continue;
			}
			
			if (settings.isEnableLimit() && entity.getTagIDs().size() > settings.getLimit()) {
				continue;
			}
			
			if (!listManager.applyLists(entity)) {
				continue;
			}
			
			getEntities().add(entity);
		}
		
		Reload.notify(Notifier.FILTER);
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
						getEntities().add(iterator);
					}
				}
			}
		}
	}
	public static EntityList applyTo(EntityList listBefore) {
		EntityList returnValue = new EntityList(listBefore);
		returnValue.retainAll(getEntities());
		return returnValue;
	}
	
	public static FilterSettings getSettings() {
		return settings;
	}
	public static FilterListManager getListManager() {
		return listManager;
	}
	public static EntityList getNewEntities() {
		return newEntities;
	}
	
	private Filter() {}
	private static class Loader {
		private static final Filter INSTANCE = new Filter();
	}
	public static Filter getEntities() {
		return Loader.INSTANCE;
	}
}
