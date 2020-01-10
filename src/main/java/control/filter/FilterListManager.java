package control.filter;

import base.CustomList;
import base.entity.Entity;
import base.tag.TagList;
import control.reload.Notifier;
import control.reload.Reload;

public class FilterListManager {
	private final CustomList<Integer> whitelist = new CustomList<>();
	private final CustomList<Integer> blacklist = new CustomList<>();
	
	FilterListManager() {
	
	}
	
	public void whitelist(Integer tagID) {
		if (!isWhitelisted(tagID)) {
			whitelist.add(tagID);
			blacklist.remove(tagID);
			Reload.notify(Notifier.FILTER_NEEDS_REFRESH);
		}
	}
	public void blacklist(Integer tagID) {
		if (!isBlacklisted(tagID)) {
			whitelist.remove(tagID);
			blacklist.add(tagID);
			Reload.notify(Notifier.FILTER_NEEDS_REFRESH);
		}
	}
	public void unlist(Integer tagID) {
		whitelist.remove(tagID);
		blacklist.remove(tagID);
		Reload.notify(Notifier.FILTER_NEEDS_REFRESH);
	}
	
	public void whitelist(String group) {
		for (String name : TagList.getMain().getNames(group)) {
			whitelist(TagList.getMain().getTag(group, name).getID());
		}
	}
	public void blacklist(String group) {
		for (String name : TagList.getMain().getNames(group)) {
			blacklist(TagList.getMain().getTag(group, name).getID());
		}
	}
	public void unlist(String group) {
		for (String name : TagList.getMain().getNames(group)) {
			unlist(TagList.getMain().getTag(group, name).getID());
		}
	}
	
	boolean isWhitelistOk(Entity entity) {
		return isWhitelistOk(entity.getTagIDs());
	}
	boolean isWhitelistOk(CustomList<Integer> tagIDs) {
		if (whitelist.isEmpty()) {
			return true;
		} else {
			CustomList<Integer> commonTags = new CustomList<>(tagIDs);
			commonTags.retainAll(whitelist);
			double factor = (double) commonTags.size() / (double) whitelist.size();
			return factor >= Filter.getSettings().getWhitelistFactor();
		}
	}
	
	boolean isBlacklistOk(Entity entity) {
		return isBlacklistOk(entity.getTagIDs());
	}
	boolean isBlacklistOk(CustomList<Integer> tagIDs) {
		if (blacklist.isEmpty()) {
			return true;
		} else {
			CustomList<Integer> commonTags = new CustomList<>(tagIDs);
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
	public boolean isWhitelisted(Integer tagID) {
		return whitelist.contains(tagID);
	}
	public boolean isWhitelisted(String group, String name) {
		return isWhitelisted(TagList.getMain().getTag(group, name).getID());
	}
	
	public boolean isBlacklisted(String group) {
		for (String name : TagList.getMain().getNames(group)) {
			if (!isBlacklisted(group, name)) {
				return false;
			}
		}
		return true;
	}
	public boolean isBlacklisted(Integer tagID) {
		return blacklist.contains(tagID);
	}
	public boolean isBlacklisted(String group, String name) {
		return isBlacklisted(TagList.getMain().getTag(group, name).getID());
	}
	
	public boolean isUnlisted(String group) {
		for (String name : TagList.getMain().getNames(group)) {
			if (!isUnlisted(group, name)) {
				return false;
			}
		}
		return true;
	}
	public boolean isUnlisted(Integer tagID) {
		return !isWhitelisted(tagID) && !isBlacklisted(tagID);
	}
	public boolean isUnlisted(String group, String name) {
		return !isWhitelisted(group, name) && !isBlacklisted(group, name);
	}
	
	CustomList<Integer> getWhitelist() {
		return whitelist;
	}
	CustomList<Integer> getBlacklist() {
		return blacklist;
	}
}
