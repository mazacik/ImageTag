package frontend.component.side;

import backend.BaseList;
import backend.tag.Tag;
import frontend.node.ListBox;
import frontend.node.override.VBox;
import main.Main;

public abstract class SidePaneBase extends VBox {
	public static final double MIN_WIDTH = 300;
	
	protected final BaseList<TagNode> rootNodes;
	protected final BaseList<TagNode> openNodes;
	
	protected final ListBox listBox;
	
	protected SidePaneBase() {
		rootNodes = new BaseList<>();
		openNodes = new BaseList<>();
		
		listBox = new ListBox();
	}
	
	public boolean reload() {
		rootNodes.clear();
		Main.DB_TAG.forEach(this::createNode);
		listBox.setNodes(rootNodes);
		
		BaseList<String> openNodesHelper = new BaseList<>();
		openNodes.forEach(tagNode -> openNodesHelper.add(tagNode.getStringValue()));
		openNodes.clear();
		for (TagNode tagNode : getTagNodes()) {
			if (openNodesHelper.contains(tagNode.getStringValue())) {
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
	
	protected BaseList<TagNode> getTagNodes() {
		BaseList<TagNode> returnList = new BaseList<>();
		getTagNodesRecursion(rootNodes, returnList);
		return returnList;
	}
	private void getTagNodesRecursion(BaseList<TagNode> tagNodes, BaseList<TagNode> returnList) {
		for (TagNode tagNode : tagNodes) {
			returnList.add(tagNode);
			getTagNodesRecursion(tagNode.getChildrenDirect(), returnList);
		}
	}
	
	public BaseList<TagNode> getRootNodes() {
		return rootNodes;
	}
	public BaseList<TagNode> getOpenNodes() {
		return openNodes;
	}
}
