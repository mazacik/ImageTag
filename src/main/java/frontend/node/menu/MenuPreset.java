package frontend.node.menu;

import frontend.node.SeparatorNode;
import frontend.node.textnode.ArrowTextNodeTemplates;
import frontend.node.textnode.TextNodeTemplates;
import javafx.scene.layout.Region;

public enum MenuPreset {
	ENTITY_TILE(TextNodeTemplates.FILE_OPEN.get(),
	            TextNodeTemplates.FILE_EDIT.get(),
	            TextNodeTemplates.FILE_BROWSE.get(),
	            TextNodeTemplates.FILE_DELETE.get(),
	            TextNodeTemplates.SELECTION_DELETE.get(),
	            new SeparatorNode(),
	            ArrowTextNodeTemplates.FILE_TAGS.get(),
	            ArrowTextNodeTemplates.SELECTION_TAGS.get(),
	            ArrowTextNodeTemplates.GROUP.get(),
	            new SeparatorNode(),
	            TextNodeTemplates.FILE_COPYFILENAME.get(),
	            TextNodeTemplates.FILE_COPYFILEPATH.get(),
	            new SeparatorNode(),
	            ArrowTextNodeTemplates.TOOLS.get()
	),
	TAG_SELECT(TextNodeTemplates.TAG_EDIT.get(),
	           TextNodeTemplates.TAG_REMOVE.get(),
	           TextNodeTemplates.TAG_DELETE.get()
	),
	TAG_FILTER(TextNodeTemplates.TAG_EDIT.get(),
	           TextNodeTemplates.TAG_REMOVE.get(),
	           TextNodeTemplates.TAG_DELETE.get()
	),
	;
	
	private final Region[] regions;
	
	static {
		for (MenuPreset preset : values()) {
			ListMenu.getTemplates().add(new ListMenu(false, preset.regions));
		}
	}
	
	MenuPreset(Region... regions) {
		this.regions = regions;
	}
	
	public ListMenu getTemplate() {
		return ListMenu.getTemplates().get(this.ordinal());
	}
}
