package control.filter;

import base.CustomList;
import base.entity.Entity;
import base.entity.EntityList;
import control.reload.Notifier;
import control.reload.Reload;
import enums.MediaType;
import main.Root;

import java.util.Collection;

public class Filter extends EntityList {
	private final FilterSettings settings = new FilterSettings();
	private final FilterListManager listManager = new FilterListManager();
	private final EntityList lastImport = new EntityList();
	
	public Filter() {
	
	}
	
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
	
	public void reset() {
		listManager.clear();
		Reload.notify(Notifier.FILTER_CHANGED);
	}
	public void refresh() {
		Root.SELECT.storePosition();
		
		this.clear();
		for (Entity entity : Root.ENTITYLIST) {
			if (this.isValid(entity)) {
				this.addImpl(entity);
			}
		}
		
		Root.SELECT.restorePosition();
		Reload.notify(Notifier.FILTER_CHANGED);
	}
	
	public void resolve() {
		Reload.getNeedsFilterCheck().forEach(this::resolve);
		Reload.getNeedsFilterCheck().clear();
	}
	public void resolve(Entity entity) {
		if (Root.ENTITYLIST.contains(entity)) {
			boolean contains = this.contains(entity);
			boolean valid = this.isValid(entity);
			
			if (contains && !valid) {
				this.remove(entity);
			} else if (!contains && valid) {
				this.addImpl(entity);
			}
		}
	}
	public boolean isValid(Entity entity) {
		if (entity == null || !Root.ENTITYLIST.contains(entity)) return false;
		
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
		
		if (settings.isOnlySession() && !lastImport.contains(entity)) {
			return false;
		}
		
		if (settings.isEnableLimit() && entity.getTagIDs().size() > settings.getLimit()) {
			return false;
		}
		
		return listManager.applyLists(entity);
	}
	
	public void showSimilar(Entity entity) {
		listManager.clear();
		listManager.clear();
		this.clear();
		
		CustomList<Integer> query = entity.getTagIDs();
		for (Entity iterator : Root.ENTITYLIST) {
			if (iterator.getTagIDs().size() != 0) {
				CustomList<Integer> sameTags = new CustomList<>(query);
				sameTags.retainAll(iterator.getTagIDs());
				
				if (!sameTags.isEmpty()) {
					double similarity = (double) sameTags.size() / (double) query.size();
					if (similarity >= settings.getSimilarityFactor()) {
						this.addImpl(iterator);
					}
				}
			}
		}
	}
	public EntityList getFilteredList(EntityList listOld) {
		EntityList returnValue = new EntityList(listOld);
		returnValue.retainAll(this);
		return returnValue;
	}
	
	public FilterSettings getSettings() {
		return settings;
	}
	public FilterListManager getListManager() {
		return listManager;
	}
	public EntityList getLastImport() {
		return lastImport;
	}
}
