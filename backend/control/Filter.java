package application.backend.control;

import application.backend.base.CustomList;
import application.backend.base.entity.Entity;
import application.backend.base.entity.EntityList;
import application.backend.base.tag.Tag;
import application.backend.base.tag.TagList;
import application.backend.util.FileUtil;
import application.main.InstanceCollector;

import java.util.ArrayList;

public class Filter extends EntityList implements InstanceCollector {
	private TagList infoListWhite;
	private TagList infoListBlack;
	
	private double whitelistFactor;
	private double blacklistFactor;
	
	private boolean showImages;
	private boolean showGifs;
	private boolean showVideos;
	private boolean sessionOnly;
	private boolean enableLimit;
	private int limit;
	
	public Filter() {
	
	}
	
	public void init() {
		infoListWhite = new TagList();
		infoListBlack = new TagList();
		
		whitelistFactor = 1.00;
		blacklistFactor = 0.01;
		
		showImages = true;
		showGifs = false;
		showVideos = false;
		sessionOnly = false;
		enableLimit = false;
		limit = 0;
	}
	
	public boolean add(Entity entity) {
		if (super.add(entity)) {
			reload.notify(Reload.Control.FILTER);
			return true;
		} else {
			return false;
		}
	}
	public void setAll(EntityList dataObjects) {
		if (super.setAll(dataObjects)) {
			reload.notify(Reload.Control.FILTER);
		}
	}
	public void clear() {
		super.clear();
		reload.notify(Reload.Control.FILTER);
	}
	
	public void reset() {
		filter.getInfoListWhite().clear();
		filter.getInfoListBlack().clear();
		refresh();
	}
	public void refresh() {
		filter.clear();
		
		for (Entity entity : entityListMain) {
			switch (FileUtil.getFileType(entity)) {
				case IMAGE:
					if (!showImages) continue;
					break;
				case VIDEO:
					if (!showVideos) continue;
					break;
				case GIF:
					if (!showGifs) continue;
					break;
			}
			
			if (sessionOnly && !currentSessionObjects.contains(entity)) {
				continue;
			}
			
			TagList tagList = entity.getTagList();
			if (enableLimit && tagList.size() > limit) {
				continue;
			}
			
			if (isWhitelistOk(tagList) && isBlacklistOk(tagList)) {
				this.add(entity);
			}
		}
		
		if (!this.contains(target.get())) {
			target.set(this.getFirst());
		}
		
		CustomList<Entity> selectHelper = new CustomList<>();
		for (Entity entity : select) {
			if (!this.contains(entity)) selectHelper.add(entity);
		}
		select.removeAll(selectHelper);
		if (select.isEmpty()) {
			select.set(target.get());
		}
	}
	
	@SuppressWarnings("FieldCanBeLocal")
	private final double similarityFactor = 0.5;
	public void showSimilar(Entity entity) {
		filter.getInfoListWhite().clear();
		filter.getInfoListBlack().clear();
		filter.clear();
		
		TagList query = entity.getTagList();
		for (Entity iterator : entityListMain) {
			if (iterator.getTagList().size() != 0) {
				ArrayList<Tag> sameTags = new ArrayList<>(query);
				sameTags.retainAll(iterator.getTagList());
				
				if (!sameTags.isEmpty()) {
					double similarity = (double) sameTags.size() / (double) query.size();
					if (similarity >= similarityFactor) {
						filter.add(iterator);
					}
				}
			}
		}
	}
	
	private final EntityList currentSessionObjects = new EntityList();
	public EntityList getCurrentSessionObjects() {
		return currentSessionObjects;
	}
	
	public EntityList checkList(EntityList listBefore) {
		EntityList listAfter = new EntityList();
		listBefore.forEach(dataObject -> {
			if (this.contains(dataObject)) {
				listAfter.add(dataObject);
			}
		});
		return listAfter;
	}
	
	public void whitelist(Tag tag) {
		if (!isWhitelisted(tag)) {
			infoListWhite.add(tag);
			infoListBlack.remove(tag);
		}
	}
	public void blacklist(Tag tag) {
		if (!isBlacklisted(tag)) {
			infoListWhite.remove(tag);
			infoListBlack.add(tag);
		}
	}
	public void unlist(Tag tag) {
		infoListWhite.remove(tag);
		infoListBlack.remove(tag);
	}
	
	public void whitelist(String group) {
		for (String name : tagListMain.getNames(group)) {
			whitelist(tagListMain.getTag(group, name));
		}
	}
	public void blacklist(String group) {
		for (String name : tagListMain.getNames(group)) {
			blacklist(tagListMain.getTag(group, name));
		}
	}
	public void unlist(String group) {
		for (String name : tagListMain.getNames(group)) {
			unlist(tagListMain.getTag(group, name));
		}
	}
	
	private boolean isWhitelistOk(TagList tagList) {
		if (infoListWhite.isEmpty()) {
			return true;
		} else {
			ArrayList<Tag> commonTags = new ArrayList<>(tagList);
			commonTags.retainAll(infoListWhite);
			double factor = (double) commonTags.size() / (double) infoListWhite.size();
			return factor >= whitelistFactor;
		}
	}
	private boolean isBlacklistOk(TagList tagList) {
		if (infoListBlack.isEmpty()) {
			return true;
		} else {
			ArrayList<Tag> commonTags = new ArrayList<>(tagList);
			commonTags.retainAll(infoListBlack);
			double factor = (double) commonTags.size() / (double) infoListBlack.size();
			return factor <= blacklistFactor;
		}
	}
	
	public boolean isWhitelisted(String group) {
		boolean value = true;
		for (String name : tagListMain.getNames(group)) {
			if (!isWhitelisted(group, name)) {
				value = false;
				break;
			}
		}
		return value;
	}
	public boolean isBlacklisted(String group) {
		boolean value = true;
		for (String name : tagListMain.getNames(group)) {
			if (!isBlacklisted(group, name)) {
				value = false;
				break;
			}
		}
		return value;
	}
	
	public boolean isWhitelisted(Tag tag) {
		return infoListWhite.containsEqual(tag);
	}
	public boolean isWhitelisted(String group, String name) {
		return infoListWhite.containsEqual(tagListMain.getTag(group, name));
	}
	public boolean isBlacklisted(Tag tag) {
		return infoListBlack.containsEqual(tag);
	}
	public boolean isBlacklisted(String group, String name) {
		return infoListBlack.containsEqual(tagListMain.getTag(group, name));
	}
	
	public TagList getInfoListWhite() {
		return infoListWhite;
	}
	public TagList getInfoListBlack() {
		return infoListBlack;
	}
	
	public boolean isShowImages() {
		return showImages;
	}
	public boolean isShowGifs() {
		return showGifs;
	}
	public boolean isShowVideos() {
		return showVideos;
	}
	public boolean isSessionOnly() {
		return sessionOnly;
	}
	public boolean isEnableLimit() {
		return enableLimit;
	}
	public int getLimit() {
		return limit;
	}
	
	public void setShowImages(boolean showImages) {
		this.showImages = showImages;
	}
	public void setShowGifs(boolean showGifs) {
		this.showGifs = showGifs;
	}
	public void setShowVideos(boolean showVideos) {
		this.showVideos = showVideos;
	}
	public void setSessionOnly(boolean sessionOnly) {
		this.sessionOnly = sessionOnly;
	}
	public void setEnableLimit(boolean enableLimit) {
		this.enableLimit = enableLimit;
	}
	public void setLimit(int limit) {
		this.limit = limit;
	}
	
	public double getWhitelistFactor() {
		return whitelistFactor;
	}
	public void setWhitelistFactor(double whitelistFactor) {
		this.whitelistFactor = whitelistFactor;
	}
	public double getBlacklistFactor() {
		return blacklistFactor;
	}
	public void setBlacklistFactor(double blacklistFactor) {
		this.blacklistFactor = blacklistFactor;
	}
}
