package userinterface.main.side;

import control.Filter;
import database.object.TagObject;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.util.Pair;
import main.InstanceManager;
import userinterface.nodes.ColorData;
import userinterface.nodes.NodeUtil;
import userinterface.nodes.base.TextNode;
import userinterface.nodes.menu.ClickMenuLeft;
import userinterface.stage.StageUtil;
import userinterface.style.ColorUtil;
import userinterface.style.SizeUtil;
import userinterface.style.enums.ColorType;
import utils.enums.Direction;

public class FilterPane extends SidePaneBase {
	private final TextNode nodeRefresh;
	private final TextNode nodeSettings;
	private final TextNode nodeReset;
	
	public FilterPane() {
		ColorData colorDataSimple = new ColorData(ColorType.DEF, ColorType.ALT, ColorType.DEF, ColorType.DEF);
		
		nodeTitle = new TextNode("", colorDataSimple);
		nodeTitle.setBorder(NodeUtil.getBorder(0, 0, 1, 0));
		nodeTitle.prefWidthProperty().bind(this.widthProperty());
		
		nodeRefresh = new TextNode("Refresh", colorDataSimple);
		nodeSettings = new TextNode("Settings", colorDataSimple);
		nodeReset = new TextNode("Reset", colorDataSimple);
		ClickMenuLeft.install(nodeTitle, Direction.RIGHT, nodeRefresh, nodeSettings, nodeReset);
		
		TextNode btnNew = new TextNode("Create a new tag", colorDataSimple);
		btnNew.setBorder(NodeUtil.getBorder(0, 0, 1, 0));
		btnNew.prefWidthProperty().bind(this.widthProperty());
		btnNew.setOnMouseClicked(event -> {
			if (event.getButton() == MouseButton.PRIMARY) {
				Pair<TagObject, Boolean> result = StageUtil.showStageEditorTag();
				InstanceManager.getTagListMain().add(result.getKey());
				InstanceManager.getTagListMain().sort();
				if (result.getValue()) InstanceManager.getSelect().addTagObject(result.getKey());
				InstanceManager.getReload().doReload();
			}
		});
		
		tagNodesBox = NodeUtil.getVBox(ColorType.DEF, ColorType.DEF);
		scrollPane = new ScrollPane();
		scrollPane.setContent(tagNodesBox);
		scrollPane.setFitToWidth(true);
		scrollPane.setFitToHeight(true);
		scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
		scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
		NodeUtil.addToManager(scrollPane, ColorType.DEF);
		
		this.setPrefWidth(SizeUtil.getUsableScreenWidth());
		this.setMinWidth(SizeUtil.getMinWidthSideLists());
		this.getChildren().addAll(nodeTitle, btnNew, scrollPane);
	}
	
	public boolean reload() {
		Filter filter = InstanceManager.getFilter();
		
		nodeTitle.setText("Filter: " + filter.size() + " matches");
		
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
	
	public void changeNodeState(TagNode tagNode, TextNode nameNode) {
		if (nameNode == null) {
			String groupName = tagNode.getGroup();
			Color textColor;
			if (InstanceManager.getFilter().isWhitelisted(groupName)) {
				InstanceManager.getFilter().blacklist(groupName);
				textColor = ColorUtil.getTextColorNeg();
			} else if (InstanceManager.getFilter().isBlacklisted(groupName)) {
				InstanceManager.getFilter().unlist(groupName);
				textColor = ColorUtil.getTextColorDef();
			} else {
				InstanceManager.getFilter().whitelist(groupName);
				textColor = ColorUtil.getTextColorPos();
			}
			tagNode.setTextFill(textColor);
			tagNode.getNameNodes().forEach(node -> node.setTextFill(textColor));
		} else {
			TagObject tagObject = InstanceManager.getTagListMain().getTagObject(tagNode.getGroup(), nameNode.getText());
			if (InstanceManager.getFilter().isWhitelisted(tagObject)) {
				InstanceManager.getFilter().blacklist(tagObject);
				if (InstanceManager.getFilter().isBlacklisted(tagObject.getGroup())) {
					tagNode.setTextFill(ColorUtil.getTextColorNeg());
				} else if (!InstanceManager.getFilter().isWhitelisted(tagObject.getGroup())) {
					tagNode.setTextFill(ColorUtil.getTextColorDef());
				}
				nameNode.setTextFill(ColorUtil.getTextColorNeg());
			} else if (InstanceManager.getFilter().isBlacklisted(tagObject)) {
				InstanceManager.getFilter().unlist(tagObject);
				if (!InstanceManager.getFilter().isWhitelisted(tagObject.getGroup()) && !InstanceManager.getFilter().isBlacklisted(tagObject.getGroup())) {
					tagNode.setTextFill(ColorUtil.getTextColorDef());
				}
				nameNode.setTextFill(ColorUtil.getTextColorDef());
			} else {
				InstanceManager.getFilter().whitelist(tagObject);
				if (InstanceManager.getFilter().isWhitelisted(tagObject.getGroup())) {
					tagNode.setTextFill(ColorUtil.getTextColorPos());
				}
				nameNode.setTextFill(ColorUtil.getTextColorPos());
			}
		}
		InstanceManager.getFilter().refresh();
	}
	
	public TextNode getNodeRefresh() {
		return nodeRefresh;
	}
	public TextNode getNodeSettings() {
		return nodeSettings;
	}
	public TextNode getNodeReset() {
		return nodeReset;
	}
}
