package frontend.component.side;

import backend.TagUtil;
import backend.misc.Direction;
import backend.reload.Reload;
import frontend.UserInterface;
import frontend.decorator.DecoratorUtil;
import frontend.node.menu.ClickMenu;
import frontend.node.menu.ListMenu;
import frontend.node.textnode.TextNode;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import main.Main;

public class TagNode extends TextNode {
	private boolean backgroundLock = false;
	
	static {
		ClickMenu.register(TagNode.class, ListMenu.Preset.TAG);
	}
	
	public TagNode(SidePaneBase parentPane, String tag) {
		super(tag);
		
		if (Main.FILTER.getFilterListManager().isWhite(tag)) {
			setTextFill(DecoratorUtil.getColorPositive());
		} else if (Main.FILTER.getFilterListManager().isBlack(tag)) {
			setTextFill(DecoratorUtil.getColorNegative());
		}
		
		this.setAlignment(Pos.CENTER_LEFT);
		this.setPadding(new Insets(0, 0, 0, 10));
		this.setMaxWidth(Double.MAX_VALUE);
		this.addEventFilter(MouseEvent.MOUSE_ENTERED, event -> {
			if (!backgroundLock) {
				this.setBackground(DecoratorUtil.getBackgroundSecondary());
			}
		});
		this.addEventFilter(MouseEvent.MOUSE_EXITED, event -> {
			if (!backgroundLock) {
				this.setBackground(Background.EMPTY);
			}
		});
		this.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
			switch (event.getButton()) {
				case PRIMARY:
					//todo try to get parentPane from event
					if (parentPane == UserInterface.getFilterPane()) {
						clickFilter();
					} else if (parentPane == UserInterface.getSelectPane().getSelectionTagsPane()) {
						clickSelect();
					}
					Reload.start();
					break;
				case SECONDARY:
					TagUtil.setCurrentNode(this);
					break;
			}
		});
		
		ClickMenu.install(this, Direction.NONE, MouseButton.SECONDARY);
	}
	
	private void clickFilter() {
		if (Main.FILTER.getFilterListManager().isWhite(getText())) {
			Main.FILTER.getFilterListManager().blacklist(getText());
			setTextFill(DecoratorUtil.getColorNegative());
		} else if (Main.FILTER.getFilterListManager().isBlack(getText())) {
			Main.FILTER.getFilterListManager().unlist(getText());
			setTextFill(DecoratorUtil.getColorPrimary());
		} else {
			Main.FILTER.getFilterListManager().whitelist(getText());
			setTextFill(DecoratorUtil.getColorPositive());
		}
	}
	private void clickSelect() {
		if (getTextFill().equals(DecoratorUtil.getColorPositive()) || getTextFill().equals(DecoratorUtil.getColorUnion())) {
			Main.SELECT.removeTag(getText());
		} else {
			Main.SELECT.addTag(getText());
		}
	}
	
	public void setBackgroundLock(boolean backgroundLock) {
		this.backgroundLock = backgroundLock;
	}
}
