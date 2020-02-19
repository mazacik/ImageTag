package ui.main.side;

import base.CustomList;
import base.tag.Tag;
import base.tag.TagList;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Background;
import ui.decorator.Decorator;
import ui.node.TextNode;
import ui.override.VBox;

public abstract class SidePaneBase extends VBox {
	public static final double MIN_WIDTH = 250;
	
	protected final CustomList<TagNode> rootNodes;
	protected final CustomList<TagNode> openNodes;
	
	protected final ScrollPane scrollPane;
	protected final TextNode nodeText;
	protected final VBox boxNodes;
	
	protected SidePaneBase() {
		rootNodes = new CustomList<>();
		openNodes = new CustomList<>();
		
		nodeText = new TextNode("", true, true, false, true);
		boxNodes = new VBox();
		scrollPane = new ScrollPane(boxNodes);
		scrollPane.setFitToWidth(true);
		scrollPane.setFitToHeight(true);
		scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
		scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
		scrollPane.setBackground(Background.EMPTY);
		
		this.setMinWidth(SidePaneBase.MIN_WIDTH);
		this.setPrefWidth(Decorator.getUsableScreenWidth());
	}
	
	public boolean reload() {
		rootNodes.clear();
		TagList.getMain().forEach(this::createNode);
		boxNodes.getChildren().setAll(rootNodes);
		
		CustomList<String> openNodes = new CustomList<>();
		this.openNodes.forEach(tagNode -> openNodes.add(tagNode.getStringValue()));
		this.openNodes.clear();
		for (TagNode tagNode : getTagNodes()) {
			if (openNodes.contains(tagNode.getStringValue())) {
				tagNode.open();
			}
		}
		
		return true;
	}
	
	private void createNode(Tag tag) {
		//check root nodes
		for (TagNode tagNode : rootNodes) {
			if (tagNode.getText().equals(tag.getLevels().getFirst())) {
				//root node found, continue with child nodes
				createNodeRecursion(tagNode, tag);
				return;
			}
		}
		
		//root node not found, create
		TagNode rootNode = new TagNode(this, tag, 0);
		rootNodes.add(rootNode);
		
		//continue with child nodes
		createNodeRecursion(rootNode, tag);
	}
	private void createNodeRecursion(TagNode tagNode, Tag tag) {
		//check current node
		if (tagNode.getStringValue().equals(tag.getStringValue())) {
			//done
			tagNode.getToggleNode().setVisible(false);
			return;
		}
		
		//check child nodes
		if (tagNode.getLevels().size() < tag.getLevels().size()) {
			String nextLevelString = tag.getLevels().get(tagNode.getLevels().size()); //getLevel() = 0base; getLevels().size() = (pseudo) 1base
			for (TagNode _tagNode : tagNode.getChildrenDirect()) {
				if (_tagNode.getText().equals(nextLevelString)) {
					//child node found, continue with next level
					createNodeRecursion(_tagNode, tag);
					return;
				}
			}
		}
		
		//child node not found, create
		TagNode newNode = new TagNode(this, tag, tagNode.getLevels().size());
		tagNode.getChildrenDirect().add(newNode);
		
		//continue with continue with next level
		createNodeRecursion(newNode, tag);
	}
	
	protected CustomList<TagNode> getTagNodes() {
		CustomList<TagNode> returnList = new CustomList<>();
		getTagNodesRecursion(rootNodes, returnList);
		return returnList;
	}
	private void getTagNodesRecursion(CustomList<TagNode> tagNodes, CustomList<TagNode> returnList) {
		for (TagNode tagNode : tagNodes) {
			returnList.add(tagNode);
			getTagNodesRecursion(tagNode.getChildrenDirect(), returnList);
		}
	}
	
	public CustomList<TagNode> getRootNodes() {
		return rootNodes;
	}
	public CustomList<TagNode> getOpenNodes() {
		return openNodes;
	}
	
	public ScrollPane getScrollPane() {
		return scrollPane;
	}
}
