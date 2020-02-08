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
		CustomList<String> listLevelsAfter = TagEditStage.show(null);
		if (!listLevelsAfter.isEmpty()) {
			_create(listLevelsAfter);
		}
	}
	public static void create(CustomList<String> listLevelsBefore) {
		CustomList<String> listLevelsAfter = TagEditStage.show(listLevelsBefore);
		if (!listLevelsAfter.isEmpty()) {
			_create(listLevelsAfter);
		}
	}
	private static void _create(CustomList<String> listLevelsAfter) {
		Tag tagNew = new Tag(listLevelsAfter);
		
		TagList.getMain().add(tagNew);
		TagList.getMain().sort();
		
		Reload.notify(Notifier.TAG_LIST_MAIN);
	}
	
	public static void edit(String stringValueBefore, int numLevelsBefore) {
		CustomList<String> listLevelsAfter = TagEditStage.show(ClickMenu.getTagNode().getLevels());
		if (!listLevelsAfter.isEmpty()) {
			_edit(stringValueBefore, numLevelsBefore, listLevelsAfter);
		}
	}
	private static void _edit(String stringValueBefore, int numLevelsBefore, CustomList<String> listLevelsAfter) {
		TagList.getMain().getTagsContaining(stringValueBefore).forEach(tag -> tag.replaceLevelsFromStart(numLevelsBefore, listLevelsAfter));
		TagList.getMain().sort();
		
		Filter.getListManager().update(stringValueBefore, numLevelsBefore, listLevelsAfter);
		
		Reload.notify(Notifier.TAG_LIST_MAIN);
	}
	
	public static void remove() {
		TagNode tagNode = ClickMenu.getTagNode();
		if (ConfirmationStage.show("Remove \"" + tagNode.getText() + "\" ?")) {
			tagNode.getSubNodesComplete().forEach(subNode -> Filter.getListManager().unlist(subNode.getStringValue()));
			_remove(TagList.getMain().getTagsContaining(tagNode.getStringValue()));
		}
	}
	private static void _remove(TagList tagList) {
		EntityList.getMain().forEach(entity -> entity.removeTag(tagList));
		TagList.getMain().removeAll(tagList);
		Reload.notify(Notifier.TAG_LIST_MAIN);
	}
}
