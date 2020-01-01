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
import ui.component.clickmenu.ClickMenu;
import ui.component.simple.TextNode;
import ui.decorator.ColorUtil;
import ui.main.side.left.PaneFilter;
import ui.main.side.right.PaneSelect;

public class NameNode extends TextNode {
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
		if (Filter.getListManager().isWhitelisted(tag)) {
			Filter.getListManager().blacklist(tag);
			if (Filter.getListManager().isBlacklisted(tag.getGroup())) {
				groupNode.setTextFill(ColorUtil.getColorNegative());
			} else if (!Filter.getListManager().isWhitelisted(tag.getGroup())) {
				groupNode.setTextFill(ColorUtil.getColorPrimary());
			}
			this.setTextFill(ColorUtil.getColorNegative());
		} else if (Filter.getListManager().isBlacklisted(tag)) {
			Filter.getListManager().unlist(tag);
			if (!Filter.getListManager().isWhitelisted(tag.getGroup()) && !Filter.getListManager().isBlacklisted(tag.getGroup())) {
				groupNode.setTextFill(ColorUtil.getColorPrimary());
			}
			this.setTextFill(ColorUtil.getColorPrimary());
		} else {
			Filter.getListManager().whitelist(tag);
			if (Filter.getListManager().isWhitelisted(tag.getGroup())) {
				groupNode.setTextFill(ColorUtil.getColorPositive());
			}
			this.setTextFill(ColorUtil.getColorPositive());
		}
		Filter.refresh();
	}
	private void changeStateAsSelect() {
		Tag tag = TagList.getMain().getTag(groupNode.getGroup(), this.getText());
		if (this.getTextFill().equals(ColorUtil.getColorPositive()) || this.getTextFill().equals(ColorUtil.getColorShare())) {
			this.setTextFill(ColorUtil.getColorPrimary());
			Select.getEntities().removeTag(tag);
		} else {
			this.setTextFill(ColorUtil.getColorPositive());
			Select.getEntities().addTag(tag);
		}
		
		Reload.start();
	}
}
