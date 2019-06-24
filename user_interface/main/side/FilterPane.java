package user_interface.main.side;

import control.Filter;
import database.object.TagObject;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import lifecycle.InstanceManager;
import user_interface.main.NodeBase;
import user_interface.nodes.ColorData;
import user_interface.nodes.NodeUtil;
import user_interface.nodes.base.TextNode;
import user_interface.nodes.menu.ClickMenuLeft;
import user_interface.stage.StageUtil;
import user_interface.stage.Stages;
import user_interface.style.ColorUtil;
import user_interface.style.SizeUtil;
import user_interface.style.StyleUtil;
import user_interface.style.enums.ColorType;
import utils.enums.Direction;

import java.util.ArrayList;
import java.util.Comparator;

public class FilterPane extends VBox implements NodeBase, SidePane {
	private final TextNode nodeTitle;
	private final ScrollPane scrollPane;
	
	private final VBox tagNodes;
	
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
				InstanceManager.getTagListMain().add((TagObject) StageUtil.show(Stages.STAGE_TAG_EDITOR));
				InstanceManager.getTagListMain().sort();
				InstanceManager.getReload().doReload();
			}
		});
		
		tagNodes = NodeUtil.getVBox(ColorType.DEF, ColorType.DEF);
		scrollPane = new ScrollPane();
		scrollPane.setContent(tagNodes);
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
		
		for (Node node : tagNodes.getChildren()) {
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
	public void refresh() {
		ArrayList<String> groupsHere = new ArrayList<>();
		for (Node node : tagNodes.getChildren()) {
			if (node instanceof TagNode) {
				TagNode tagNode = (TagNode) node;
				String group = tagNode.getGroup();
				if (!groupsHere.contains(group)) {
					groupsHere.add(group);
				}
			}
		}
		
		ArrayList<String> groupsThere = new ArrayList<>();
		for (TagObject tagObject : InstanceManager.getTagListMain()) {
			String group = tagObject.getGroup();
			if (!groupsThere.contains(group)) {
				groupsThere.add(group);
			}
		}
		
		//add new groups
		if (!groupsHere.containsAll(groupsThere)) {
			ArrayList<String> groups = new ArrayList<>();
			tagNodes.getChildren().forEach(node -> {
				if (node instanceof TagNode) {
					groups.add(((TagNode) node).getGroup());
				}
			});
			
			for (String groupThere : groupsThere) {
				if (!groupsHere.contains(groupThere)) {
					groups.add(groupThere);
					groups.sort(Comparator.naturalOrder());
					int index = groups.indexOf(groupThere);
					
					TagNode tagNode = new TagNode(this, groupThere);
					tagNodes.getChildren().add(index, tagNode);
				}
			}
			
			StyleUtil.applyStyle(tagNodes);
		}
		
		//remove orphan groups
		if (!groupsThere.containsAll(groupsHere)) {
			for (String groupHere : groupsHere) {
				if (!groupsThere.contains(groupHere)) {
					TagNode tagNode = new TagNode(this, groupHere);
					tagNodes.getChildren().remove(tagNode);
				}
			}
		}
	}
	
	public void updateNameNode(String group, String oldName, String newName) {
		for (Node node : tagNodes.getChildren()) {
			if (node instanceof TagNode) {
				TagNode tagNode = (TagNode) node;
				if (tagNode.getGroup().equals(group)) {
					for (TextNode nameNode : tagNode.getNameNodes()) {
						if (nameNode.getText().equals(oldName)) {
							nameNode.setText(newName);
						}
					}
				}
			}
		}
	}
	public void removeNameNode(String group, String name) {
		ArrayList<TagNode> emptyNodes = new ArrayList<>();
		for (Node node : tagNodes.getChildren()) {
			if (node instanceof TagNode) {
				TagNode tagNode = (TagNode) node;
				if (tagNode.getGroup().equals(group)) {
					String nameToRemove = "";
					for (TextNode nameNode : tagNode.getNameNodes()) {
						if (nameNode.getText().equals(name)) {
							nameToRemove = nameNode.getText();
							break;
						}
					}
					tagNode.removeNameNode(nameToRemove);
				}
				if (tagNode.getNameNodes().isEmpty()) emptyNodes.add(tagNode);
			}
		}
		tagNodes.getChildren().removeAll(emptyNodes);
	}
	
	public void expandAll() {
		for (Node node : tagNodes.getChildren()) {
			if (node instanceof TagNode) {
				TagNode tagNode = (TagNode) node;
				tagNode.showNameNodes();
			}
		}
	}
	public void collapseAll() {
		for (Node node : tagNodes.getChildren()) {
			if (node instanceof TagNode) {
				TagNode tagNode = (TagNode) node;
				tagNode.hideNameNodes();
			}
		}
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
	
	public ScrollPane getScrollPane() {
		return scrollPane;
	}
	
	public VBox getTagNodes() {
		return tagNodes;
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
