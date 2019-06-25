package user_interface.main.side;

import control.Filter;
import control.Reload;
import control.Select;
import control.Target;
import database.list.TagList;
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
import user_interface.nodes.buttons.ButtonFactory;
import user_interface.nodes.buttons.ButtonTemplates;
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
	
	private final VBox tagNodesBox;
	
	private String actualText = "";
	
	public SelectPane() {
		ColorData colorDataSimple = new ColorData(ColorType.DEF, ColorType.ALT, ColorType.DEF, ColorType.DEF);
		
		nodeTitle = new TextNode("", colorDataSimple);
		nodeTitle.setBorder(NodeUtil.getBorder(0, 0, 1, 0));
		nodeTitle.prefWidthProperty().bind(this.widthProperty());
		
		tfSearch = new EditNode("Search tags to add to selection");
		tfSearch.setBorder(NodeUtil.getBorder(0, 0, 1, 0));
		
		ButtonFactory bf = ButtonFactory.getInstance();
		TextNode nodeSelectAll = bf.get(ButtonTemplates.SEL_ALL);
		TextNode nodeSelectNone = bf.get(ButtonTemplates.SEL_NONE);
		TextNode nodeSelectMerge = bf.get(ButtonTemplates.SEL_MERGE);
		ClickMenuLeft.install(nodeTitle, Direction.LEFT, nodeSelectAll, nodeSelectNone, new Separator(), nodeSelectMerge);
		
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
		this.getChildren().addAll(nodeTitle, tfSearch, scrollPane);
	}
	
	public void refresh() {
		TagList tagListMain = InstanceManager.getTagListMain();
		
		//	primary helpers
		ArrayList<String> groupsHere = new ArrayList<>();
		ArrayList<TagObject> tagsMain = new ArrayList<>(InstanceManager.getTagListMain());
		ArrayList<TagObject> tagsHere = new ArrayList<>();
		for (Node node : tagNodesBox.getChildren()) {
			if (node instanceof TagNode) {
				TagNode tagNode = (TagNode) node;
				if (groupsHere.contains(tagNode.getGroup())) groupsHere.add(tagNode.getGroup());
				for (TextNode nameNode : tagNode.getNameNodes()) {
					TagObject tagObject = tagListMain.getTagObject(tagNode.getGroup(), nameNode.getText());
					if (!tagsHere.contains(tagObject)) tagsHere.add(tagObject);
				}
			}
		}
		
		//	secondary helpers
		ArrayList<TagObject> tagsToAdd = new ArrayList<>();
		for (TagObject tagMain : tagsMain) {
			if (!tagsHere.contains(tagMain)) {
				tagsToAdd.add(tagMain);
			}
		}
		ArrayList<TagObject> tagsToRemove = new ArrayList<>();
		for (TagObject tagHere : tagsHere) {
			if (!tagsMain.contains(tagHere)) {
				tagsToRemove.add(tagHere);
			}
		}
		
		//	check whether any changes are necessary (add)
		if (!tagsHere.containsAll(tagsMain)) {
			for (TagObject tagObject : tagsToAdd) {
				//	check if the TagNode exists, if not, add it
				int index = groupsHere.indexOf(tagObject.getGroup());
				if (!groupsHere.contains(tagObject.getGroup())) {
					groupsHere.add(tagObject.getGroup());
					groupsHere.sort(Comparator.naturalOrder());
					TagNode tagNode = new TagNode(this, tagObject.getGroup());
					tagNodesBox.getChildren().add(index, tagNode);
				}
				//	add NameNode to the respective TagNode and sort
				Node node = tagNodesBox.getChildren().get(index);
				if (node instanceof TagNode) {
					TagNode tagNode = (TagNode) node;
					tagNode.addNameNode(tagObject.getName());
					tagNode.sortNameNodes();
				}
			}
			StyleUtil.applyStyle(tagNodesBox);
		}
		
		//	check whether any changes are necessary (remove)
		if (!tagsMain.containsAll(tagsHere)) {
			for (TagObject tagObject : tagsToRemove) {
				//	use helper to find the TagNode
				//	remove NameNode from the TagNode
				Node node = tagNodesBox.getChildren().get(groupsHere.indexOf(tagObject.getGroup()));
				if (node instanceof TagNode) {
					TagNode tagNode = (TagNode) node;
					tagNode.removeNameNode(tagObject.getName());
					//	if TagNode is empty, remove it
					if (tagNode.getNameNodes().isEmpty()) tagNodesBox.getChildren().remove(tagNode);
				}
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
		for (Node node : tagNodesBox.getChildren()) {
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
	
	public void updateNameNode(String group, String oldName, String newName) {
		for (Node node : tagNodesBox.getChildren()) {
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
		for (Node node : tagNodesBox.getChildren()) {
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
		tagNodesBox.getChildren().removeAll(emptyNodes);
	}
	
	public void expandAll() {
		for (Node node : tagNodesBox.getChildren()) {
			if (node instanceof TagNode) {
				TagNode tagNode = (TagNode) node;
				tagNode.showNameNodes();
			}
		}
	}
	public void collapseAll() {
		for (Node node : tagNodesBox.getChildren()) {
			if (node instanceof TagNode) {
				TagNode tagNode = (TagNode) node;
				tagNode.hideNameNodes();
			}
		}
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
			
			InstanceManager.getReload().flag(Reload.Control.TAG);
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
	
	public VBox getTagNodesBox() {
		return tagNodesBox;
	}
	public String getActualText() {
		return actualText;
	}
	public TextField getTfSearch() {
		return tfSearch;
	}
	public ScrollPane getScrollPane() {
		return scrollPane;
	}
	
	public void setActualText(String actualText) {
		this.actualText = actualText;
	}
}
