package backend.filter;

import backend.BaseList;
import backend.entity.Entity;
import backend.reload.Notifier;
import backend.reload.Reload;

public class FilterListManager {
	private final BaseList<String> whitelist = new BaseList<>();
	private final BaseList<String> blacklist = new BaseList<>();
	
	public boolean checkLists(Entity entity) {
		return entity.getTagList().containsAll(whitelist) && !entity.getTagList().containsAny(blacklist);
	}
	
	public void advance(String tag) {
		if (isWhite(tag)) {
			blacklist(tag);
		} else if (isBlack(tag)) {
			unlist(tag);
		} else {
			whitelist(tag);
		}
	}
	public void whitelist(String tag) {
		whitelist.add(tag, true);
		blacklist.remove(tag);
		Reload.notify(Notifier.FILTER_NEEDS_REFRESH);
	}
	public void blacklist(String tag) {
		whitelist.remove(tag);
		blacklist.add(tag, true);
		Reload.notify(Notifier.FILTER_NEEDS_REFRESH);
	}
	public void unlist(String tag) {
		whitelist.remove(tag);
		blacklist.remove(tag);
		Reload.notify(Notifier.FILTER_NEEDS_REFRESH);
	}
	public void clear() {
		whitelist.clear();
		blacklist.clear();
		Reload.notify(Notifier.FILTER_NEEDS_REFRESH);
	}
	
	public boolean isWhite(String tag) {
		return whitelist.contains(tag);
	}
	public boolean isBlack(String tag) {
		return blacklist.contains(tag);
	}
}
