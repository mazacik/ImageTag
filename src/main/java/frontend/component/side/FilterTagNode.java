package frontend.component.side;

import backend.misc.Direction;
import backend.reload.Reload;
import frontend.decorator.DecoratorUtil;
import frontend.node.menu.ListMenu;
import frontend.node.menu.MenuPreset;
import javafx.scene.input.MouseEvent;
import main.Main;

public class FilterTagNode extends TagNode {
	
	public FilterTagNode(String tag) {
		super(tag);
		
		this.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
			Main.FILTER.getFilterListManager().advance(tag);
			Reload.start();
			
			if (Main.FILTER.getFilterListManager().isWhite(tag)) this.getTextNode().setTextFill(DecoratorUtil.getColorPositive());
			else if (Main.FILTER.getFilterListManager().isBlack(tag)) this.getTextNode().setTextFill(DecoratorUtil.getColorNegative());
			else this.getTextNode().setTextFill(DecoratorUtil.getColorPrimary());
		});
		
		ListMenu.install(this, Direction.NONE, ListMenu.MenuTrigger.CLICK_RIGHT, MenuPreset.TAG_FILTER.getTemplate());
	}
}
