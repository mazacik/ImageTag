package control.filter;

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
		for (String name : TagList.getMainInstance().getNames(group)) {
			whitelist(TagList.getMainInstance().getTag(group, name));
		}
	}
	public void blacklist(String group) {
		for (String name : TagList.getMainInstance().getNames(group)) {
			blacklist(TagList.getMainInstance().getTag(group, name));
		}
	}
	public void unlist(String group) {
		for (String name : TagList.getMainInstance().getNames(group)) {
			unlist(TagList.getMainInstance().getTag(group, name));
		}
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
		boolean value = true;
		for (String name : TagList.getMainInstance().getNames(group)) {
			if (!isWhitelisted(group, name)) {
				value = false;
				break;
			}
		}
		return value;
	}
	public boolean isBlacklisted(String group) {
		boolean value = true;
		for (String name : TagList.getMainInstance().getNames(group)) {
			if (!isBlacklisted(group, name)) {
				value = false;
				break;
			}
		}
		return value;
	}
	
	public boolean isWhitelisted(Tag tag) {
		return whitelist.containsEqualTo(tag);
	}
	public boolean isWhitelisted(String group, String name) {
		return whitelist.containsEqualTo(TagList.getMainInstance().getTag(group, name));
	}
	public boolean isBlacklisted(Tag tag) {
		return blacklist.containsEqualTo(tag);
	}
	public boolean isBlacklisted(String group, String name) {
		return blacklist.containsEqualTo(TagList.getMainInstance().getTag(group, name));
	}
	
	TagList getWhitelist() {
		return whitelist;
	}
	TagList getBlacklist() {
		return blacklist;
	}
}
