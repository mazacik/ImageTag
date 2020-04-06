package ui.main.side;

import base.CustomList;
import base.tag.Tag;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Background;
import main.Root;
import misc.Settings;
import ui.override.VBox;

public abstract class SidePaneBase extends VBox {
	public static final double MIN_WIDTH = 250;
	
	protected final CustomList<TagNode> rootNodes;
	protected final CustomList<TagNode> openNodes;
	
	protected final ScrollPane scrollPane;
	protected final VBox boxNodes;
	
	protected SidePaneBase() {
		rootNodes = new CustomList<>();
		openNodes = new CustomList<>();
		
		boxNodes = new VBox();
		scrollPane = new ScrollPane(boxNodes);
		scrollPane.setFitToWidth(true);
		scrollPane.setFitToHeight(true);
		scrollPane.setMinViewportWidth(MIN_WIDTH);
		scrollPane.setBackground(Background.EMPTY);
		scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
		scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
		scrollPane.addEventFilter(ScrollEvent.SCROLL, this::onScroll);
	}
	
	public boolean reload() {
		rootNodes.clear();
		Root.TAGLIST.forEach(this::createNode);
		boxNodes.getChildren().setAll(rootNodes);
		
		CustomList<String> openNodesHelper = new CustomList<>();
		openNodes.forEach(tagNode -> openNodesHelper.addImpl(tagNode.getStringValue()));
		openNodes.clear();
		for (TagNode tagNode : getTagNodes()) {
			if (openNodesHelper.contains(tagNode.getStringValue())) {
				tagNode.open();
			}
		}
		
		return true;
	}
	
	private void onScroll(ScrollEvent event) {
		event.consume();
		
		double rowHeight;
		if (rootNodes.getFirstImpl() != null) {
			rowHeight = rootNodes.getFirstImpl().getHeight();
		} else {
			rowHeight = Settings.FONT_SIZE.getInteger();
		}
		double contentHeight = boxNodes.getHeight() - scrollPane.getViewportBounds().getHeight();
		double rowToContentRatio = rowHeight / contentHeight;
		double currentVvalue = scrollPane.getVvalue();
		
		if (event.getDeltaY() > 0) {
			//mouse-scroll-up
			scrollPane.setVvalue(currentVvalue - rowToContentRatio);
		} else {
			//mouse-scroll-down
			scrollPane.setVvalue(currentVvalue + rowToContentRatio);
		}
	}
	
	private void createNode(Tag tag) {
		//check root nodes
		for (TagNode tagNode : rootNodes) {
			if (tagNode.getText().equals(tag.getLevels().getFirstImpl())) {
				//root node found, continue with child nodes
				createNodeRecursion(tagNode, tag);
				return;
			}
		}
		
		//root node not found, create
		TagNode rootNode = new TagNode(this, tag, 0);
		rootNodes.addImpl(rootNode);
		
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
		tagNode.getChildrenDirect().addImpl(newNode);
		
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
			returnList.addImpl(tagNode);
			getTagNodesRecursion(tagNode.getChildrenDirect(), returnList);
		}
	}
	
	public CustomList<TagNode> getRootNodes() {
		return rootNodes;
	}
	public CustomList<TagNode> getOpenNodes() {
		return openNodes;
	}
}
