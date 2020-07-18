package backend;

import backend.reload.Notifier;
import backend.reload.Reload;
import frontend.node.textnode.TextNode;
import frontend.stage.TagEditStage;
import main.Main;

import java.util.Comparator;

public class TagUtil {
	public static TextNode currentNode;
	public static TextNode getCurrentNode() {
		return currentNode;
	}
	public static void setCurrentNode(TextNode currentNode) {
		TagUtil.currentNode = currentNode;
	}
	
	public static void create() {
		String newTag = new TagEditStage().create();
		if (!newTag.isEmpty()) { //other checks in TagEditStage
			Main.TAGLIST.add(newTag);
			Main.TAGLIST.sort(Comparator.naturalOrder());
			
			Reload.notify(Notifier.TAGLIST_CHANGED);
		}
	}
	public static void edit(String tag) {
		int index = Main.TAGLIST.indexOf(tag);
		if (index != -1) {
			String newTag = new TagEditStage().edit(tag);
			if (!newTag.isEmpty()) { //other checks in TagEditStage
				Main.TAGLIST.set(index, newTag);
				Main.TAGLIST.sort(Comparator.naturalOrder());
				Main.ENTITYLIST.forEach(entity -> entity.getTagList().replace(tag, newTag));
				
				if (Main.FILTER.getFilterListManager().isWhite(tag)) {
					Main.FILTER.getFilterListManager().unlist(tag);
					Main.FILTER.getFilterListManager().whitelist(newTag);
				} else if (Main.FILTER.getFilterListManager().isBlack(tag)) {
					Main.FILTER.getFilterListManager().unlist(tag);
					Main.FILTER.getFilterListManager().blacklist(newTag);
				}
				
				Reload.notify(Notifier.TAGLIST_CHANGED);
			}
		}
	}
	public static void remove() {
		String tag = currentNode.getText();
		if (Main.TAGLIST.contains(tag)) {
			Main.TAGLIST.remove(tag);
			Main.ENTITYLIST.forEach(entity -> entity.getTagList().remove(tag));
			
			Main.FILTER.getFilterListManager().unlist(tag);
			
			Reload.notify(Notifier.TAGLIST_CHANGED);
		}
	}
	
	private static BaseList<String> clipboard = null;
	public static BaseList<String> getClipboard() {
		return clipboard;
	}
	public static void setClipboard(BaseList<String> clipboard) {
		TagUtil.clipboard = new BaseList<>(clipboard);
	}
}
