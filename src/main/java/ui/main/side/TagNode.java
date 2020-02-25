package ui.main.side;

import base.CustomList;
import base.tag.Tag;
import base.tag.TagList;
import control.Select;
import control.filter.Filter;
import control.reload.Reload;
import javafx.geometry.Insets;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.paint.Color;
import ui.custom.ClickMenu;
import ui.decorator.Decorator;
import ui.node.TextNode;
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
	
	public TagNode(SidePaneBase parentPane, Tag tag, int level) {
		this.parentPane = parentPane;
		this.childrenDirect = new CustomList<>();
		this.levels = new CustomList<>();
		
		for (int i = 0; i <= level; i++) {
			levels.add(tag.getLevels().get(i));
		}
		
		updateStringValue();
		
		int paddingLeft = PADDING_DEFAULT + PADDING_INCREMENT * level;
		
		toggleNode = new TextNode("+ ", false, false, false, false);
		toggleNode.setPadding(new Insets(0, PADDING_DEFAULT, 0, paddingLeft));
		
		textNode = new TextNode(tag.getLevels().get(level), false, false, false, false);
		
		boxMain = new HBox(toggleNode, textNode);
		this.getChildren().add(boxMain);
		
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
					if (parentPane == FilterPane.getInstance()) {
						if (event.getX() > toggleNode.getWidth()) {
							clickFilter();
							Reload.start();
						} else {
							if (!this.isOpen()) {
								this.open();
							} else {
								this.close();
							}
						}
					} else if (parentPane == SelectPane.getInstance()) {
						clickSelect();
						Reload.start();
					}
					break;
				case SECONDARY:
					ClickMenu.setTagNode(this);
					break;
			}
		});
		
		ClickMenu.install(textNode, MouseButton.SECONDARY, ClickMenu.StaticInstance.TAG);
	}
	
	private void clickFilter() {
		if (Filter.getListManager().isWhitelisted(stringValue)) {
			Filter.getListManager().blacklist(stringValue, getLevels().size());
			setTextFill(Decorator.getColorNegative());
		} else if (Filter.getListManager().isBlacklisted(stringValue)) {
			Filter.getListManager().unlist(stringValue);
			setTextFill(Decorator.getColorPrimary());
		} else {
			Filter.getListManager().whitelist(stringValue, getLevels().size());
			setTextFill(Decorator.getColorPositive());
		}
	}
	private void clickSelect() {
		if (this.isLast()) {
			if (textNode.getTextFill().equals(Decorator.getColorPositive()) || textNode.getTextFill().equals(Decorator.getColorUnion())) {
				Select.getEntities().removeTag(TagList.getMain().getTag(this.getStringValue()).getID());
			} else {
				Select.getEntities().addTag(TagList.getMain().getTag(this.getStringValue()).getID());
			}
		} else {
			if (this.isOpen()) {
				this.close();
			} else {
				this.open();
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
	
	public CustomList<TagNode> getChildrenDirect() {
		return childrenDirect;
	}
	public CustomList<TagNode> getSubNodesAll() {
		CustomList<TagNode> subNodes = new CustomList<>();
		getSubNodesRecursion(this, subNodes);
		return subNodes;
	}
	private void getSubNodesRecursion(TagNode currentNode, CustomList<TagNode> tagNodes) {
		if (currentNode.isLast()) {
			tagNodes.add(currentNode);
		} else {
			currentNode.getChildrenDirect().forEach(subNode -> getSubNodesRecursion(subNode, tagNodes));
		}
	}
	
	private void updateStringValue() {
		StringBuilder string = new StringBuilder();
		for (String level : levels) {
			string.append(level);
		}
		stringValue = string.toString();
	}
	public String getStringValue() {
		return stringValue;
	}
	
	public CustomList<TagNode> getParentNodes() {
		TagNode rootNode = getTagNode(parentPane.getRootNodes(), levels.getFirst());
		
		CustomList<TagNode> returnList = new CustomList<>();
		returnList.add(rootNode);
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
			returnList.add(tagNode);
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
