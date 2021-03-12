package backend.filter;

import backend.BaseList;
import backend.entity.Entity;
import backend.entity.EntityList;
import backend.misc.FileExtension;
import backend.misc.FileUtil;
import backend.reload.Notifier;
import backend.reload.Reload;
import backend.settings.Settings;
import frontend.stage.settings.FilterSettingsPane;
import main.Main;

import java.util.Collection;

public class Filter extends EntityList {
	private final FilterListManager filterListManager = new FilterListManager();
	private final EntityList lastImport = new EntityList();
	
	public Filter() {
		super();
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
	
	@SuppressWarnings("unused") public void refresh() {
		Main.SELECT.storePosition();
		
		this.clear();
		for (Entity entity : Main.ENTITYLIST) {
			if (this.isValid(entity)) {
				this.add(entity);
			}
		}
		
		Main.SELECT.restorePosition();
		Reload.notify(Notifier.FILTER_CHANGED);
	}
	
	public void resolve() {
		if (!Reload.getNeedsFilterCheck().isEmpty()) {
			Reload.getNeedsFilterCheck().forEach(this::resolve);
			Reload.getNeedsFilterCheck().clear();
		}
	}
	public void resolve(Entity entity) {
		if (Main.ENTITYLIST.contains(entity)) {
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
		if (entity == null || !Main.ENTITYLIST.contains(entity)) return false;
		
		if (!FilterSettingsPane.QUERY.isEmpty()) {
			if (!entity.getName().toLowerCase().contains(FilterSettingsPane.QUERY.toLowerCase())) {
				return false;
			}
		}
		
		if (!isMediaTypeValid(entity)) {
			return false;
		}
		
		if (!isFileExtensionValid(FileUtil.getFileExtension(entity))) {
			return false;
		}
		
		if (Settings.ONLY_LAST_IMPORT.getBooleanValue() && !lastImport.contains(entity)) {
			return false;
		}
		
		if (entity.getLikes() < Settings.MIN_LIKES.getIntValue() || entity.getLikes() > Settings.MAX_LIKES.getIntValue()) {
			return false;
		}
		
		int entityTagCount = entity.getTagList().size();
		if (entityTagCount < Settings.MIN_TAG_COUNT.getIntValue() || entityTagCount > Settings.MAX_TAG_COUNT.getIntValue()) {
			return false;
		}
		
		if (!entity.hasGroup()) {
			if (Settings.MIN_GROUP_SIZE.getIntValue() > 0) {
				return false;
			}
		} else {
			int entityGroupSize = entity.getGroup().size();
			if (entityGroupSize < Settings.MIN_GROUP_SIZE.getIntValue() || entityGroupSize > Settings.MAX_GROUP_SIZE.getIntValue()) {
				return false;
			}
		}
		
		return filterListManager.checkLists(entity);
	}
	
	private boolean isMediaTypeValid(Entity entity) {
		switch (entity.getMediaType()) {
			case IMG:
				return Settings.ENABLE_IMG.getBooleanValue();
			case GIF:
			case VID:
				return Settings.ENABLE_VID.getBooleanValue() &&
				       entity.getMediaDuration() >= Settings.MIN_MEDIA_LENGTH.getIntValue() &&
				       entity.getMediaDuration() <= Settings.MAX_MEDIA_LENGTH.getIntValue();
			default:
				return false;
		}
	}
	private boolean isFileExtensionValid(FileExtension extension) {
		switch (extension) {
			case JPG:
				return Settings.ENABLE_IMG_JPG.getBooleanValue();
			case JPEG:
				return Settings.ENABLE_IMG_JPEG.getBooleanValue();
			case PNG:
				return Settings.ENABLE_IMG_PNG.getBooleanValue();
			case GIF:
				return Settings.ENABLE_VID_GIF.getBooleanValue();
			case MP4:
				return Settings.ENABLE_VID_MP4.getBooleanValue();
			case M4V:
				return Settings.ENABLE_VID_M4V.getBooleanValue();
			case MOV:
				return Settings.ENABLE_VID_MOV.getBooleanValue();
			case WMV:
				return Settings.ENABLE_VID_WMV.getBooleanValue();
			case AVI:
				return Settings.ENABLE_VID_AVI.getBooleanValue();
			case WEBM:
				return Settings.ENABLE_VID_WEBM.getBooleanValue();
			default:
				return false;
		}
	}
	
	public Entity getRepresentingRandom() {
		Entity entity = createRepresentingList().getRandom();
		if (entity != null) {
			if (entity.hasGroup()) {
				return Main.FILTER.getFilteredList(entity.getGroup()).getRandom();
			} else {
				return entity;
			}
		}
		return null;
	}
	
	public void showSimilar(Entity entity) {
		filterListManager.clear();
		filterListManager.clear();
		this.clear();
		
		BaseList<String> query = entity.getTagList();
		for (Entity iterator : Main.ENTITYLIST) {
			if (iterator.getTagList().size() != 0) {
				BaseList<String> sameTags = new BaseList<>(query);
				sameTags.retainAll(iterator.getTagList());
				
				if (!sameTags.isEmpty()) {
					double similarity = (double) sameTags.size() / (double) query.size();
					if (similarity >= Settings.SIMILARITY_FACTOR.getIntValue()) {
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
}
