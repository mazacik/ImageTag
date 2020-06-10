package backend.list.tag;

import backend.control.reload.Notifier;
import backend.control.reload.Reload;
import backend.list.BaseList;
import frontend.component.side.TagNode;
import frontend.stage.TagEditStage;
import main.Main;

public class TagUtil {
	public static TagNode currentTagNode;
	public static TagNode getCurrentTagNode() {
		return currentTagNode;
	}
	public static void setCurrentTagNode(TagNode currentTagNode) {
		TagUtil.currentTagNode = currentTagNode;
	}
	
	public static void create() {
		create(null);
	}
	public static void create(BaseList<String> levelsOld) {
		BaseList<String> listLevelsNew = new TagEditStage().showCreate(levelsOld);
		if (listLevelsNew != null && !listLevelsNew.isEmpty()) {
			Main.DB_TAG.add(new Tag(listLevelsNew));
			Main.DB_TAG.sort();
			
			Reload.notify(Notifier.TAGLIST_CHANGED);
		}
	}
	public static void edit(String stringValueOld, int numLevelsOld) {
		TagList affectedTags = Main.DB_TAG.getTagsStartingWith(stringValueOld);
		TagList tagListHelper = new TagList(Main.DB_TAG);
		tagListHelper.removeAll(affectedTags);
		
		BaseList<String> listLevelsNew = new TagEditStage().showEdit(tagListHelper, currentTagNode.getLevels());
		editImpl(affectedTags, stringValueOld, numLevelsOld, listLevelsNew);
	}
	private static void editImpl(TagList affectedTags, String stringValueOld, int numLevelsOld, BaseList<String> listLevelsNew) {
		if (!listLevelsNew.isEmpty()) {
			affectedTags.forEach(tag -> tag.replaceLevelsFromStart(numLevelsOld, listLevelsNew));
			Main.DB_TAG.sort();
			Main.FILTER.getFilterListManager().update(stringValueOld, numLevelsOld, listLevelsNew);
			
			Reload.notify(Notifier.TAGLIST_CHANGED);
		}
	}
	public static void remove() {
		currentTagNode.getSubNodesDeepest().forEach(subNode -> Main.FILTER.getFilterListManager().unlist(subNode.getStringValue()));
		
		TagList tagList = Main.DB_TAG.getTagsStartingWith(currentTagNode.getStringValue());
		Main.DB_ENTITY.forEach(entity -> entity.removeTag(tagList));
		Main.DB_TAG.removeAll(tagList);
		
		Reload.notify(Notifier.TAGLIST_CHANGED);
	}
	public static void removeIntermediate() {
		if (currentTagNode.isLast()) {
			TagUtil.remove();
		} else {
			int index = currentTagNode.getLevels().size() - 1;
			
			TagList affectedTags = Main.DB_TAG.getTagsStartingWith(currentTagNode.getStringValue());
			BaseList<String> listLevelsNew = new BaseList<>(currentTagNode.getLevels());
			listLevelsNew.remove(index);
			
			editImpl(affectedTags, currentTagNode.getStringValue(), currentTagNode.getLevels().size(), listLevelsNew);
		}
	}
	
	private static TagList clipboard = null;
	public static TagList getClipboard() {
		return clipboard;
	}
	public static void setClipboard(TagList clipboard) {
		TagUtil.clipboard = clipboard;
	}
}
