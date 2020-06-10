package backend.control.filter;

import backend.control.reload.Notifier;
import backend.control.reload.Reload;
import backend.list.BaseList;
import backend.list.entity.Entity;
import backend.list.entity.EntityList;
import backend.misc.EntityType;
import backend.misc.FileUtil;
import frontend.stage.settings.FilterSettingsPane;
import main.Main;

import java.util.Collection;

public class Filter extends EntityList {
	private final FilterListManager filterListManager = new FilterListManager();
	private final EntityList lastImport = new EntityList();
	
	private final EntityList representingList = new EntityList();
	public Entity getRepresentingRandom() {
		Entity entity = representingList.getRandom();
		if (entity != null) {
			if (entity.hasGroup()) {
				return Main.FILTER.getFilteredList(entity.getGroup()).getRandom();
			} else {
				return entity;
			}
		}
		return null;
	}
	
	public Filter() {
	
	}
	
	public boolean add(Entity entity) {
		if (super.add(entity)) {
			Reload.notify(Notifier.FILTER_CHANGED);
			return true;
		} else {
			return false;
		}
	}
	public boolean addAll(Collection<? extends Entity> c) {
		if (super.addAll(c)) {
			Reload.notify(Notifier.FILTER_CHANGED);
			return true;
		} else {
			return false;
		}
	}
	
	public boolean remove(Entity entity) {
		if (super.remove(entity)) {
			Reload.notify(Notifier.FILTER_CHANGED);
			return true;
		} else {
			return false;
		}
	}
	public boolean removeAll(Collection<?> c) {
		if (super.removeAll(c)) {
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
	
	public void refresh() {
		Main.SELECT.storePosition();
		
		this.clear();
		for (Entity entity : Main.DB_ENTITY) {
			if (this.isValid(entity)) {
				this.add(entity);
			}
		}
		
		representingList.setAll(this.createRepresentingList());
		
		Main.SELECT.restorePosition();
		Reload.notify(Notifier.FILTER_CHANGED);
	}
	
	public void resolve() {
		if (!Reload.getNeedsFilterCheck().isEmpty()) {
			Reload.getNeedsFilterCheck().forEach(this::resolve);
			Reload.getNeedsFilterCheck().clear();
			
			representingList.setAll(this.createRepresentingList());
		}
	}
	public void resolve(Entity entity) {
		if (Main.DB_ENTITY.contains(entity)) {
			boolean contains = this.contains(entity);
			boolean valid = this.isValid(entity);
			
			if (contains && !valid) {
				this.remove(entity);
			} else if (!contains && valid) {
				this.add(entity);
			}
		}
	}
	public boolean isValid(Entity entity) {
		if (entity == null || !Main.DB_ENTITY.contains(entity)) return false;
		
		if (!FilterSettingsPane.QUERY.isEmpty()) {
			if (!entity.getName().toLowerCase().contains(FilterSettingsPane.QUERY.toLowerCase())) {
				return false;
			}
		}
		
		if (entity.getType() == EntityType.IMG) {
			if (!FilterOption.ENABLE_IMG.getBooleanValue()) {
				return false;
			}
			String s = "ENABLE_IMG_" + FileUtil.getFileExtension(entity).toUpperCase();
			FilterOption fs = FilterOption.valueOf(s);
			if (!fs.getBooleanValue()) {
				return false;
			}
		} else if (entity.getType() == EntityType.GIF) {
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
		} else if (entity.getType() == EntityType.VID) {
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
		
		if (FilterOption.ONLY_LAST_IMPORT.getBooleanValue() && !lastImport.contains(entity)) {
			return false;
		}
		
		if (FilterOption.ONLY_FAVORITES.getBooleanValue() && !entity.isFavorite()) {
			return false;
		}
		
		int entityTagCount = entity.getTagIDList().size();
		if (entityTagCount < FilterOption.TAG_COUNT_MIN.getIntValue() || entityTagCount > FilterOption.TAG_COUNT_MAX.getIntValue()) {
			return false;
		}
		
		if (!entity.hasGroup()) {
			if (FilterOption.GROUP_SIZE_MIN.getIntValue() > 0) {
				return false;
			}
		} else {
			int entityGroupSize = entity.getGroup().size();
			if (entityGroupSize < FilterOption.GROUP_SIZE_MIN.getIntValue() || entityGroupSize > FilterOption.GROUP_SIZE_MAX.getIntValue()) {
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
		for (Entity iterator : Main.DB_ENTITY) {
			if (iterator.getTagIDList().size() != 0) {
				BaseList<Integer> sameTags = new BaseList<>(query);
				sameTags.retainAll(iterator.getTagIDList());
				
				if (!sameTags.isEmpty()) {
					double similarity = (double) sameTags.size() / (double) query.size();
					if (similarity >= FilterOption.SIMILARITY_FACTOR.getIntValue()) {
						this.add(iterator);
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
	
	public EntityList getRepresentingList() {
		return representingList;
	}
}
