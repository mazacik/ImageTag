package application.gui.panes.side;

import application.controller.Filter;
import application.database.object.TagObject;
import application.gui.decorator.ColorUtil;
import application.gui.nodes.NodeUtil;
import application.gui.nodes.simple.TextNode;
import application.gui.stage.Stages;
import application.main.Instances;
import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.util.Pair;

public class FilterPane extends SidePaneBase {
	public FilterPane() {
		nodeTitle = new TextNode("", false, false, false, true);
		
		TextNode btnCreateNewTag = new TextNode("Create New Tag", true, true, false, true);
		btnCreateNewTag.setBorder(NodeUtil.getBorder(0, 0, 1, 0));
		btnCreateNewTag.prefWidthProperty().bind(this.widthProperty());
		btnCreateNewTag.addEventFilter(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
			Pair<TagObject, Boolean> result = Stages.getTagEditStage()._show();
			Instances.getTagListMain().add(result.getKey());
			Instances.getTagListMain().sort();
			if (result.getValue())
				Instances.getSelect().addTagObject(result.getKey());
			Instances.getReload().doReload();
		});
		
		TextNode btnReset = new TextNode("⟲", true, true, false, true);
		btnReset.addEventFilter(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
			Instances.getFilter().reset();
			Instances.getReload().doReload();
		});
		
		TextNode btnSettings = new TextNode("⁝", true, true, false, true);
		btnSettings.addEventFilter(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> Stages.getFilterSettingsStage()._show());
		
		HBox hBoxTitle = new HBox(btnReset, nodeTitle, btnSettings);
		hBoxTitle.setBorder(NodeUtil.getBorder(0, 0, 1, 0));
		nodeTitle.prefWidthProperty().bind(this.widthProperty());
		
		this.setBorder(NodeUtil.getBorder(0, 1, 0, 0));
		this.getChildren().addAll(hBoxTitle, btnCreateNewTag, scrollPane);
	}
	
	public boolean reload() {
		Filter filter = Instances.getFilter();
		
		nodeTitle.setText("Filter: " + filter.size());
		
		Color textColorDefault = ColorUtil.getTextColorDef();
		Color textColorPositive = ColorUtil.getTextColorPos();
		Color textColorNegative = ColorUtil.getTextColorNeg();
		
		updateNodes();
		
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
			if (Instances.getFilter().isWhitelisted(groupName)) {
				Instances.getFilter().blacklist(groupName);
				textColor = ColorUtil.getTextColorNeg();
			} else if (Instances.getFilter().isBlacklisted(groupName)) {
				Instances.getFilter().unlist(groupName);
				textColor = ColorUtil.getTextColorDef();
			} else {
				Instances.getFilter().whitelist(groupName);
				textColor = ColorUtil.getTextColorPos();
			}
			groupNode.setTextFill(textColor);
			groupNode.getNameNodes().forEach(node -> node.setTextFill(textColor));
		} else {
			TagObject tagObject = Instances.getTagListMain().getTagObject(groupNode.getGroup(), nameNode.getText());
			if (Instances.getFilter().isWhitelisted(tagObject)) {
				Instances.getFilter().blacklist(tagObject);
				if (Instances.getFilter().isBlacklisted(tagObject.getGroup())) {
					groupNode.setTextFill(ColorUtil.getTextColorNeg());
				} else if (!Instances.getFilter().isWhitelisted(tagObject.getGroup())) {
					groupNode.setTextFill(ColorUtil.getTextColorDef());
				}
				nameNode.setTextFill(ColorUtil.getTextColorNeg());
			} else if (Instances.getFilter().isBlacklisted(tagObject)) {
				Instances.getFilter().unlist(tagObject);
				if (!Instances.getFilter().isWhitelisted(tagObject.getGroup()) && !Instances.getFilter().isBlacklisted(tagObject.getGroup())) {
					groupNode.setTextFill(ColorUtil.getTextColorDef());
				}
				nameNode.setTextFill(ColorUtil.getTextColorDef());
			} else {
				Instances.getFilter().whitelist(tagObject);
				if (Instances.getFilter().isWhitelisted(tagObject.getGroup())) {
					groupNode.setTextFill(ColorUtil.getTextColorPos());
				}
				nameNode.setTextFill(ColorUtil.getTextColorPos());
			}
		}
		Instances.getFilter().refresh();
	}
}
