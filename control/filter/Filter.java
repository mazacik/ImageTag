package control.filter;

import base.CustomList;
import base.entity.Entity;
import base.entity.EntityList;
import base.tag.TagList;
import control.Select;
import control.Target;
import control.reload.ChangeIn;
import control.reload.Reload;
import misc.FileUtil;

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
		
		for (Entity entity : EntityList.getMain()) {
			switch (FileUtil.getType(entity)) {
				case IMAGE:
					if (!settings.isShowImages()) continue;
					break;
				case VIDEO:
					if (!settings.isShowVideos()) continue;
					break;
				case GIF:
					if (!settings.isShowGifs()) continue;
					break;
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
		
		if (!Loader.INSTANCE.contains(Target.get())) {
			Target.set(Loader.INSTANCE.getFirst());
		}
		
		for (Entity entity : new CustomList<>(Select.getEntities())) {
			if (!Loader.INSTANCE.contains(entity)) {
				Select.getEntities().remove(entity);
			}
		}
		if (Select.getEntities().isEmpty()) {
			Select.getEntities().set(Target.get());
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
		EntityList listAfter = new EntityList();
		listBefore.forEach(entity -> {
			if (Loader.INSTANCE.contains(entity)) {
				listAfter.add(entity);
			}
		});
		return listAfter;
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
