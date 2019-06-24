package user_interface.main.side;

import control.Filter;
import control.Reload;
import control.Select;
import control.Target;
import database.object.DataObject;
import database.object.TagObject;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import lifecycle.InstanceManager;
import user_interface.main.NodeBase;
import user_interface.nodes.ColorData;
import user_interface.nodes.NodeUtil;
import user_interface.nodes.base.EditNode;
import user_interface.nodes.base.Separator;
import user_interface.nodes.base.TextNode;
import user_interface.nodes.menu.ClickMenuLeft;
import user_interface.style.ColorUtil;
import user_interface.style.SizeUtil;
import user_interface.style.StyleUtil;
import user_interface.style.enums.ColorType;
import utils.enums.Direction;

import java.util.ArrayList;
import java.util.Comparator;

public class SelectPane extends VBox implements NodeBase, SidePane {
	private final TextNode nodeTitle;
	private final ScrollPane scrollPane;
	
	private final EditNode tfSearch;
	private String actualText = "";
	
	private final VBox tagNodes;
	
	private final TextNode nodeSelectAll;
	private final TextNode nodeSelectNone;
	private final TextNode nodeSelectMerge;
	
	public SelectPane() {
		ColorData colorDataSimple = new ColorData(ColorType.DEF, ColorType.ALT, ColorType.DEF, ColorType.DEF);
		
		nodeTitle = new TextNode("", colorDataSimple);
		nodeTitle.setBorder(NodeUtil.getBorder(0, 0, 1, 0));
		nodeTitle.prefWidthProperty().bind(this.widthProperty());
		
		tfSearch = new EditNode("Search tags to add to selection");
		tfSearch.setBorder(NodeUtil.getBorder(0, 0, 1, 0));
		
		nodeSelectAll = new TextNode("Select All", colorDataSimple);
		nodeSelectNone = new TextNode("Select None", colorDataSimple);
		nodeSelectMerge = new TextNode("Merge Selection", colorDataSimple);
		ClickMenuLeft.install(nodeTitle, Direction.LEFT, nodeSelectAll, nodeSelectNone, new Separator(), nodeSelectMerge);
		
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
		this.getChildren().addAll(nodeTitle, tfSearch, scrollPane);
	}
	
	public boolean reload() {
		Select select = InstanceManager.getSelect();
		Target target = InstanceManager.getTarget();
		
		refreshTitle();
		
		Color textColorDefault = ColorUtil.getTextColorDef();
		Color textColorPositive = ColorUtil.getTextColorPos();
		Color textColorShare = ColorUtil.getTextColorShr();
		
		ArrayList<String> groupsInter;
		ArrayList<String> groupsShare;
		if (select.size() == 0) {
			if (target.getCurrentTarget() != null) {
				groupsInter = target.getCurrentTarget().getTagList().getGroups();
				groupsShare = new ArrayList<>();
			} else {
				return false;
			}
		} else {
			groupsInter = select.getIntersectingTags().getGroups();
			groupsShare = select.getSharedTags().getGroups();
		}
		
		refresh();
		for (Node node : tagNodes.getChildren()) {
			if (node instanceof TagNode) {
				TagNode tagNode = (TagNode) node;
				String group = tagNode.getGroup();
				
				if (groupsInter.contains(group)) {
					tagNode.setTextFill(textColorPositive);
				} else if (groupsShare.contains(group)) {
					tagNode.setTextFill(textColorShare);
				} else {
					tagNode.setTextFill(textColorDefault);
				}
				ArrayList<String> namesInter;
				ArrayList<String> namesShare;
				if (select.size() == 0) {
					namesInter = target.getCurrentTarget().getTagList().getNames(group);
					namesShare = new ArrayList<>();
				} else {
					namesInter = select.getIntersectingTags().getNames(group);
					namesShare = select.getSharedTags().getNames(group);
				}
				for (TextNode nameNode : tagNode.getNameNodes()) {
					String name = nameNode.getText();
					
					if (namesInter.contains(name)) {
						nameNode.setTextFill(textColorPositive);
					} else if (namesShare.contains(name)) {
						nameNode.setTextFill(textColorShare);
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
	
	private void refreshTitle() {
		Filter filter = InstanceManager.getFilter();
		Select select = InstanceManager.getSelect();
		
		int hiddenTilesCount = 0;
		for (DataObject dataObject : select) {
			if (!filter.contains(dataObject)) {
				hiddenTilesCount++;
			}
		}
		
		String text = "Selection: " + select.size() + " file(s)";
		if (hiddenTilesCount > 0) {
			text += ", " + hiddenTilesCount + " hidden by filter";
		}
		
		nodeTitle.setText(text);
	}
	
	public void changeNodeState(TagNode tagNode, TextNode nameNode) {
		if (nameNode != null) {
			TagObject tagObject = InstanceManager.getTagListMain().getTagObject(tagNode.getGroup(), nameNode.getText());
			if (nameNode.getTextFill().equals(ColorUtil.getTextColorPos()) || nameNode.getTextFill().equals(ColorUtil.getTextColorShr())) {
				nameNode.setTextFill(ColorUtil.getTextColorDef());
				this.removeTagObjectFromSelection(tagObject);
			} else {
				nameNode.setTextFill(ColorUtil.getTextColorPos());
				this.addTagObjectToSelection(tagObject);
			}
			
			InstanceManager.getReload().flag(Reload.Control.INFO);
			InstanceManager.getReload().doReload();
		}
	}
	public void addTagObjectToSelection(TagObject tagObject) {
		if (InstanceManager.getSelect().size() < 1) {
			DataObject currentTargetedItem = InstanceManager.getTarget().getCurrentTarget();
			if (currentTargetedItem != null) {
				currentTargetedItem.getTagList().add(tagObject);
			}
		} else {
			InstanceManager.getSelect().addTagObject(tagObject);
		}
	}
	public void removeTagObjectFromSelection(TagObject tagObject) {
		if (InstanceManager.getSelect().size() < 1) {
			DataObject currentTargetedItem = InstanceManager.getTarget().getCurrentTarget();
			if (currentTargetedItem != null) {
				currentTargetedItem.getTagList().remove(tagObject);
			}
		} else {
			InstanceManager.getSelect().removeTagObject(tagObject);
		}
	}
	
	public TextField getTfSearch() {
		return tfSearch;
	}
	public String getActualText() {
		return actualText;
	}
	public void setActualText(String actualText) {
		this.actualText = actualText;
	}
	
	public ScrollPane getScrollPane() {
		return scrollPane;
	}
	
	public VBox getTagNodes() {
		return tagNodes;
	}
	
	public TextNode getNodeSelectAll() {
		return nodeSelectAll;
	}
	public TextNode getNodeSelectNone() {
		return nodeSelectNone;
	}
	public TextNode getNodeSelectMerge() {
		return nodeSelectMerge;
	}
}
