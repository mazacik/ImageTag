package base.tag;

import base.CustomList;
import control.reload.Notifier;
import control.reload.Reload;
import main.Root;
import ui.main.side.TagNode;
import ui.stage.ConfirmationStage;
import ui.stage.TagEditStage;

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
	public static void create(CustomList<String> levelsOld) {
		CustomList<String> listLevelsNew = new TagEditStage().showCreate(levelsOld);
		if (listLevelsNew != null && !listLevelsNew.isEmpty()) {
			Root.TAGLIST.addImpl(new Tag(listLevelsNew));
			Root.TAGLIST.sort();
			
			Reload.notify(Notifier.TAGLIST_CHANGED);
		}
	}
	public static void edit(String stringValueOld, int numLevelsOld) {
		TagList affectedTags = Root.TAGLIST.getTagsStartingWith(stringValueOld);
		TagList tagListHelper = new TagList(Root.TAGLIST);
		tagListHelper.removeAll(affectedTags);
		
		CustomList<String> listLevelsNew = new TagEditStage().showEdit(tagListHelper, currentTagNode.getLevels());
		if (!listLevelsNew.isEmpty()) {
			affectedTags.forEach(tag -> tag.replaceLevelsFromStart(numLevelsOld, listLevelsNew));
			Root.TAGLIST.sort();
			Root.FILTER.getListManager().update(stringValueOld, numLevelsOld, listLevelsNew);
			
			Reload.notify(Notifier.TAGLIST_CHANGED);
		}
	}
	public static void remove() {
		if (new ConfirmationStage("Remove \"" + currentTagNode.getText() + "\" ?").getResult()) {
			currentTagNode.getSubNodesDeepest().forEach(subNode -> Root.FILTER.getListManager().unlist(subNode.getStringValue()));
			
			TagList tagList = Root.TAGLIST.getTagsStartingWith(currentTagNode.getStringValue());
			Root.ENTITYLIST.forEach(entity -> entity.removeTag(tagList));
			Root.TAGLIST.removeAll(tagList);
			
			Reload.notify(Notifier.TAGLIST_CHANGED);
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
