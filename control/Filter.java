package control;

import database.list.ObjectList;
import database.list.TagList;
import database.object.DataObject;
import database.object.TagObject;
import main.InstanceManager;

import java.util.ArrayList;

public class Filter extends ObjectList {
	private final TagList infoListWhite;
	private final TagList infoListBlack;
	
	private double whitelistFactor;
	private double blacklistFactor;
	
	private boolean showImages = true;
	private boolean showGifs = true;
	private boolean showVideos = false;
	private boolean sessionOnly = false;
	private boolean enableLimit = false;
	private int limit = 0;
	
	public Filter() {
		infoListWhite = new TagList();
		infoListBlack = new TagList();
		
		whitelistFactor = 1.00;
		blacklistFactor = 0.01;
	}
	
	public void reset() {
		InstanceManager.getFilter().getInfoListWhite().clear();
		InstanceManager.getFilter().getInfoListBlack().clear();
		refresh();
	}
	public void refresh() {
		InstanceManager.getFilter().clear();
		for (DataObject dataObject : InstanceManager.getObjectListMain()) {
			switch (dataObject.getFileType()) {
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
			
			if (sessionOnly && !currentSessionObjects.contains(dataObject)) continue;
			
			TagList tagList = dataObject.getTagList();
			if (enableLimit && tagList.size() > limit) continue;
			
			if (isWhitelistOk(tagList) && isBlacklistOk(tagList)) {
				this.add(dataObject);
			}
		}
	}
	public boolean refreshObject(DataObject dataObject) {
		switch (dataObject.getFileType()) {
			case IMAGE:
				if (!showImages) {
					this.remove(dataObject);
					return false;
				}
				break;
			case VIDEO:
				if (!showVideos) {
					this.remove(dataObject);
					return false;
				}
				break;
			case GIF:
				if (!showGifs) {
					this.remove(dataObject);
					return false;
				}
				break;
		}
		
		if (sessionOnly && !currentSessionObjects.contains(dataObject)) {
			this.remove(dataObject);
			return false;
		}
		
		TagList tagList = dataObject.getTagList();
		if (enableLimit && tagList.size() > limit) {
			this.remove(dataObject);
			return false;
		}
		
		if (isWhitelistOk(tagList) && isBlacklistOk(tagList)) {
			this.add(dataObject);
			return true;
		}
		
		return false;
	}
	
	@SuppressWarnings("FieldCanBeLocal")
	private final double similarityFactor = 0.5;
	public void showSimilar(DataObject dataObject) {
		InstanceManager.getFilter().getInfoListWhite().clear();
		InstanceManager.getFilter().getInfoListBlack().clear();
		InstanceManager.getFilter().clear();
		
		TagList query = dataObject.getTagList();
		
		for (DataObject iterator : InstanceManager.getObjectListMain()) {
			if (iterator.getTagList().size() != 0) {
				ArrayList<TagObject> sameTags = new ArrayList<>(query);
				sameTags.retainAll(iterator.getTagList());
				
				if (sameTags.size() != 0) {
					double similarity = (double) sameTags.size() / (double) query.size();
					if (similarity >= similarityFactor) {
						InstanceManager.getFilter().add(iterator);
					}
				}
			}
		}
	}
	
	private final ObjectList currentSessionObjects = new ObjectList();
	public ObjectList getCurrentSessionObjects() {
		return currentSessionObjects;
	}
	
	public void whitelist(TagObject tagObject) {
		if (!isWhitelisted(tagObject)) {
			infoListWhite.add(tagObject);
			infoListBlack.remove(tagObject);
		}
	}
	public void blacklist(TagObject tagObject) {
		if (!isBlacklisted(tagObject)) {
			infoListWhite.remove(tagObject);
			infoListBlack.add(tagObject);
		}
	}
	public void unlist(TagObject tagObject) {
		infoListWhite.remove(tagObject);
		infoListBlack.remove(tagObject);
	}
	
	public void whitelist(String group) {
		for (String name : InstanceManager.getTagListMain().getNames(group)) {
			whitelist(InstanceManager.getTagListMain().getTagObject(group, name));
		}
	}
	public void blacklist(String group) {
		for (String name : InstanceManager.getTagListMain().getNames(group)) {
			blacklist(InstanceManager.getTagListMain().getTagObject(group, name));
		}
	}
	public void unlist(String group) {
		for (String name : InstanceManager.getTagListMain().getNames(group)) {
			unlist(InstanceManager.getTagListMain().getTagObject(group, name));
		}
	}
	
	private boolean isWhitelistOk(TagList tagList) {
		if (infoListWhite.isEmpty()) {
			return true;
		} else {
			ArrayList<TagObject> commonTags = new ArrayList<>(tagList);
			commonTags.retainAll(infoListWhite);
			double factor = (double) commonTags.size() / (double) infoListWhite.size();
			return factor >= whitelistFactor;
		}
	}
	private boolean isBlacklistOk(TagList tagList) {
		if (infoListBlack.isEmpty()) {
			return true;
		} else {
			ArrayList<TagObject> commonTags = new ArrayList<>(tagList);
			commonTags.retainAll(infoListBlack);
			double factor = (double) commonTags.size() / (double) infoListBlack.size();
			return factor <= blacklistFactor;
		}
	}
	
	public boolean isWhitelisted(String group) {
		boolean value = true;
		for (String name : InstanceManager.getTagListMain().getNames(group)) {
			if (!isWhitelisted(group, name)) {
				value = false;
				break;
			}
		}
		return value;
	}
	public boolean isBlacklisted(String group) {
		boolean value = true;
		for (String name : InstanceManager.getTagListMain().getNames(group)) {
			if (!isBlacklisted(group, name)) {
				value = false;
				break;
			}
		}
		return value;
	}
	
	public boolean isWhitelisted(TagObject tagObject) {
		return infoListWhite.contains(tagObject);
	}
	public boolean isWhitelisted(String group, String name) {
		return infoListWhite.contains(InstanceManager.getTagListMain().getTagObject(group, name));
	}
	public boolean isBlacklisted(TagObject tagObject) {
		return infoListBlack.contains(tagObject);
	}
	public boolean isBlacklisted(String group, String name) {
		return infoListBlack.contains(InstanceManager.getTagListMain().getTagObject(group, name));
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
