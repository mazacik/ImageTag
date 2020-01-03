package control.filter;

import base.CustomList;
import base.entity.Entity;
import base.entity.EntityList;
import base.tag.TagList;
import control.Select;
import control.reload.ChangeIn;
import control.reload.Reload;

public class Filter extends EntityList {
	private static final FilterSettings settings = new FilterSettings();
	private static final FilterListManager listManager = new FilterListManager();
	private static final EntityList newEntities = new EntityList();
	
	public boolean add(Entity entity) {
		if (super.add(entity)) {
			Reload.notify(ChangeIn.FILTER);
			return true;
		} else {
			return false;
		}
	}
	public void clear() {
		super.clear();
		Reload.notify(ChangeIn.FILTER);
	}
	
	public static void reset() {
		listManager.getWhitelist().clear();
		listManager.getBlacklist().clear();
		Reload.notify(ChangeIn.FILTER);
	}
	public static void refresh() {
		Loader.INSTANCE.clear();
		long mediaDuration;
		for (Entity entity : EntityList.getMain()) {
			mediaDuration = entity.getMediaDuration();
			if (mediaDuration == 0) {
				if (!settings.isShowImages()) continue;
			} else if (mediaDuration >= 1 && mediaDuration < 30000) {
				if (!settings.isShowGifs()) continue;
			} else if (mediaDuration >= 30000) {
				if (!settings.isShowVideos()) continue;
			} else {
				continue;
			}
			
			if (settings.isShowOnlyNewEntities() && !newEntities.contains(entity)) {
				continue;
			}
			
			TagList tagList = entity.getTagList();
			if (settings.isEnableLimit() && tagList.size() > settings.getLimit()) {
				continue;
			}
			
			if (listManager.isWhitelistOk(tagList) && listManager.isBlacklistOk(tagList)) {
				Loader.INSTANCE.add(entity);
			}
		}
		
		if (!Loader.INSTANCE.contains(Select.getTarget())) {
			Select.setTarget(Loader.INSTANCE.getFirst());
		}
		
		for (Entity entity : new CustomList<>(Select.getEntities())) {
			if (!Loader.INSTANCE.contains(entity)) {
				Select.getEntities().remove(entity);
			}
		}
		if (Select.getEntities().isEmpty()) {
			Select.getEntities().set(Select.getTarget());
		}
	}
	
	public static void showSimilar(Entity entity) {
		listManager.getWhitelist().clear();
		listManager.getBlacklist().clear();
		Loader.INSTANCE.clear();
		
		TagList query = entity.getTagList();
		for (Entity iterator : EntityList.getMain()) {
			if (iterator.getTagList().size() != 0) {
				TagList sameTags = new TagList(query);
				sameTags.retainAll(iterator.getTagList());
				
				if (!sameTags.isEmpty()) {
					double similarity = (double) sameTags.size() / (double) query.size();
					if (similarity >= settings.getSimilarityFactor()) {
						Loader.INSTANCE.add(iterator);
					}
				}
			}
		}
	}
	public static EntityList applyTo(EntityList listBefore) {
		EntityList entityList = new EntityList();
		listBefore.forEach(entity -> {
			if (Loader.INSTANCE.contains(entity)) {
				entityList.add(entity);
			}
		});
		return entityList;
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
