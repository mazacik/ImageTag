package gui.main.side;

import baseobject.tag.Tag;
import gui.component.clickmenu.ClickMenu;
import gui.component.simple.TextNode;
import gui.decorator.ColorUtil;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import main.InstanceCollector;

public class NameNode extends TextNode implements InstanceCollector {
	private static final Insets PADDING = new Insets(0, 0, 0, 50);
	
	private final GroupNode groupNode;
	
	public NameNode(GroupNode groupNode, String text) {
		super(text, true, false, false, false);
		
		this.groupNode = groupNode;
		
		this.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
			switch (event.getButton()) {
				case PRIMARY:
					this.changeState();
					reload.doReload();
					break;
				case SECONDARY:
					ClickMenu.setGroup(groupNode.getText());
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
		if (groupNode.getParentPane() == filterPane) {
			this.changeStateAsFilter();
		} else if (groupNode.getParentPane() == selectPane) {
			this.changeStateAsSelect();
		}
	}
	private void changeStateAsFilter() {
		Tag tag = tagListMain.getTag(groupNode.getText(), this.getText());
		if (filter.isWhitelisted(tag)) {
			filter.blacklist(tag);
			if (filter.isBlacklisted(tag.getGroup())) {
				groupNode.setTextFill(ColorUtil.getColorNegative());
			} else if (!filter.isWhitelisted(tag.getGroup())) {
				groupNode.setTextFill(ColorUtil.getColorPrimary());
			}
			this.setTextFill(ColorUtil.getColorNegative());
		} else if (filter.isBlacklisted(tag)) {
			filter.unlist(tag);
			if (!filter.isWhitelisted(tag.getGroup()) && !filter.isBlacklisted(tag.getGroup())) {
				groupNode.setTextFill(ColorUtil.getColorPrimary());
			}
			this.setTextFill(ColorUtil.getColorPrimary());
		} else {
			filter.whitelist(tag);
			if (filter.isWhitelisted(tag.getGroup())) {
				groupNode.setTextFill(ColorUtil.getColorPositive());
			}
			this.setTextFill(ColorUtil.getColorPositive());
		}
		filter.refresh();
	}
	private void changeStateAsSelect() {
		Tag tag = tagListMain.getTag(groupNode.getText(), this.getText());
		if (this.getTextFill().equals(ColorUtil.getColorPositive()) || this.getTextFill().equals(ColorUtil.getColorShare())) {
			this.setTextFill(ColorUtil.getColorPrimary());
			select.removeTag(tag);
		} else {
			this.setTextFill(ColorUtil.getColorPositive());
			select.addTag(tag);
		}
		
		reload.doReload();
	}
}
