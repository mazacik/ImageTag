package application.frontend.pane.side.left;

import application.backend.base.tag.Tag;
import application.frontend.component.NodeUtil;
import application.frontend.component.simple.TextNode;
import application.frontend.decorator.ColorUtil;
import application.frontend.pane.side.GroupNode;
import application.frontend.pane.side.SidePaneBase;
import application.frontend.stage.StageManager;
import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.util.Pair;

import java.util.logging.Logger;

public class FilterPane extends SidePaneBase {
	public FilterPane() {
	
	}
	
	public void init() {
		nodeTitle = new TextNode("", false, false, false, true);
		
		TextNode btnCreateNewTag = new TextNode("Create a New Tag", true, true, false, true);
		btnCreateNewTag.setBorder(NodeUtil.getBorder(0, 0, 1, 0));
		btnCreateNewTag.prefWidthProperty().bind(this.widthProperty());
		btnCreateNewTag.addEventFilter(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
			Pair<Tag, Boolean> result = StageManager.getTagEditStage().show("");
			tagListMain.add(result.getKey());
			tagListMain.sort();
			if (result.getValue())
				select.addTag(result.getKey());
			reload.doReload();
		});
		
		TextNode btnReset = new TextNode("⟲", true, true, false, true);
		btnReset.addEventFilter(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
			filter.reset();
			reload.doReload();
		});
		
		TextNode btnSettings = new TextNode("⁝", true, true, false, true);
		btnSettings.addEventFilter(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> StageManager.getFilterSettingsStage().show());
		
		HBox hBoxTitle = new HBox(btnReset, nodeTitle, btnSettings);
		hBoxTitle.setBorder(NodeUtil.getBorder(0, 0, 1, 0));
		nodeTitle.prefWidthProperty().bind(this.widthProperty());
		
		this.setBorder(NodeUtil.getBorder(0, 1, 0, 0));
		this.getChildren().addAll(hBoxTitle, btnCreateNewTag, scrollPane);
	}
	
	public boolean refresh() {
		Logger.getGlobal().info(this.toString());
		
		nodeTitle.setText("Filter: " + filter.size());
		
		Color textColorDefault = ColorUtil.getTextColorDef();
		Color textColorPositive = ColorUtil.getTextColorPos();
		Color textColorNegative = ColorUtil.getTextColorNeg();
		
		for (Node node : groupNodes.getChildren()) {
			if (node instanceof GroupNode) {
				GroupNode groupNode = (GroupNode) node;
				String group = groupNode.getGroup();
				
				if (filter.isWhitelisted(group)) {
					groupNode.setTextFill(textColorPositive);
				} else if (filter.isBlacklisted(group)) {
					groupNode.setTextFill(textColorNegative);
				} else {
					groupNode.setTextFill(textColorDefault);
				}
				for (TextNode nameNode : groupNode.getNameNodes()) {
					String name = nameNode.getText();
					
					if (filter.isWhitelisted(group, name)) {
						nameNode.setTextFill(textColorPositive);
					} else if (filter.isBlacklisted(group, name)) {
						nameNode.setTextFill(textColorNegative);
					} else {
						nameNode.setTextFill(textColorDefault);
					}
				}
			}
		}
		
		return true;
	}
	
	public void changeNodeState(GroupNode groupNode, TextNode nameNode) {
		if (nameNode == null) {
			String groupName = groupNode.getGroup();
			Color textColor;
			if (filter.isWhitelisted(groupName)) {
				filter.blacklist(groupName);
				textColor = ColorUtil.getTextColorNeg();
			} else if (filter.isBlacklisted(groupName)) {
				filter.unlist(groupName);
				textColor = ColorUtil.getTextColorDef();
			} else {
				filter.whitelist(groupName);
				textColor = ColorUtil.getTextColorPos();
			}
			groupNode.setTextFill(textColor);
			groupNode.getNameNodes().forEach(node -> node.setTextFill(textColor));
		} else {
			Tag tag = tagListMain.getTag(groupNode.getGroup(), nameNode.getText());
			if (filter.isWhitelisted(tag)) {
				filter.blacklist(tag);
				if (filter.isBlacklisted(tag.getGroup())) {
					groupNode.setTextFill(ColorUtil.getTextColorNeg());
				} else if (!filter.isWhitelisted(tag.getGroup())) {
					groupNode.setTextFill(ColorUtil.getTextColorDef());
				}
				nameNode.setTextFill(ColorUtil.getTextColorNeg());
			} else if (filter.isBlacklisted(tag)) {
				filter.unlist(tag);
				if (!filter.isWhitelisted(tag.getGroup()) && !filter.isBlacklisted(tag.getGroup())) {
					groupNode.setTextFill(ColorUtil.getTextColorDef());
				}
				nameNode.setTextFill(ColorUtil.getTextColorDef());
			} else {
				filter.whitelist(tag);
				if (filter.isWhitelisted(tag.getGroup())) {
					groupNode.setTextFill(ColorUtil.getTextColorPos());
				}
				nameNode.setTextFill(ColorUtil.getTextColorPos());
			}
		}
		filter.refresh();
	}
}
