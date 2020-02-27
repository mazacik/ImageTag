package base.tag;

import base.CustomList;
import base.entity.EntityList;
import control.filter.Filter;
import control.reload.Notifier;
import control.reload.Reload;
import ui.custom.ClickMenu;
import ui.main.side.TagNode;
import ui.stage.ConfirmationStage;
import ui.stage.TagEditStage;

public class TagUtil {
	public static void create() {
		create(null);
	}
	public static void create(CustomList<String> listLevelsOld) {
		CustomList<String> listLevelsNew = TagEditStage.show(TagList.getMain(), listLevelsOld);
		if (listLevelsNew != null && !listLevelsNew.isEmpty()) {
			Tag tagNew = new Tag(listLevelsNew);
			
			TagList.getMain().add(tagNew);
			TagList.getMain().sort();
			
			Reload.notify(Notifier.TAG_LIST_MAIN);
		}
	}
	public static void edit(String stringValueOld, int numLevelsOld) {
		TagList affectedTags = TagList.getMain().getTagsStartingWith(stringValueOld);
		TagList tagListHelper = new TagList(TagList.getMain());
		tagListHelper.removeAll(affectedTags);
		
		CustomList<String> listLevelsNew = TagEditStage.show(tagListHelper, ClickMenu.getTagNode().getLevels());
		if (!listLevelsNew.isEmpty()) {
			affectedTags.forEach(tag -> tag.replaceLevelsFromStart(numLevelsOld, listLevelsNew));
			TagList.getMain().sort();
			Filter.getListManager().update(stringValueOld, numLevelsOld, listLevelsNew);
			
			Reload.notify(Notifier.TAG_LIST_MAIN);
		}
	}
	public static void remove() {
		TagNode tagNode = ClickMenu.getTagNode();
		if (ConfirmationStage.show("Remove \"" + tagNode.getText() + "\" ?")) {
			tagNode.getSubNodesAll().forEach(subNode -> Filter.getListManager().unlist(subNode.getStringValue()));
			
			TagList tagList = TagList.getMain().getTagsStartingWith(tagNode.getStringValue());
			EntityList.getMain().forEach(entity -> entity.removeTag(tagList));
			TagList.getMain().removeAll(tagList);
			
			Reload.notify(Notifier.TAG_LIST_MAIN);
		}
	}
}
