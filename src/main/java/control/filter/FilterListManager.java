package control.filter;

import base.CustomList;
import base.entity.Entity;
import control.reload.Notifier;
import control.reload.Reload;

public class FilterListManager {
	private final CustomList<TagSimple> whitelist = new CustomList<>();
	private final CustomList<TagSimple> blacklist = new CustomList<>();
	
	public boolean applyLists(Entity entity) {
		CustomList<String> stringValues = new CustomList<>();
		entity.getTagList().forEach(tag -> stringValues.add(tag.getStringValue()));
		
		int deepestMatch = 0;
		boolean ok = whitelist.isEmpty();
		
		for (TagSimple whiteTagNode : whitelist) {
			boolean hasThisTag = false;
			for (String stringValue : stringValues) {
				if (stringValue.startsWith(whiteTagNode.getStringValue())) {
					hasThisTag = true;
					break;
				}
			}
			if (!hasThisTag) {
				return false;
			}
		}
		
		if (!whitelist.isEmpty() && stringValues.isEmpty()) {
			return false;
		}
		
		for (String stringValue : stringValues) {
			for (TagSimple whiteTagNode : whitelist) {
				if (stringValue.contains(whiteTagNode.getStringValue())) {
					if (whiteTagNode.getLevelCount() >= deepestMatch) {
						deepestMatch = whiteTagNode.getLevelCount();
						ok = true;
					}
				}
			}
			for (TagSimple blackTagNode : blacklist) {
				if (stringValue.contains(blackTagNode.getStringValue())) {
					if (blackTagNode.getLevelCount() >= deepestMatch) {
						deepestMatch = blackTagNode.getLevelCount();
						ok = false;
					}
				}
			}
		}
		
		return ok;
	}
	
	public void whitelist(String stringValue, int numLevels) {
		whitelistAdd(stringValue, numLevels);
		blacklistRemove(stringValue);
		Reload.notify(Notifier.FILTER_NEEDS_REFRESH);
	}
	public void blacklist(String stringValue, int numLevels) {
		whitelistRemove(stringValue);
		blacklistAdd(stringValue, numLevels);
		Reload.notify(Notifier.FILTER_NEEDS_REFRESH);
	}
	public void unlist(String stringValue) {
		whitelistRemove(stringValue);
		blacklistRemove(stringValue);
		Reload.notify(Notifier.FILTER_NEEDS_REFRESH);
	}
	public void clear() {
		whitelist.clear();
		blacklist.clear();
		Reload.notify(Notifier.FILTER_NEEDS_REFRESH);
	}
	
	private void whitelistAdd(String stringValue, int numLevels) {
		for (TagSimple tagSimple : whitelist) {
			if (tagSimple.stringValue.equals(stringValue)) {
				return;
			}
		}
		
		whitelist.add(new TagSimple(stringValue, numLevels));
	}
	private void blacklistAdd(String stringValue, int numLevels) {
		for (TagSimple tagSimple : blacklist) {
			if (tagSimple.stringValue.equals(stringValue)) {
				return;
			}
		}
		
		blacklist.add(new TagSimple(stringValue, numLevels));
	}
	private void whitelistRemove(String stringValue) {
		whitelist.removeIf(tagSimple -> tagSimple.stringValue.equals(stringValue));
	}
	private void blacklistRemove(String stringValue) {
		blacklist.removeIf(tagSimple -> tagSimple.stringValue.equals(stringValue));
	}
	
	//todo needs testing
	public void update(String stringBefore, int numLevelsBefore, CustomList<String> listLevelsAfter) {
		StringBuilder sb = new StringBuilder();
		listLevelsAfter.forEach(sb::append);
		String stringAfter = sb.toString();
		
		for (TagSimple tagSimple : whitelist) {
			if (tagSimple.stringValue.startsWith(stringBefore)) {
				unlist(tagSimple.stringValue);
				String stringUnaffected = tagSimple.stringValue.substring(stringBefore.length());
				int numLevelsUnaffected = tagSimple.levelCount - numLevelsBefore;
				whitelist(stringAfter + stringUnaffected, listLevelsAfter.size() + numLevelsUnaffected);
			}
		}
	}
	
	public boolean isWhitelisted(String stringValue) {
		for (TagSimple tagSimple : whitelist) {
			if (tagSimple.stringValue.equals(stringValue)) {
				return true;
			}
		}
		return false;
	}
	public boolean isBlacklisted(String stringValue) {
		for (TagSimple tagSimple : blacklist) {
			if (tagSimple.stringValue.equals(stringValue)) {
				return true;
			}
		}
		return false;
	}
	
	private static class TagSimple {
		private final String stringValue;
		private final int levelCount;
		
		public TagSimple(String stringValue, int levelCount) {
			this.stringValue = stringValue;
			this.levelCount = levelCount;
		}
		
		public String getStringValue() {
			return stringValue;
		}
		public int getLevelCount() {
			return levelCount;
		}
	}
}
