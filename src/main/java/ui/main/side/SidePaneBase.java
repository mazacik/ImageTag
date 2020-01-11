package ui.main.side;

import base.CustomList;
import base.tag.Tag;
import base.tag.TagList;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Background;
import ui.decorator.Decorator;
import ui.node.NodeText;
import ui.override.VBox;

public abstract class SidePaneBase extends VBox {
	public static final double MIN_WIDTH_SIDELISTS = 250;
	
	protected final CustomList<TagNode> tagNodesLevel0;
	
	protected NodeText nodeTitle;
	protected VBox boxNodes;
	protected ScrollPane scrollPane;
	
	public SidePaneBase() {
		tagNodesLevel0 = new CustomList<>();
		
		nodeTitle = new NodeText("", true, true, false, true);
		boxNodes = new VBox();
		scrollPane = new ScrollPane(boxNodes);
		scrollPane.setFitToWidth(true);
		scrollPane.setFitToHeight(true);
		scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
		scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
		scrollPane.setBackground(Background.EMPTY);
		
		this.setMinWidth(SidePaneBase.MIN_WIDTH_SIDELISTS);
		this.setPrefWidth(Decorator.getUsableScreenWidth());
	}
	
	public boolean reload() {
		tagNodesLevel0.clear();
		
		for (Tag tag : TagList.getMain()) {
			getTagNodeRecursiveFromLevel0(tag);
		}
		
		boxNodes.getChildren().setAll(tagNodesLevel0);
		
		return true;
	}
	
	private TagNode getTagNodeRecursiveFromLevel0(Tag tag) {
		for (TagNode tagNode : tagNodesLevel0) {
			if (tagNode.getText().equals(tag.getLevel(0))) {
				//child node found, repeat with next level
				return getTagNodeRecursive(tagNode, tag);
			}
		}
		
		//child node not found, needs to be created
		TagNode newNode = new TagNode(this, tag, 0);
		tagNodesLevel0.add(newNode);
		return getTagNodeRecursive(newNode, tag);
	}
	
	private TagNode getTagNodeRecursive(TagNode currentNode, Tag tag) {
		//check if current node is the one we want
		if (currentNode.getStringValue().equals(tag.getStringValue())) {
			return currentNode;
		}
		
		//look for a valid child node
		if (!currentNode.getSubNodes().isEmpty()) {
			String nextLevelString = tag.getLevel(currentNode.getLevel() + 1);
			for (TagNode tagNode : currentNode.getSubNodes()) {
				if (tagNode.getText().equals(nextLevelString)) {
					//child node found, repeat with next level
					return getTagNodeRecursive(tagNode, tag);
				}
			}
		}
		
		//child node not found, needs to be created
		TagNode newNode = new TagNode(this, tag, currentNode.getLevel() + 1);
		currentNode.getSubNodes().add(newNode);
		return getTagNodeRecursive(newNode, tag);
	}
	
	protected void getTagNodes(CustomList<TagNode> tagNodes) {
		getTagNodes(tagNodesLevel0, tagNodes);
	}
	private void getTagNodes(CustomList<TagNode> subNodes, CustomList<TagNode> tagNodes) {
		for (TagNode tagNode : subNodes) {
			tagNodes.add(tagNode);
			getTagNodes(tagNode.getSubNodes(), tagNodes);
		}
	}
	
	//	public boolean reloadOld() {
	//		//	populate primary lists
	//		CustomList<String> groupsWithNodes = new CustomList<>();
	//		TagList tagsWithNodes = new TagList();
	//
	//		for (Node node : boxNodes.getChildren()) {
	//			if (node instanceof GroupNode) {
	//				GroupNode groupNode = (GroupNode) node;
	//				groupsWithNodes.add(groupNode.getGroup(), true);
	//				for (NodeText nameNode : groupNode.getNameNodes()) {
	//					Tag tag = TagList.getMain().getTag(groupNode.getGroup(), nameNode.getText());
	//					if (tag == null) {
	//						//  it's possible the Tag is not in TagList.getMain()
	//						tag = new Tag(groupNode.getGroup(), nameNode.getText());
	//					}
	//					tagsWithNodes.add(tag, true);
	//				}
	//			}
	//		}
	//
	//		//	populate secondary lists
	//		CustomList<Tag> tagsWithoutNodes = new CustomList<>();
	//		for (Tag tagMain : TagList.getMain()) {
	//			if (!tagsWithNodes.contains(tagMain)) {
	//				tagsWithoutNodes.add(tagMain);
	//			}
	//		}
	//		TagList nodesWithoutTags = new TagList();
	//		for (Tag tagCurrent : tagsWithNodes) {
	//			if (!TagList.getMain().contains(tagCurrent)) {
	//				nodesWithoutTags.add(tagCurrent);
	//			}
	//		}
	//
	//		//	check if any changes are necessary (add)
	//		if (!tagsWithNodes.containsAll(TagList.getMain())) {
	//			for (Tag tag : tagsWithoutNodes) {
	//				//	check if the GroupNode exists, if not, add it
	//				String group = tag.getGroup();
	//				int index;
	//				if (!groupsWithNodes.contains(group)) {
	//					groupsWithNodes.add(group);
	//					groupsWithNodes.sort(Comparator.naturalOrder());
	//					index = groupsWithNodes.indexOf(group);
	//					GroupNode groupNode = new GroupNode(this, group);
	//					boxNodes.getChildren().add(index, groupNode);
	//				} else {
	//					index = groupsWithNodes.indexOf(group);
	//				}
	//
	//				//	add NameNode to the respective GroupNode and sort
	//				Node node = boxNodes.getChildren().get(index);
	//				if (node instanceof GroupNode) {
	//					GroupNode groupNode = (GroupNode) node;
	//					groupNode.addNameNode(tag.getName());
	//					groupNode.sort();
	//					groupNode.expand();
	//				}
	//			}
	//		}
	//
	//		//	check if any changes are necessary (remove)
	//		if (!TagList.getMain().containsAll(tagsWithNodes)) {
	//			for (Tag tag : nodesWithoutTags) {
	//				//	use helper to find the GroupNode
	//				//	remove NameNode from the GroupNode
	//				Node node = boxNodes.getChildren().get(groupsWithNodes.indexOf(tag.getGroup()));
	//				if (node instanceof GroupNode) {
	//					GroupNode groupNode = (GroupNode) node;
	//					groupNode.removeNameNode(tag.getName());
	//				}
	//			}
	//		}
	//
	//		return true;
	//	}
	
	public ScrollPane getScrollPane() {
		return scrollPane;
	}
}
