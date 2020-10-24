package frontend.component.side;

import backend.misc.Direction;
import frontend.node.menu.ListMenu;
import frontend.node.menu.MenuPreset;

public class SelectTagNode extends TagNode {
	
	public SelectTagNode(String tag) {
		super(tag);
		
		ListMenu.install(this, Direction.NONE, ListMenu.MenuTrigger.CLICK_RIGHT, MenuPreset.TAG_SELECT.getTemplate());
	}
	
}
