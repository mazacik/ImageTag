package control.filter;

import base.entity.Entity;
import base.tag.Tag;
import base.tag.TagList;

public class FilterListManager {
	private final TagList whitelist = new TagList();
	private final TagList blacklist = new TagList();
	
	FilterListManager() {
	
	}
	
	public void whitelist(Tag tag) {
		if (!isWhitelisted(tag)) {
			whitelist.add(tag);
			blacklist.remove(tag);
		}
	}
	public void blacklist(Tag tag) {
		if (!isBlacklisted(tag)) {
			whitelist.remove(tag);
			blacklist.add(tag);
		}
	}
	public void unlist(Tag tag) {
		whitelist.remove(tag);
		blacklist.remove(tag);
	}
	
	public void whitelist(String group) {
		for (String name : TagList.getMain().getNames(group)) {
			whitelist(TagList.getMain().getTag(group, name));
		}
	}
	public void blacklist(String group) {
		for (String name : TagList.getMain().getNames(group)) {
			blacklist(TagList.getMain().getTag(group, name));
		}
	}
	public void unlist(String group) {
		for (String name : TagList.getMain().getNames(group)) {
			unlist(TagList.getMain().getTag(group, name));
		}
	}
	
	boolean isWhitelistOk(Entity entity) {
		return isWhitelistOk(entity.getTagList());
	}
	boolean isWhitelistOk(TagList tagList) {
		if (whitelist.isEmpty()) {
			return true;
		} else {
			TagList commonTags = new TagList(tagList);
			commonTags.retainAll(whitelist);
			double factor = (double) commonTags.size() / (double) whitelist.size();
			return factor >= Filter.getSettings().getWhitelistFactor();
		}
	}
	
	boolean isBlacklistOk(Entity entity) {
		return isBlacklistOk(entity.getTagList());
	}
	boolean isBlacklistOk(TagList tagList) {
		if (blacklist.isEmpty()) {
			return true;
		} else {
			TagList commonTags = new TagList(tagList);
			commonTags.retainAll(blacklist);
			double factor = (double) commonTags.size() / (double) blacklist.size();
			return factor <= Filter.getSettings().getBlacklistFactor();
		}
	}
	
	public boolean isWhitelisted(String group) {
		for (String name : TagList.getMain().getNames(group)) {
			if (!isWhitelisted(group, name)) {
				return false;
			}
		}
		return true;
	}
	public boolean isWhitelisted(Tag tag) {
		return whitelist.contains(tag);
	}
	public boolean isWhitelisted(String group, String name) {
		return isWhitelisted(TagList.getMain().getTag(group, name));
	}
	
	public boolean isBlacklisted(String group) {
		for (String name : TagList.getMain().getNames(group)) {
			if (!isBlacklisted(group, name)) {
				return false;
			}
		}
		return true;
	}
	public boolean isBlacklisted(Tag tag) {
		return blacklist.contains(tag);
	}
	public boolean isBlacklisted(String group, String name) {
		return isBlacklisted(TagList.getMain().getTag(group, name));
	}
	
	public boolean isUnlisted(String group) {
		for (String name : TagList.getMain().getNames(group)) {
			if (!isUnlisted(group, name)) {
				return false;
			}
		}
		return true;
	}
	public boolean isUnlisted(Tag tag) {
		return !isWhitelisted(tag) && !isBlacklisted(tag);
	}
	public boolean isUnlisted(String group, String name) {
		return !isWhitelisted(group, name) && !isBlacklisted(group, name);
	}
	
	TagList getWhitelist() {
		return whitelist;
	}
	TagList getBlacklist() {
		return blacklist;
	}
}
