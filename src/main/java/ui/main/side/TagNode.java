package ui.main.side;

import base.CustomList;
import base.tag.Tag;
import base.tag.TagUtil;
import control.reload.Reload;
import enums.Direction;
import javafx.geometry.Insets;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.paint.Color;
import main.Root;
import ui.custom.ClickMenu;
import ui.custom.ListMenu;
import ui.decorator.Decorator;
import ui.node.textnode.TextNode;
import ui.override.HBox;
import ui.override.VBox;

public class TagNode extends VBox {
	private static final int PADDING_DEFAULT = 10;
	private static final int PADDING_INCREMENT = 20;
	
	private final TextNode toggleNode;
	private final TextNode textNode;
	private final HBox boxMain;
	
	private final SidePaneBase parentPane;
	private final CustomList<String> levels;
	private final CustomList<TagNode> childrenDirect;
	
	private String stringValue;
	
	private boolean backgroundLock = false;
	
	static {
		ClickMenu.register(TagNode.class, ListMenu.Preset.TAG);
	}
	
	public TagNode(SidePaneBase parentPane, Tag tag, int depth) {
		this.parentPane = parentPane;
		this.childrenDirect = new CustomList<>();
		this.levels = new CustomList<>();
		
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i <= depth; i++) {
			String level = tag.getLevels().get(i);
			levels.addImpl(level);
			sb.append(level);
		}
		stringValue = sb.toString();
		
		toggleNode = new TextNode("+ ", false, false, false, false);
		toggleNode.setPadding(new Insets(0, PADDING_DEFAULT, 0, PADDING_DEFAULT + PADDING_INCREMENT * depth));
		
		textNode = new TextNode(tag.getLevels().get(depth), false, false, false, false);
		
		boxMain = new HBox(toggleNode, textNode);
		this.getChildren().add(boxMain);
		
		if (Root.FILTER.getListManager().isWhitelisted(stringValue)) {
			setTextFill(Decorator.getColorPositive());
		} else if (Root.FILTER.getListManager().isBlacklisted(stringValue)) {
			setTextFill(Decorator.getColorNegative());
		}
		
		boxMain.addEventFilter(MouseEvent.MOUSE_ENTERED, event -> {
			if (!backgroundLock) {
				this.setBackground(Decorator.getBackgroundSecondary());
			}
		});
		boxMain.addEventFilter(MouseEvent.MOUSE_EXITED, event -> {
			if (!backgroundLock) {
				this.setBackground(Background.EMPTY);
			}
		});
		boxMain.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
			switch (event.getButton()) {
				case PRIMARY:
					if (parentPane == Root.FILTER_PANE) {
						clickFilter(event);
					} else if (parentPane == Root.SELECT_PANE) {
						clickSelect(event);
					}
					Reload.start();
					break;
				case SECONDARY:
					TagUtil.setCurrentTagNode(this);
					break;
			}
		});
		
		ClickMenu.install(this, Direction.NONE, MouseButton.SECONDARY);
	}
	
	private void clickFilter(MouseEvent event) {
		if (event.getX() > toggleNode.getWidth()) {
			if (Root.FILTER.getListManager().isWhitelisted(stringValue)) {
				Root.FILTER.getListManager().blacklist(stringValue, getLevels().size());
				setTextFill(Decorator.getColorNegative());
			} else if (Root.FILTER.getListManager().isBlacklisted(stringValue)) {
				Root.FILTER.getListManager().unlist(stringValue);
				setTextFill(Decorator.getColorPrimary());
			} else {
				Root.FILTER.getListManager().whitelist(stringValue, getLevels().size());
				setTextFill(Decorator.getColorPositive());
			}
		} else {
			if (event.isShiftDown()) {
				if (this.isOpen()) {
					this.getSubNodesAll().forEach(TagNode::close);
				} else {
					this.getSubNodesAll().forEach(TagNode::open);
				}
			} else {
				if (this.isOpen()) {
					this.close();
				} else {
					this.open();
				}
			}
		}
	}
	private void clickSelect(MouseEvent event) {
		if (this.isLast()) {
			if (textNode.getTextFill().equals(Decorator.getColorPositive()) || textNode.getTextFill().equals(Decorator.getColorUnion())) {
				Root.SELECT.removeTag(Root.TAGLIST.getTag(this.getStringValue()).getID());
			} else {
				Root.SELECT.addTag(Root.TAGLIST.getTag(this.getStringValue()).getID());
			}
		} else {
			if (event.isShiftDown()) {
				if (this.isOpen()) {
					this.getSubNodesAll().forEach(TagNode::close);
				} else {
					this.getSubNodesAll().forEach(TagNode::open);
				}
			} else {
				if (this.isOpen()) {
					this.close();
				} else {
					this.open();
				}
			}
		}
	}
	
	public void open() {
		if (!isLast()) {
			parentPane.getOpenNodes().addImpl(this);
			this.getChildren().retainAll(boxMain);
			this.getChildren().addAll(childrenDirect);
			toggleNode.setText("− ");
		}
	}
	public void close() {
		if (!isLast()) {
			parentPane.getOpenNodes().remove(this);
			this.getChildren().retainAll(boxMain);
			toggleNode.setText("+ ");
		}
	}
	
	public boolean isOpen() {
		return this.getChildren().size() > 1;
	}
	public boolean isLast() {
		return childrenDirect.isEmpty();
	}
	
	public CustomList<TagNode> getChildrenDirect() {
		return childrenDirect;
	}
	
	public CustomList<TagNode> getSubNodesAll() {
		CustomList<TagNode> subNodes = new CustomList<>();
		getSubNodesAllRecursion(this, subNodes);
		return subNodes;
	}
	private void getSubNodesAllRecursion(TagNode currentNode, CustomList<TagNode> tagNodes) {
		tagNodes.addImpl(currentNode);
		if (!currentNode.isLast()) {
			currentNode.getChildrenDirect().forEach(subNode -> getSubNodesAllRecursion(subNode, tagNodes));
		}
	}
	public CustomList<TagNode> getSubNodesDeepest() {
		CustomList<TagNode> subNodes = new CustomList<>();
		getSubNodesDeepestRecursion(this, subNodes);
		return subNodes;
	}
	private void getSubNodesDeepestRecursion(TagNode currentNode, CustomList<TagNode> tagNodes) {
		if (currentNode.isLast()) {
			tagNodes.addImpl(currentNode);
		} else {
			currentNode.getChildrenDirect().forEach(subNode -> getSubNodesDeepestRecursion(subNode, tagNodes));
		}
	}
	
	public String getStringValue() {
		return stringValue;
	}
	
	public CustomList<TagNode> getParentNodes() {
		TagNode rootNode = getTagNode(parentPane.getRootNodes(), levels.getFirstImpl());
		
		CustomList<TagNode> returnList = new CustomList<>();
		returnList.addImpl(rootNode);
		getParentNodesRecursion(returnList, levels, rootNode, 1);
		
		return returnList;
	}
	private void getParentNodesRecursion(CustomList<TagNode> returnList, CustomList<String> levels, TagNode currentNode, int index) {
		if (levels.size() == index) {
			//done
			return;
		}
		
		TagNode tagNode = getTagNode(currentNode.getChildrenDirect(), levels.get(index));
		if (tagNode != null) {
			returnList.addImpl(tagNode);
			getParentNodesRecursion(returnList, levels, tagNode, ++index);
		}
	}
	private TagNode getTagNode(CustomList<TagNode> list, String stringValue) {
		for (TagNode tagNode : list) {
			if (tagNode.getText().equals(stringValue)) {
				return tagNode;
			}
		}
		return null;
	}
	
	public String getText() {
		return textNode.getText();
	}
	public CustomList<String> getLevels() {
		return levels;
	}
	public TextNode getToggleNode() {
		return toggleNode;
	}
	
	public void setTextFill(Color color) {
		textNode.setTextFill(color);
	}
	public void setBackgroundLock(boolean backgroundLock) {
		this.backgroundLock = backgroundLock;
	}
}
