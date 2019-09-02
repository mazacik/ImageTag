package application.controller;

import application.database.list.CustomList;
import application.database.list.DataObjectList;
import application.database.list.TagList;
import application.database.object.DataObject;
import application.database.object.TagObject;
import application.main.Instances;

import java.util.ArrayList;

public class Filter extends DataObjectList {
	private final TagList infoListWhite;
	private final TagList infoListBlack;
	
	private double whitelistFactor;
	private double blacklistFactor;
	
	private boolean showImages = true;
	private boolean showGifs = false;
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
	
	public boolean add(DataObject dataObject) {
		if (super.add(dataObject)) {
			Instances.getReload().notify(Reload.Control.FILTER);
			return true;
		} else {
			return false;
		}
	}
	public void setAll(DataObjectList dataObjects) {
		if (super.setAll(dataObjects)) {
			Instances.getReload().notify(Reload.Control.FILTER);
		}
	}
	public void clear() {
		super.clear();
		Instances.getReload().notify(Reload.Control.FILTER);
	}
	
	public void reset() {
		Instances.getFilter().getInfoListWhite().clear();
		Instances.getFilter().getInfoListBlack().clear();
		refresh();
	}
	public void refresh() {
		Instances.getFilter().clear();
		
		for (DataObject dataObject : Instances.getObjectListMain()) {
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
		
		if (!this.contains(Instances.getTarget().getCurrentTarget())) Instances.getTarget().set(this.getFirst());
		
		CustomList<DataObject> selectHelper = new CustomList<>();
		for (DataObject dataObject : Instances.getSelect()) {
			if (!this.contains(dataObject)) selectHelper.add(dataObject);
		}
		Instances.getSelect().removeAll(selectHelper);
		if (Instances.getSelect().isEmpty()) Instances.getSelect().set(Instances.getTarget().getCurrentTarget());
		
		//Instances.getGalleryPane().adjustViewportToCurrentTarget();
	}
	
	@SuppressWarnings("FieldCanBeLocal")
	private final double similarityFactor = 0.5;
	public void showSimilar(DataObject dataObject) {
		Instances.getFilter().getInfoListWhite().clear();
		Instances.getFilter().getInfoListBlack().clear();
		Instances.getFilter().clear();
		
		TagList query = dataObject.getTagList();
		for (DataObject iterator : Instances.getObjectListMain()) {
			if (iterator.getTagList().size() != 0) {
				ArrayList<TagObject> sameTags = new ArrayList<>(query);
				sameTags.retainAll(iterator.getTagList());
				
				if (!sameTags.isEmpty()) {
					double similarity = (double) sameTags.size() / (double) query.size();
					if (similarity >= similarityFactor) {
						Instances.getFilter().add(iterator);
					}
				}
			}
		}
	}
	
	private final DataObjectList currentSessionObjects = new DataObjectList();
	public DataObjectList getCurrentSessionObjects() {
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
		for (String name : Instances.getTagListMain().getNames(group)) {
			whitelist(Instances.getTagListMain().getTagObject(group, name));
		}
	}
	public void blacklist(String group) {
		for (String name : Instances.getTagListMain().getNames(group)) {
			blacklist(Instances.getTagListMain().getTagObject(group, name));
		}
	}
	public void unlist(String group) {
		for (String name : Instances.getTagListMain().getNames(group)) {
			unlist(Instances.getTagListMain().getTagObject(group, name));
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
		for (String name : Instances.getTagListMain().getNames(group)) {
			if (!isWhitelisted(group, name)) {
				value = false;
				break;
			}
		}
		return value;
	}
	public boolean isBlacklisted(String group) {
		boolean value = true;
		for (String name : Instances.getTagListMain().getNames(group)) {
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
		return infoListWhite.contains(Instances.getTagListMain().getTagObject(group, name));
	}
	public boolean isBlacklisted(TagObject tagObject) {
		return infoListBlack.contains(tagObject);
	}
	public boolean isBlacklisted(String group, String name) {
		return infoListBlack.contains(Instances.getTagListMain().getTagObject(group, name));
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
