package frontend.component.side;

import backend.misc.Direction;
import backend.reload.Reload;
import frontend.node.menu.ListMenu;
import frontend.node.menu.MenuPreset;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import main.Main;

public class FilterTagNode extends TagNode {
	
	public FilterTagNode(String tag) {
		super(tag);
		
		this.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
			if (event.getButton() == MouseButton.PRIMARY) {
				Main.FILTER.getFilterListManager().unlist(tag);
				Reload.start();
			}
		});
		
		ListMenu.install(this, Direction.NONE, ListMenu.MenuTrigger.CLICK_RIGHT, MenuPreset.TAG_FILTER.getTemplate());
	}
}
