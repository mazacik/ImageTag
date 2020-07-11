package frontend.component.side;

import backend.BaseList;
import backend.misc.Direction;
import backend.reload.Reload;
import backend.tag.Tag;
import backend.tag.TagUtil;
import frontend.UserInterface;
import frontend.decorator.DecoratorUtil;
import frontend.node.menu.ClickMenu;
import frontend.node.menu.ListMenu;
import frontend.node.override.HBox;
import frontend.node.override.VBox;
import frontend.node.textnode.TextNode;
import javafx.geometry.Insets;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.paint.Color;
import main.Main;

public class TagNode extends VBox {
	private static final int PADDING_DEFAULT = 10;
	private static final int PADDING_INCREMENT = 20;
	
	private final TextNode toggleNode;
	private final TextNode textNode;
	private final HBox boxMain;
	
	private final SidePaneBase parentPane;
	private final BaseList<String> levels;
	private final BaseList<TagNode> childrenDirect;
	
	private String stringValue;
	
	private boolean backgroundLock = false;
	
	static {
		ClickMenu.register(TagNode.class, ListMenu.Preset.TAG);
	}
	
	public TagNode(SidePaneBase parentPane, Tag tag, int depth) {
		this.parentPane = parentPane;
		this.childrenDirect = new BaseList<>();
		this.levels = new BaseList<>();
		
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i <= depth; i++) {
			String level = tag.getLevels().get(i);
			levels.add(level);
			sb.append(level);
		}
		stringValue = sb.toString();
		
		toggleNode = new TextNode("+ ", false, false, false, false);
		toggleNode.setPadding(new Insets(0, PADDING_DEFAULT, 0, PADDING_DEFAULT + PADDING_INCREMENT * depth));
		
		textNode = new TextNode(tag.getLevels().get(depth), false, false, false, false);
		
		boxMain = new HBox(toggleNode, textNode);
		this.getChildren().add(boxMain);
		
		if (Main.FILTER.getFilterListManager().isWhitelisted(stringValue)) {
			setTextFill(DecoratorUtil.getColorPositive());
		} else if (Main.FILTER.getFilterListManager().isBlacklisted(stringValue)) {
			setTextFill(DecoratorUtil.getColorNegative());
		}
		
		boxMain.addEventFilter(MouseEvent.MOUSE_ENTERED, event -> {
			if (!backgroundLock) {
				this.setBackground(DecoratorUtil.getBackgroundSecondary());
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
					if (parentPane == UserInterface.getFilterPane()) {
						clickFilter(event);
					} else if (parentPane == UserInterface.getSelectPane().getSelectionTagsPane()) {
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
			if (Main.FILTER.getFilterListManager().isWhitelisted(stringValue)) {
				Main.FILTER.getFilterListManager().blacklist(stringValue, getLevels().size());
				setTextFill(DecoratorUtil.getColorNegative());
			} else if (Main.FILTER.getFilterListManager().isBlacklisted(stringValue)) {
				Main.FILTER.getFilterListManager().unlist(stringValue);
				setTextFill(DecoratorUtil.getColorPrimary());
			} else {
				Main.FILTER.getFilterListManager().whitelist(stringValue, getLevels().size());
				setTextFill(DecoratorUtil.getColorPositive());
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
			if (textNode.getTextFill().equals(DecoratorUtil.getColorPositive()) || textNode.getTextFill().equals(DecoratorUtil.getColorUnion())) {
				Main.SELECT.removeTag(Main.DB_TAG.getTag(this.getStringValue()).getID());
			} else {
				Main.SELECT.addTag(Main.DB_TAG.getTag(this.getStringValue()).getID());
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
			parentPane.getOpenNodes().add(this);
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
	
	public BaseList<TagNode> getChildrenDirect() {
		return childrenDirect;
	}
	
	public BaseList<TagNode> getSubNodesAll() {
		BaseList<TagNode> subNodes = new BaseList<>();
		getSubNodesAllRecursion(this, subNodes);
		return subNodes;
	}
	private void getSubNodesAllRecursion(TagNode currentNode, BaseList<TagNode> tagNodes) {
		tagNodes.add(currentNode);
		if (!currentNode.isLast()) {
			currentNode.getChildrenDirect().forEach(subNode -> getSubNodesAllRecursion(subNode, tagNodes));
		}
	}
	public BaseList<TagNode> getSubNodesDeepest() {
		BaseList<TagNode> subNodes = new BaseList<>();
		getSubNodesDeepestRecursion(this, subNodes);
		return subNodes;
	}
	private void getSubNodesDeepestRecursion(TagNode currentNode, BaseList<TagNode> tagNodes) {
		if (currentNode.isLast()) {
			tagNodes.add(currentNode);
		} else {
			currentNode.getChildrenDirect().forEach(subNode -> getSubNodesDeepestRecursion(subNode, tagNodes));
		}
	}
	
	public String getStringValue() {
		return stringValue;
	}
	
	public BaseList<TagNode> getParentNodes() {
		TagNode rootNode = getTagNode(parentPane.getRootNodes(), levels.getFirst());
		
		BaseList<TagNode> returnList = new BaseList<>();
		returnList.add(rootNode);
		getParentNodesRecursion(returnList, levels, rootNode, 1);
		
		return returnList;
	}
	private void getParentNodesRecursion(BaseList<TagNode> returnList, BaseList<String> levels, TagNode currentNode, int index) {
		if (levels.size() == index) {
			//done
			return;
		}
		
		TagNode tagNode = getTagNode(currentNode.getChildrenDirect(), levels.get(index));
		if (tagNode != null) {
			returnList.add(tagNode);
			getParentNodesRecursion(returnList, levels, tagNode, ++index);
		}
	}
	private TagNode getTagNode(BaseList<TagNode> list, String stringValue) {
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
	public BaseList<String> getLevels() {
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
