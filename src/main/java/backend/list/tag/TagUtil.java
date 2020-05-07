package backend.list.tag;

import backend.control.reload.Notifier;
import backend.control.reload.Reload;
import backend.list.BaseList;
import frontend.component.side.TagNode;
import frontend.stage.ConfirmationStage;
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
			Main.TAGLIST.add(new Tag(listLevelsNew));
			Main.TAGLIST.sort();
			
			Reload.notify(Notifier.TAGLIST_CHANGED);
		}
	}
	public static void edit(String stringValueOld, int numLevelsOld) {
		TagList affectedTags = Main.TAGLIST.getTagsStartingWith(stringValueOld);
		TagList tagListHelper = new TagList(Main.TAGLIST);
		tagListHelper.removeAll(affectedTags);
		
		BaseList<String> listLevelsNew = new TagEditStage().showEdit(tagListHelper, currentTagNode.getLevels());
		editImpl(affectedTags, stringValueOld, numLevelsOld, listLevelsNew);
	}
	private static void editImpl(TagList affectedTags, String stringValueOld, int numLevelsOld, BaseList<String> listLevelsNew) {
		if (!listLevelsNew.isEmpty()) {
			affectedTags.forEach(tag -> tag.replaceLevelsFromStart(numLevelsOld, listLevelsNew));
			Main.TAGLIST.sort();
			Main.FILTER.getFilterListManager().update(stringValueOld, numLevelsOld, listLevelsNew);
			
			Reload.notify(Notifier.TAGLIST_CHANGED);
		}
	}
	public static void remove() {
		if (new ConfirmationStage("Remove \"" + currentTagNode.getText() + "\" ?").getResult()) {
			currentTagNode.getSubNodesDeepest().forEach(subNode -> Main.FILTER.getFilterListManager().unlist(subNode.getStringValue()));
			
			TagList tagList = Main.TAGLIST.getTagsStartingWith(currentTagNode.getStringValue());
			Main.ENTITYLIST.forEach(entity -> entity.removeTag(tagList));
			Main.TAGLIST.removeAll(tagList);
			
			Reload.notify(Notifier.TAGLIST_CHANGED);
		}
	}
	public static void removeIntermediate() {
		if (new ConfirmationStage("Remove \"" + currentTagNode.getText() + "\" ?").getResult()) {
			int index = currentTagNode.getLevels().size() - 1;
			
			TagList affectedTags = Main.TAGLIST.getTagsStartingWith(currentTagNode.getStringValue());
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
