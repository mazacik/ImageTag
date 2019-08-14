package application.gui.panes.side;

import application.controller.Filter;
import application.database.object.TagObject;
import application.gui.decorator.ColorUtil;
import application.gui.decorator.SizeUtil;
import application.gui.nodes.NodeUtil;
import application.gui.nodes.simple.TextNode;
import application.gui.stage.Stages;
import application.main.Instances;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Pair;

public class FilterPane extends SidePaneBase {
	
	private boolean needsReload;
	
	public FilterPane() {
		needsReload = false;
		
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
		
		tagNodesBox = new VBox();
		scrollPane = new ScrollPane();
		scrollPane.setContent(tagNodesBox);
		scrollPane.setFitToWidth(true);
		scrollPane.setFitToHeight(true);
		scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
		scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
		scrollPane.setBackground(Background.EMPTY);
		
		TextNode btnReload = new TextNode("⟲", true, true, false, true);
		btnReload.addEventFilter(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
			Instances.getFilter().reset();
			Instances.getReload().doReload();
		});
		
		TextNode btnSettings = new TextNode("⁝", true, true, false, true);
		btnSettings.addEventFilter(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> Stages.getFilterSettingsStage()._show());
		
		HBox hBoxTitle = new HBox(btnReload, nodeTitle, btnSettings);
		hBoxTitle.setBorder(NodeUtil.getBorder(0, 0, 1, 0));
		nodeTitle.prefWidthProperty().bind(this.widthProperty());
		
		this.setPrefWidth(SizeUtil.getUsableScreenWidth());
		this.setMinWidth(SizeUtil.getMinWidthSideLists());
		this.getChildren().addAll(hBoxTitle, btnCreateNewTag, scrollPane);
	}
	
	public boolean reload() {
		Filter filter = Instances.getFilter();
		
		nodeTitle.setText("Filter: " + filter.size());
		
		Color textColorDefault = ColorUtil.getTextColorDef();
		Color textColorPositive = ColorUtil.getTextColorPos();
		Color textColorNegative = ColorUtil.getTextColorNeg();
		
		refresh();
		
		for (Node node : tagNodesBox.getChildren()) {
			if (node instanceof TagNode) {
				TagNode tagNode = (TagNode) node;
				String group = tagNode.getGroup();
				
				if (filter.isWhitelisted(group)) {
					tagNode.setTextFill(textColorPositive);
				} else if (filter.isBlacklisted(group)) {
					tagNode.setTextFill(textColorNegative);
				} else {
					tagNode.setTextFill(textColorDefault);
				}
				for (TextNode nameNode : tagNode.getNameNodes()) {
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
	
	@Override
	public void changeNodeState(TagNode tagNode, TextNode nameNode) {
		if (nameNode == null) {
			String groupName = tagNode.getGroup();
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
			tagNode.setTextFill(textColor);
			tagNode.getNameNodes().forEach(node -> node.setTextFill(textColor));
		} else {
			TagObject tagObject = Instances.getTagListMain().getTagObject(tagNode.getGroup(), nameNode.getText());
			if (Instances.getFilter().isWhitelisted(tagObject)) {
				Instances.getFilter().blacklist(tagObject);
				if (Instances.getFilter().isBlacklisted(tagObject.getGroup())) {
					tagNode.setTextFill(ColorUtil.getTextColorNeg());
				} else if (!Instances.getFilter().isWhitelisted(tagObject.getGroup())) {
					tagNode.setTextFill(ColorUtil.getTextColorDef());
				}
				nameNode.setTextFill(ColorUtil.getTextColorNeg());
			} else if (Instances.getFilter().isBlacklisted(tagObject)) {
				Instances.getFilter().unlist(tagObject);
				if (!Instances.getFilter().isWhitelisted(tagObject.getGroup()) && !Instances.getFilter().isBlacklisted(tagObject.getGroup())) {
					tagNode.setTextFill(ColorUtil.getTextColorDef());
				}
				nameNode.setTextFill(ColorUtil.getTextColorDef());
			} else {
				Instances.getFilter().whitelist(tagObject);
				if (Instances.getFilter().isWhitelisted(tagObject.getGroup())) {
					tagNode.setTextFill(ColorUtil.getTextColorPos());
				}
				nameNode.setTextFill(ColorUtil.getTextColorPos());
			}
		}
		Instances.getFilter().refresh();
	}
	@Override
	public boolean getNeedsReload() {
		return needsReload;
	}
	@Override
	public void setNeedsReload(boolean needsReload) {
		this.needsReload = needsReload;
	}
}
