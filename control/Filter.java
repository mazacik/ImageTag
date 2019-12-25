package control;

import baseobject.CustomList;
import baseobject.entity.Entity;
import baseobject.entity.EntityList;
import baseobject.tag.Tag;
import baseobject.tag.TagList;
import control.reload.ChangeIn;
import main.InstanceCollector;
import tools.FileUtil;

public class Filter extends EntityList implements InstanceCollector {
	private TagList infoListWhite;
	private TagList infoListBlack;
	
	private double whitelistFactor;
	private double blacklistFactor;
	
	private boolean showImages;
	private boolean showGifs;
	private boolean showVideos;
	private boolean showOnlyNewEntities;
	private boolean enableLimit;
	private int limit;
	
	public Filter() {
		infoListWhite = new TagList();
		infoListBlack = new TagList();
		
		whitelistFactor = 1.00;
		blacklistFactor = 0.01;
		
		showImages = true;
		showGifs = false;
		showVideos = false;
		showOnlyNewEntities = false;
		enableLimit = false;
		limit = 0;
	}
	
	public boolean add(Entity entity) {
		if (super.add(entity)) {
			reload.notify(ChangeIn.FILTER);
			return true;
		} else {
			return false;
		}
	}
	public void clear() {
		super.clear();
		reload.notify(ChangeIn.FILTER);
	}
	
	public void reset() {
		infoListWhite.clear();
		infoListBlack.clear();
		reload.notify(ChangeIn.FILTER);
	}
	public void refresh() {
		this.clear();
		
		for (Entity entity : mainEntityList) {
			switch (FileUtil.getType(entity)) {
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
			
			if (showOnlyNewEntities && !newEntities.contains(entity)) {
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
		
		for (Entity entity : new CustomList<>(select)) {
			if (!this.contains(entity)) {
				select.remove(entity);
			}
		}
		if (select.isEmpty()) {
			select.set(target.get());
		}
	}
	
	@SuppressWarnings("FieldCanBeLocal")
	private final double similarityFactor = 0.5;
	public void showSimilar(Entity entity) {
		infoListWhite.clear();
		infoListBlack.clear();
		super.clear();
		
		TagList query = entity.getTagList();
		for (Entity iterator : mainEntityList) {
			if (iterator.getTagList().size() != 0) {
				TagList sameTags = new TagList(query);
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
	
	private final EntityList newEntities = new EntityList();
	public EntityList getNewEntities() {
		return newEntities;
	}
	
	public EntityList applyTo(EntityList listBefore) {
		EntityList listAfter = new EntityList();
		listBefore.forEach(entity -> {
			if (this.contains(entity)) {
				listAfter.add(entity);
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
		for (String name : mainTagList.getNames(group)) {
			whitelist(mainTagList.getTag(group, name));
		}
	}
	public void blacklist(String group) {
		for (String name : mainTagList.getNames(group)) {
			blacklist(mainTagList.getTag(group, name));
		}
	}
	public void unlist(String group) {
		for (String name : mainTagList.getNames(group)) {
			unlist(mainTagList.getTag(group, name));
		}
	}
	
	private boolean isWhitelistOk(TagList tagList) {
		if (infoListWhite.isEmpty()) {
			return true;
		} else {
			TagList commonTags = new TagList(tagList);
			commonTags.retainAll(infoListWhite);
			double factor = (double) commonTags.size() / (double) infoListWhite.size();
			return factor >= whitelistFactor;
		}
	}
	private boolean isBlacklistOk(TagList tagList) {
		if (infoListBlack.isEmpty()) {
			return true;
		} else {
			TagList commonTags = new TagList(tagList);
			commonTags.retainAll(infoListBlack);
			double factor = (double) commonTags.size() / (double) infoListBlack.size();
			return factor <= blacklistFactor;
		}
	}
	
	public boolean isWhitelisted(String group) {
		boolean value = true;
		for (String name : mainTagList.getNames(group)) {
			if (!isWhitelisted(group, name)) {
				value = false;
				break;
			}
		}
		return value;
	}
	public boolean isBlacklisted(String group) {
		boolean value = true;
		for (String name : mainTagList.getNames(group)) {
			if (!isBlacklisted(group, name)) {
				value = false;
				break;
			}
		}
		return value;
	}
	
	public boolean isWhitelisted(Tag tag) {
		return infoListWhite.containsEqualTo(tag);
	}
	public boolean isWhitelisted(String group, String name) {
		return infoListWhite.containsEqualTo(mainTagList.getTag(group, name));
	}
	public boolean isBlacklisted(Tag tag) {
		return infoListBlack.containsEqualTo(tag);
	}
	public boolean isBlacklisted(String group, String name) {
		return infoListBlack.containsEqualTo(mainTagList.getTag(group, name));
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
	public boolean isShowOnlyNewEntities() {
		return showOnlyNewEntities;
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
	public void setShowOnlyNewEntities(boolean showOnlyNewEntities) {
		this.showOnlyNewEntities = showOnlyNewEntities;
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
