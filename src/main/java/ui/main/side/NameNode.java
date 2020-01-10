package ui.main.side;

import base.tag.Tag;
import base.tag.TagList;
import control.Select;
import control.filter.Filter;
import control.reload.Reload;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import ui.custom.ClickMenu;
import ui.decorator.Decorator;
import ui.node.NodeText;

public class NameNode extends NodeText {
	private static final Insets PADDING = new Insets(0, 0, 0, 50);
	
	private final GroupNode groupNode;
	
	public NameNode(GroupNode groupNode, String text) {
		super(text, true, false, false, false);
		
		this.groupNode = groupNode;
		
		this.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
			switch (event.getButton()) {
				case PRIMARY:
					this.changeState();
					Reload.start();
					break;
				case SECONDARY:
					ClickMenu.setGroup(groupNode.getGroup());
					ClickMenu.setName(this.getText());
					break;
			}
		});
		ClickMenu.install(this, MouseButton.SECONDARY, ClickMenu.StaticInstance.TAG_NAME);
		
		this.prefWidthProperty().bind(groupNode.widthProperty());
		this.setAlignment(Pos.CENTER_LEFT);
		this.setPadding(PADDING);
	}
	
	public void changeState() {
		if (groupNode.getParentPane() == PaneFilter.getInstance()) {
			this.changeStateAsFilter();
		} else if (groupNode.getParentPane() == PaneSelect.getInstance()) {
			this.changeStateAsSelect();
		}
	}
	private void changeStateAsFilter() {
		Tag tag = TagList.getMain().getTag(groupNode.getGroup(), this.getText());
		if (Filter.getListManager().isWhitelisted(tag.getID())) {
			Filter.getListManager().blacklist(tag.getID());
			if (Filter.getListManager().isBlacklisted(tag.getGroup())) {
				groupNode.setTextFill(Decorator.getColorNegative());
			} else if (!Filter.getListManager().isWhitelisted(tag.getGroup())) {
				groupNode.setTextFill(Decorator.getColorPrimary());
			}
			this.setTextFill(Decorator.getColorNegative());
		} else if (Filter.getListManager().isBlacklisted(tag.getID())) {
			Filter.getListManager().unlist(tag.getID());
			if (!Filter.getListManager().isWhitelisted(tag.getGroup()) && !Filter.getListManager().isBlacklisted(tag.getGroup())) {
				groupNode.setTextFill(Decorator.getColorPrimary());
			}
			this.setTextFill(Decorator.getColorPrimary());
		} else {
			Filter.getListManager().whitelist(tag.getID());
			if (Filter.getListManager().isWhitelisted(tag.getGroup())) {
				groupNode.setTextFill(Decorator.getColorPositive());
			}
			this.setTextFill(Decorator.getColorPositive());
		}
	}
	private void changeStateAsSelect() {
		Integer tagID = TagList.getMain().getTag(groupNode.getGroup(), this.getText()).getID();
		if (this.getTextFill().equals(Decorator.getColorPositive()) || this.getTextFill().equals(Decorator.getColorShare())) {
			this.setTextFill(Decorator.getColorPrimary());
			Select.getEntities().removeTag(tagID);
		} else {
			this.setTextFill(Decorator.getColorPositive());
			Select.getEntities().addTag(tagID);
		}
		
		Reload.start();
	}
}
