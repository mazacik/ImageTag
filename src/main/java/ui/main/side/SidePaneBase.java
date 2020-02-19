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
	
	protected final TextNode nodeTitle;
	protected final VBox boxNodes;
	protected final ScrollPane scrollPane;
	
	protected SidePaneBase() {
		rootNodes = new CustomList<>();
		
		nodeTitle = new TextNode("", true, true, false, true);
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
	
	private final CustomList<String> openNodes = new CustomList<>();
	public CustomList<String> getOpenNodes() {
		return openNodes;
	}
	
	public boolean reload() {
		rootNodes.clear();
		TagList.getMain().forEach(this::createTagNode);
		boxNodes.getChildren().setAll(rootNodes);
		
		CustomList<String> openNodes = new CustomList<>(this.openNodes);
		this.openNodes.clear();
		for (TagNode tagNode : getTagNodes()) {
			if (openNodes.contains(tagNode.getStringValue())) {
				tagNode.open();
			}
		}
		
		return true;
	}
	private TagNode createTagNode(Tag tag) {
		for (TagNode tagNode : rootNodes) {
			if (tagNode.getText().equals(tag.getLevels().getFirst())) {
				//child node found, repeat with next level
				return tagNode.getSubNode(tag);
			}
		}
		
		//child node not found, needs to be created
		TagNode newNode = new TagNode(this, tag, 0);
		rootNodes.add(newNode);
		return newNode.getSubNode(tag);
	}
	
	protected TagNode getTagNode(String query, int level) {
		for (TagNode tagNode : getTagNodes()) {
			if (tagNode.getLevels().size() - 1 == level && tagNode.getText().toLowerCase().equals(query)) {
				return tagNode;
			}
		}
		return null;
	}
	protected CustomList<TagNode> getTagNodes() {
		CustomList<TagNode> returnList = new CustomList<>();
		getTagNodesRecursion(rootNodes, returnList);
		return returnList;
	}
	private void getTagNodesRecursion(CustomList<TagNode> tagNodes, CustomList<TagNode> returnList) {
		for (TagNode tagNode : tagNodes) {
			returnList.add(tagNode);
			getTagNodesRecursion(tagNode.getSubNodesDirect(), returnList);
		}
	}
	protected CustomList<TagNode> getRootNodes() {
		return rootNodes;
	}
	
	public ScrollPane getScrollPane() {
		return scrollPane;
	}
}
