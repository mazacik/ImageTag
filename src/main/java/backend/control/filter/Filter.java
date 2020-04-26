package backend.control.filter;

import backend.control.reload.Notifier;
import backend.control.reload.Reload;
import backend.list.BaseList;
import backend.list.entity.Entity;
import backend.list.entity.EntityList;
import backend.misc.EntityType;
import backend.misc.FileUtil;
import frontend.stage.options.FilterPreferences;
import main.Root;

import java.util.Collection;

public class Filter extends EntityList {
	private final FilterListManager filterListManager = new FilterListManager();
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
		filterListManager.clear();
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
		
		if (!FilterPreferences.QUERY.isEmpty()) {
			if (!entity.getName().toLowerCase().contains(FilterPreferences.QUERY.toLowerCase())) {
				return false;
			}
		}
		
		if (entity.getEntityType() == EntityType.IMG) {
			if (!FilterOption.ENABLE_IMG.getBooleanValue()) {
				return false;
			}
			String s = "ENABLE_IMG_" + FileUtil.getFileExtension(entity).toUpperCase();
			FilterOption fs = FilterOption.valueOf(s);
			if (!fs.getBooleanValue()) {
				return false;
			}
		} else if (entity.getEntityType() == EntityType.GIF) {
			if (!FilterOption.ENABLE_VID.getBooleanValue()) {
				return false;
			}
			String s = "ENABLE_VID_" + FileUtil.getFileExtension(entity).toUpperCase();
			FilterOption fs = FilterOption.valueOf(s);
			if (!fs.getBooleanValue()) {
				return false;
			}
			if (entity.getMediaDuration() < FilterOption.MEDIA_LENGTH_MIN.getIntValue() || entity.getMediaDuration() > FilterOption.MEDIA_LENGTH_MAX.getIntValue()) {
				return false;
			}
		} else if (entity.getEntityType() == EntityType.VID) {
			if (!FilterOption.ENABLE_VID.getBooleanValue()) {
				return false;
			}
			String s = "ENABLE_VID_" + FileUtil.getFileExtension(entity).toUpperCase();
			FilterOption fs = FilterOption.valueOf(s);
			if (!fs.getBooleanValue()) {
				return false;
			}
			if (entity.getMediaDuration() < FilterOption.MEDIA_LENGTH_MIN.getIntValue() || entity.getMediaDuration() > FilterOption.MEDIA_LENGTH_MAX.getIntValue()) {
				return false;
			}
		}
		
		if (FilterOption.LAST_IMPORT_ONLY.getBooleanValue() && !lastImport.contains(entity)) {
			return false;
		}
		
		int entityTagCount = entity.getTagIDList().size();
		if (entityTagCount < FilterOption.TAG_COUNT_MIN.getIntValue() || entityTagCount > FilterOption.TAG_COUNT_MAX.getIntValue()) {
			return false;
		}
		
		if (!entity.hasCollection()) {
			if (FilterOption.COLLECTION_SIZE_MIN.getIntValue() > 0) {
				return false;
			}
		} else {
			int entityCollectionSize = entity.getCollection().size();
			if (entityCollectionSize < FilterOption.COLLECTION_SIZE_MIN.getIntValue() || entityCollectionSize > FilterOption.COLLECTION_SIZE_MAX.getIntValue()) {
				return false;
			}
		}
		
		return filterListManager.applyLists(entity);
	}
	
	public void showSimilar(Entity entity) {
		filterListManager.clear();
		filterListManager.clear();
		this.clear();
		
		BaseList<Integer> query = entity.getTagIDList();
		for (Entity iterator : Root.ENTITYLIST) {
			if (iterator.getTagIDList().size() != 0) {
				BaseList<Integer> sameTags = new BaseList<>(query);
				sameTags.retainAll(iterator.getTagIDList());
				
				if (!sameTags.isEmpty()) {
					double similarity = (double) sameTags.size() / (double) query.size();
					if (similarity >= FilterOption.SIMILARITY_FACTOR.getIntValue()) {
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
	
	public FilterListManager getFilterListManager() {
		return filterListManager;
	}
	public EntityList getLastImport() {
		return lastImport;
	}
}
