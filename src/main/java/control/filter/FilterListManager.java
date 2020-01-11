package control.filter;

import base.CustomList;
import base.entity.Entity;
import base.tag.Tag;
import control.reload.Notifier;
import control.reload.Reload;

public class FilterListManager {
	private final CustomList<String> whitelist = new CustomList<>();
	private final CustomList<String> blacklist = new CustomList<>();
	
	protected FilterListManager() {
	
	}
	
	boolean applyLists(Entity entity) {
		int matchLength = 0;
		boolean ok = whitelist.isEmpty();
		
		for (Tag tag : entity.getTagList()) {
			for (String string : whitelist) {
				if (tag.getStringValue().contains(string)) {
					if (string.length() > matchLength) {
						matchLength = string.length();
						ok = true;
					}
				}
			}
			for (String string : blacklist) {
				if (tag.getStringValue().contains(string)) {
					if (string.length() > matchLength) {
						matchLength = string.length();
						ok = false;
					}
				}
			}
		}
		
		return ok;
	}
	
	public void whitelist(Tag tag) {
		whitelist(tag.getStringValue());
	}
	public void blacklist(Tag tag) {
		blacklist(tag.getStringValue());
	}
	public void unlist(Tag tag) {
		unlist(tag.getStringValue());
	}
	
	public void whitelist(String string) {
		whitelist.add(string, true);
		blacklist.remove(string);
		Reload.notify(Notifier.FILTER_NEEDS_REFRESH);
	}
	public void blacklist(String string) {
		whitelist.remove(string);
		blacklist.add(string, true);
		Reload.notify(Notifier.FILTER_NEEDS_REFRESH);
	}
	public void unlist(String string) {
		whitelist.remove(string);
		blacklist.remove(string);
		Reload.notify(Notifier.FILTER_NEEDS_REFRESH);
	}
	
	public boolean isWhitelisted(Tag tag) {
		return isWhitelisted(tag.getStringValue());
	}
	public boolean isBlacklisted(Tag tag) {
		return isBlacklisted(tag.getStringValue());
	}
	public boolean isUnlisted(Tag tag) {
		return !isWhitelisted(tag) && !isBlacklisted(tag);
	}
	
	public boolean isWhitelisted(String string) {
		return whitelist.contains(string);
	}
	public boolean isBlacklisted(String string) {
		return blacklist.contains(string);
	}
	public boolean isUnlisted(String string) {
		return !isWhitelisted(string) && !isBlacklisted(string);
	}
	
	CustomList<String> getWhitelist() {
		return whitelist;
	}
	CustomList<String> getBlacklist() {
		return blacklist;
	}
}
