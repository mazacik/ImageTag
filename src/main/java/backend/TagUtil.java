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
			Main.DB_TAG.add(newTag);
			Main.DB_TAG.sort(Comparator.naturalOrder());
			
			Reload.notify(Notifier.TAGLIST_CHANGED);
		}
	}
	public static void edit(String tag) {
		int index = Main.DB_TAG.indexOf(tag);
		if (index != -1) {
			String newTag = new TagEditStage().edit(tag);
			if (!newTag.isEmpty()) { //other checks in TagEditStage
				Main.DB_TAG.set(index, newTag);
				Main.DB_TAG.sort(Comparator.naturalOrder());
				Main.DB_ENTITY.forEach(entity -> entity.getTagList().replace(tag, newTag));
				
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
		if (Main.DB_TAG.contains(tag)) {
			Main.DB_TAG.remove(tag);
			Main.DB_ENTITY.forEach(entity -> entity.getTagList().remove(tag));
			
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
