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
	public static final double MIN_WIDTH_SIDELISTS = 250;
	
	protected final CustomList<TagNode> tagNodesLevel0;
	
	protected final TextNode nodeTitle;
	protected final VBox boxNodes;
	protected final ScrollPane scrollPane;
	
	protected SidePaneBase() {
		tagNodesLevel0 = new CustomList<>();
		
		nodeTitle = new TextNode("", true, true, false, true);
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
	
	private final CustomList<String> openNodes = new CustomList<>();
	public CustomList<String> getOpenNodes() {
		return openNodes;
	}
	
	public boolean reload() {
		tagNodesLevel0.clear();
		TagList.getMain().forEach(this::createTagNode);
		boxNodes.getChildren().setAll(tagNodesLevel0);
		
		CustomList<String> openNodes = new CustomList<>(this.openNodes);
		this.openNodes.clear();
		for (TagNode tagNode : this.getTagNodesComplete()) {
			if (openNodes.contains(tagNode.getStringValue())) {
				tagNode.open();
			}
		}
		
		return true;
	}
	private TagNode createTagNode(Tag tag) {
		for (TagNode tagNode : tagNodesLevel0) {
			if (tagNode.getText().equals(tag.getLevel(0))) {
				//child node found, repeat with next level
				return tagNode.getSubNode(tag);
			}
		}
		
		//child node not found, needs to be created
		TagNode newNode = new TagNode(this, tag, 0);
		tagNodesLevel0.add(newNode);
		return newNode.getSubNode(tag);
	}
	
	protected CustomList<TagNode> getTagNodesComplete() {
		CustomList<TagNode> returnList = new CustomList<>();
		getTagNodesRecursion(tagNodesLevel0, returnList);
		return returnList;
	}
	private void getTagNodesRecursion(CustomList<TagNode> subNodes, CustomList<TagNode> returnList) {
		for (TagNode tagNode : subNodes) {
			returnList.add(tagNode);
			getTagNodesRecursion(tagNode.getSubNodesDirect(), returnList);
		}
	}
	
	public ScrollPane getScrollPane() {
		return scrollPane;
	}
}
