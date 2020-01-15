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
	private CustomList<TagNode> subNodes;
	
	private TextNode nodeToggle;
	private TextNode textNode;
	private HBox hBoxMain;
	
	private static final int PADDING_DEFAULT = 10;
	private static final int PADDING_INCREMENT = 10;
	
	private final SidePaneBase parentPane;
	
	private CustomList<String> levels;
	public CustomList<String> getLevels() {
		return levels;
	}
	
	private String stringValue;
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
	
	public String getStringValueSeparate() {
		StringBuilder sb = new StringBuilder();
		for (String level : levels) {
			sb.append(level);
			sb.append(" - ");
		}
		sb.delete(sb.length() - 4, sb.length() - 1);
		return sb.toString();
	}
	
	public TagNode(SidePaneBase parentPane, Tag tag, int level) {
		this.parentPane = parentPane;
		this.subNodes = new CustomList<>();
		this.levels = new CustomList<>();
		
		for (int i = 0; i <= level; i++) {
			levels.add(tag.getLevel(i));
		}
		
		updateStringValue();
		
		int paddingLeft = PADDING_DEFAULT + PADDING_INCREMENT * level;
		
		nodeToggle = new TextNode("+ ", false, false, false, false);
		nodeToggle.setPadding(new Insets(0, PADDING_DEFAULT, 0, paddingLeft));
		nodeToggle.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
			switch (event.getButton()) {
				case PRIMARY:
					if (!this.isOpen()) {
						this.open();
					} else {
						this.close();
					}
					break;
				case SECONDARY:
					break;
			}
		});
		
		textNode = new TextNode(tag.getLevel(level), false, false, false, false);
		
		hBoxMain = new HBox(nodeToggle, textNode);
		this.getChildren().add(hBoxMain);
		
		hBoxMain.addEventFilter(MouseEvent.MOUSE_ENTERED, event -> this.setBackground(Decorator.getBackgroundSecondary()));
		hBoxMain.addEventFilter(MouseEvent.MOUSE_EXITED, event -> this.setBackground(Background.EMPTY));
		hBoxMain.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
			switch (event.getButton()) {
				case PRIMARY:
					if (event.getX() > nodeToggle.getWidth()) {
						if (parentPane == PaneFilter.getInstance()) {
							clickFilter();
						} else if (parentPane == PaneSelect.getInstance()) {
							clickSelect();
						}
						Reload.start();
					}
					break;
				case SECONDARY:
					ClickMenu.setTagNode(this);
					break;
			}
		});
		
		ClickMenu.install(hBoxMain, MouseButton.SECONDARY, ClickMenu.StaticInstance.TAG);
	}
	
	private void clickFilter() {
		if (Filter.getListManager().isWhitelisted(this)) {
			Filter.getListManager().blacklist(this);
			setTextFill(Decorator.getColorNegative());
		} else if (Filter.getListManager().isBlacklisted(this)) {
			Filter.getListManager().unlist(this);
			setTextFill(Decorator.getColorPrimary());
		} else {
			Filter.getListManager().whitelist(this);
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
		parentPane.getOpenNodes().add(this.getStringValue());
		
		for (TagNode subNode : subNodes) {
			if (subNode.isLast()) {
				subNode.nodeToggle.setVisible(false);
			}
		}
		
		this.getChildren().retainAll(hBoxMain);
		this.getChildren().addAll(subNodes);
		nodeToggle.setText("− ");
	}
	public void close() {
		parentPane.getOpenNodes().remove(this.getStringValue());
		
		this.getChildren().retainAll(hBoxMain);
		nodeToggle.setText("+ ");
	}
	
	public CustomList<TagNode> getSubNodesDirect() {
		return subNodes;
	}
	public int getLevel() {
		return levels.size() - 1;
	}
	public String getText() {
		return textNode.getText();
	}
	public boolean isOpen() {
		return this.getChildren().size() > 1;
	}
	
	public boolean isLast() {
		return subNodes.isEmpty();
	}
	
	public void setTextFill(Color color) {
		textNode.setTextFill(color);
	}
	
	public TagNode getSubNode(Tag tag) {
		return getSubNode(this, tag);
	}
	private TagNode getSubNode(TagNode currentNode, Tag tag) {
		//check if current node is the one we want
		if (currentNode.getStringValue().equals(tag.getStringValue())) {
			return currentNode;
		}
		
		//look for a valid child node
		if (!currentNode.isLast()) {
			String nextLevelString = tag.getLevel(currentNode.getLevel() + 1);
			for (TagNode tagNode : currentNode.getSubNodesDirect()) {
				if (tagNode.getText().equals(nextLevelString)) {
					//child node found, repeat with next level
					return getSubNode(tagNode, tag);
				}
			}
		}
		
		//child node not found, needs to be created
		TagNode newNode = new TagNode(parentPane, tag, currentNode.getLevel() + 1);
		currentNode.getSubNodesDirect().add(newNode);
		return getSubNode(newNode, tag);
	}
	
	public CustomList<TagNode> getSubNodes() {
		CustomList<TagNode> subNodes = new CustomList<>();
		getSubNodes(this, subNodes);
		return subNodes;
	}
	private void getSubNodes(TagNode currentNode, CustomList<TagNode> tagNodes) {
		if (currentNode.isLast()) {
			tagNodes.add(currentNode);
		} else {
			currentNode.getSubNodesDirect().forEach(subNode -> getSubNodes(subNode, tagNodes));
		}
	}
}
