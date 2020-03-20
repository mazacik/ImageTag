package base.tag;

import base.CustomList;
import base.entity.EntityList;
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
		CustomList<String> listLevelsNew = TagEditStage.showCreate(levelsOld);
		if (listLevelsNew != null && !listLevelsNew.isEmpty()) {
			TagList.getMain().addImpl(new Tag(listLevelsNew));
			TagList.getMain().sort();
			
			Reload.notify(Notifier.TAGLIST_CHANGED);
		}
	}
	public static void edit(String stringValueOld, int numLevelsOld) {
		TagList affectedTags = TagList.getMain().getTagsStartingWith(stringValueOld);
		TagList tagListHelper = new TagList(TagList.getMain());
		tagListHelper.removeAll(affectedTags);
		
		CustomList<String> listLevelsNew = TagEditStage.showEdit(tagListHelper, currentTagNode.getLevels());
		if (!listLevelsNew.isEmpty()) {
			affectedTags.forEach(tag -> tag.replaceLevelsFromStart(numLevelsOld, listLevelsNew));
			TagList.getMain().sort();
			Root.FILTER.getListManager().update(stringValueOld, numLevelsOld, listLevelsNew);
			
			Reload.notify(Notifier.TAGLIST_CHANGED);
		}
	}
	public static void remove() {
		if (ConfirmationStage.show("Remove \"" + currentTagNode.getText() + "\" ?")) {
			currentTagNode.getSubNodesDeepest().forEach(subNode -> Root.FILTER.getListManager().unlist(subNode.getStringValue()));
			
			TagList tagList = TagList.getMain().getTagsStartingWith(currentTagNode.getStringValue());
			EntityList.getMain().forEach(entity -> entity.removeTag(tagList));
			TagList.getMain().removeAll(tagList);
			
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
