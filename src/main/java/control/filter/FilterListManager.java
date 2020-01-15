package control.filter;

import base.CustomList;
import base.entity.Entity;
import control.reload.Notifier;
import control.reload.Reload;
import ui.main.side.TagNode;

public class FilterListManager {
	private final CustomList<TagNode> whitelist = new CustomList<>();
	private final CustomList<TagNode> blacklist = new CustomList<>();
	
	protected FilterListManager() {
	
	}
	
	boolean applyLists(Entity entity) {
		CustomList<String> stringValues = new CustomList<>();
		entity.getTagList().forEach(tag -> stringValues.add(tag.getStringValue()));
		
		int deepestMatch = 0;
		boolean ok = whitelist.isEmpty();
		
		for (TagNode whiteTagNode : whitelist) {
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
			for (TagNode whiteTagNode : whitelist) {
				if (stringValue.contains(whiteTagNode.getStringValue())) {
					if (whiteTagNode.getLevel() > deepestMatch) {
						deepestMatch = whiteTagNode.getLevel();
						ok = true;
					}
				}
			}
			for (TagNode blackTagNode : blacklist) {
				if (stringValue.contains(blackTagNode.getStringValue())) {
					if (blackTagNode.getLevel() >= deepestMatch) {
						deepestMatch = blackTagNode.getLevel();
						ok = false;
					}
				}
			}
		}
		
		return ok;
	}
	
	public void whitelist(TagNode tagNode) {
		whitelist.add(tagNode, true);
		blacklist.remove(tagNode);
		Reload.notify(Notifier.FILTER_NEEDS_REFRESH);
	}
	public void blacklist(TagNode tagNode) {
		whitelist.remove(tagNode);
		blacklist.add(tagNode, true);
		Reload.notify(Notifier.FILTER_NEEDS_REFRESH);
	}
	public void unlist(TagNode tagNode) {
		whitelist.remove(tagNode);
		blacklist.remove(tagNode);
		Reload.notify(Notifier.FILTER_NEEDS_REFRESH);
	}
	
	public boolean isWhitelisted(TagNode tagNode) {
		return whitelist.contains(tagNode);
	}
	public boolean isBlacklisted(TagNode tagNode) {
		return blacklist.contains(tagNode);
	}
	
	CustomList<TagNode> getWhitelist() {
		return whitelist;
	}
	CustomList<TagNode> getBlacklist() {
		return blacklist;
	}
}
